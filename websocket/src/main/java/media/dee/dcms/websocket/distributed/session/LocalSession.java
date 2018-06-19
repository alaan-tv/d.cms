package media.dee.dcms.websocket.distributed.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.core.Member;
import media.dee.dcms.websocket.DistributedTaskService;
import media.dee.dcms.websocket.distributed.AbstractTask;
import media.dee.dcms.websocket.distributed.SessionAttributesChanged;
import media.dee.dcms.websocket.impl.ClusterSessionManager;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Websocket session directly connected ( locally ), all communication to client can be done via Jetty Websocket session.
 */
public class LocalSession implements media.dee.dcms.websocket.Session {

    private transient DistributedTaskService distributedTaskService;
    private transient Session session;

    private Map<String, Serializable> attributes = Collections.synchronizedMap(new HashMap<>());
    private String id;
    private String memberId;

    public LocalSession(Session session, Member member, ClusterSessionManager sessionManager){
        this.session = session;
        this.id = UUID.randomUUID().toString();
        this.memberId = member.getUuid();
        distributedTaskService = sessionManager.getDistributedTaskService();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getMemberId() {
        return memberId;
    }

    @Override
    public void send(JsonNode json) throws IOException {
        this.session.getRemote().sendString(json.toString());
    }

    @Override
    public Future<Void> sendByFuture(JsonNode json) {
        return this.session.getRemote().sendStringByFuture(json.toString());
    }

    @Override
    public Map<String, Serializable> getAttributes() {
        return new HashMap<>(attributes);
    }

    @Override
    public void putAttributesWithSync(Map<String, Serializable> map) {
        this.attributes.putAll(map);

        RemoteSession wrapper = new RemoteSession(this);
        AbstractTask task = new SessionAttributesChanged(wrapper);
        distributedTaskService.broadcast(task, this.getMemberId());
    }

    @Override
    public void putAttributes(Map<String, Serializable> map) {
        this.attributes.putAll(map);
    }

    @Override
    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            /* ignore */
        }
    }

    @Override
    public String getProtocolVersion() {
        return session.getProtocolVersion();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return session.getRemoteAddress();
    }

    @Override
    public boolean isSecure() {
        return session.isSecure();
    }

    public Session getSession(){
        return session;
    }
}
