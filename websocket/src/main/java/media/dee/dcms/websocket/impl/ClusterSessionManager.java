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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import media.dee.dcms.websocket.DistributedTaskService;
import media.dee.dcms.websocket.Session;
import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.distributed.*;
import media.dee.dcms.websocket.distributed.session.LocalSession;
import media.dee.dcms.websocket.distributed.session.RemoteSession;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

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
    private DistributedTaskService distributedTaskService;


    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setLogService(LogService log) {
        this.log = log;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setHazelcastNode(HazelcastInstance instance) {
        this.hazelcastNode = instance;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setDistributedTaskService(DistributedTaskService distributedTaskService) {
        this.distributedTaskService = distributedTaskService;
    }

    @SuppressWarnings("unused")
    @Activate
    void activate(){
        //will be used at AbstractTask
        hazelcastNode.getUserContext().put(this.getClass().getName(), this);
    }

    @Override
    public synchronized Session sessionConnected(org.eclipse.jetty.websocket.api.Session session) {
        Member member = hazelcastNode.getCluster().getLocalMember();
        LocalSession sessionWrapper = new LocalSession(session, member);

        sessionIndex.put(sessionWrapper.getId(), sessionWrapper);
        localSessions.put(session, sessionWrapper);

        AbstractTask task = new SessionConnected(sessionWrapper);
        Set<String> exclude = Collections.singleton(sessionWrapper.getMemberId());
        distributedTaskService.broadcast(task, exclude);

        return sessionWrapper;
    }

    @Override
    public synchronized Session sessionClosed(org.eclipse.jetty.websocket.api.Session session) {

        Session sessionWrapper = get(session);
        if( sessionWrapper == null ){
            log.log(LogService.LOG_DEBUG, String.format("Asynchronized websocket session in %s", getClass().getName()));
            return null;
        }

        LocalSession localSession = (LocalSession)sessionIndex.remove(sessionWrapper.getId());
        localSessions.remove(session);

        AbstractTask task = new FrontendSessionClose(new RemoteSession(this, localSession));
        Set<String> exclude = Collections.singleton(sessionWrapper.getMemberId());
        distributedTaskService.broadcast(task, exclude);

        return sessionWrapper;

    }

    public synchronized Session sessionClosed(RemoteSession session) {
        Session clusterSession = sessionIndex.remove(session.getId());

        if( clusterSession instanceof LocalSession){
            log.log(LogService.LOG_DEBUG, String.format("Unauthorized session removal session id: %s", session.getId() ));
            return null;
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
        CompletableFuture<Void> future = new CompletableFuture<>();
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
    public void broadcast(JsonNode message) {
        AbstractTask task = new BroadcastMessage(message);
        distributedTaskService.broadcast(task);
    }

    /**
     * add a remote session to the current node.
     * @param session: remote session.
     */
    @Override
    public void addSession(RemoteSession session) {
        sessionIndex.computeIfPresent(session.getId(), (id, currentSession)->{
            if( currentSession instanceof LocalSession)
                return currentSession;
            return session; //replace with new session.
        });
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
    public long send(JsonNode message) {
        return localSessions
                .values()
                .parallelStream()
                .map( s -> this.send( s, message) )
                .filter( b -> b )
                .count();
    }

    public DistributedTaskService getDistributedTaskService() {
        return distributedTaskService;
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
        AbstractTask task = new SendAcknowledgeMessage(id);
        Session session = sessionIndex.get(id);
        distributedTaskService.sendToMember(task, session.getMemberId());
    }
}
