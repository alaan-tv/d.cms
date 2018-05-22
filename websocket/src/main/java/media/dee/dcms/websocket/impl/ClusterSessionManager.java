/*
 * Copyright (c) 2002-2018 "dee media"
 *
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package media.dee.dcms.websocket.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.instance.GroupProperties;
import media.dee.dcms.websocket.Session;
import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.messages.*;
import media.dee.dcms.websocket.impl.session.LocalSession;
import media.dee.dcms.websocket.impl.session.RemoteSession;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Session Manager Service implementation over Hazelcast lib to support clustered session websocket.
 */
@Component(scope = ServiceScope.PROTOTYPE)
public class ClusterSessionManager implements SessionManager {

    private final Map<String, Session> sessionIndex = new HashMap<>();
    private final Map<org.eclipse.jetty.websocket.api.Session, Session> localSessions = new HashMap<>();
    private final Map<String, Consumer<Void>> callbackMap = new HashMap<>();
    private LogService log;
    private HazelcastInstance hazelcastNode;
    private ITopic<Message> hazelcastTopic;


    private void dispatchWSMessage(com.hazelcast.core.Message<Message> message){
        Message msg = message.getMessageObject();
        msg.dispatch(this);
    }

    @Reference
    void setLogService(LogService log) {
        this.log = log;
    }

    @SuppressWarnings("unused")
    @Activate
    void activate(){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());

        Config config = new Config();
        config.setProperty(GroupProperties.PROP_VERSION_CHECK_ENABLED, "false");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
        config.setClassLoader(this.getClass().getClassLoader());

        hazelcastNode = Hazelcast.newHazelcastInstance(config);
        hazelcastTopic = hazelcastNode.getTopic(String.format("ws:%s-%s", bundle.getSymbolicName(), bundle.getVersion()));
        hazelcastTopic.addMessageListener( this::dispatchWSMessage );
    }

    @SuppressWarnings("unused")
    @Deactivate
    void deactivate(){
        hazelcastNode.shutdown();
    }


    @Override
    public synchronized Session sessionConnected(org.eclipse.jetty.websocket.api.Session session) {
        LocalSession sessionWrapper = new LocalSession(session);

        sessionIndex.put(sessionWrapper.getId(), sessionWrapper);
        localSessions.put(session, sessionWrapper);

        hazelcastTopic.publish(new SessionConnected(sessionWrapper));

        return sessionWrapper;
    }

    @Override
    public synchronized Session sessionClosed(org.eclipse.jetty.websocket.api.Session session) {

        Session sessionWrapper = get(session);
        if( sessionWrapper == null ){
            log.log(LogService.LOG_ERROR, String.format("Unsynchroinzed websocket session in %s", getClass().getName()));
            throw new RuntimeException(String.format("Unsynchroinzed websocket session in %s", getClass().getName()));
        }

        LocalSession localSession = (LocalSession)sessionIndex.remove(sessionWrapper.getId());
        localSessions.remove(session);
        hazelcastTopic.publish(new SessionClose(new RemoteSession( this, localSession )));

        return sessionWrapper;

    }

    public synchronized Session sessionClosed(RemoteSession session) {
        if( !sessionIndex.containsKey(session.getId()) ){
            log.log(LogService.LOG_ERROR, String.format("Unsynchroinzed cluster websocket session in %s, id:%s", getClass().getName(), session.getId()));
            throw new RuntimeException(String.format("Unsynchroinzed cluster websocket session in %s, id:%s", getClass().getName(), session.getId()));
        }

        Session clusterSession = sessionIndex.remove(session.getId());

        if( clusterSession instanceof LocalSession){
            log.log(LogService.LOG_ERROR, String.format("Unauthorized session removal session id: %s", session.getId() ));
            throw new RuntimeException(String.format("Unauthorized session removal session id: %s", session.getId() ));
        }

        return session;

    }

    @Override
    public synchronized Session get(org.eclipse.jetty.websocket.api.Session session) {
        return localSessions.get(session);
    }

    @Override
    public Session get(String id) {
        return sessionIndex.get(id);
    }


    @Override
    public boolean send(Session session, JsonNode message){
        try {
            session.send(message);
            return true;
        } catch (IOException e) {
            Session clusterSession = get(session.getId());
            if( clusterSession instanceof LocalSession){
                LocalSession localSession = (LocalSession) clusterSession;
                sessionClosed(localSession.getSession());
            }
            return false;
        }
    }

    @Override
    public Future<Void> send(String sessionID, JsonNode message) {
        /* dispachter of SendMessage, so only local session should be served, remote message is passed without errors. */
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        Session session = get(sessionID);
        if( session instanceof LocalSession){
            if( send(session, message) )
                future.complete(null);
            else
                future.obtrudeException(new IOException("Websocket IOException"));
            return future;
        }
        future.complete(null);
        return future;
    }

    @Override
    public void broadcast(JsonNode message, Predicate<Map<String, Object>> filter) {
        hazelcastTopic.publish(new BroadcastMessage(message, filter));
    }

    @Override
    public void addSession(RemoteSession session) {
        sessionIndex.put(session.getId(), session);
    }

    @Override
    public boolean closeSession(RemoteSession session) {
        Session clusterSession = get(session.getId());
        if( clusterSession instanceof LocalSession){
            LocalSession localSession = (LocalSession) clusterSession;
            localSession.close();
            sessionClosed(localSession.getSession());
            return true;
        }

        return false;
    }

    @Override
    public long send(JsonNode message, Predicate<Map<String, Object>> filter) {
        Stream<Session> stream = localSessions
                .values()
                .parallelStream();

        if( filter != null )
            stream = stream.filter( s -> filter.test(s.getAttributes() ));

        return stream
                .map( s -> this.send( s, message) )
                .filter( b -> b )
                .count();
    }

    public ITopic<Message> getTopic() {
        return hazelcastTopic;
    }

    /**
     * register a callback by id to be called when another node send a message.
     * @param id: message id
     * @param complete: callback when the another node finishes the job.
     */
    public void registerMessageCallback(String id, Consumer<Void> complete){
        callbackMap.put(id, complete);
    }

    /**
     * message acknowledge is triggered by remote node in the cluster
     * @param id message id.
     * */
    public void messageAcknowledged(String id){
        Consumer<Void> consumer = callbackMap.get(id);
        if( consumer != null )
            consumer.accept(null);

    }

    /**
     * send acknowledgement message back to callee by message id
     * @param id: message id that acknowledged
     */
    public void sendAcknowledgeMessage(String id){
        this.hazelcastTopic.publish(new SendAcknowledgeMessage(id));
    }
}
