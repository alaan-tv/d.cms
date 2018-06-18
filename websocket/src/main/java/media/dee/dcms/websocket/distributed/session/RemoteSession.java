package media.dee.dcms.websocket.distributed.session;

import com.fasterxml.jackson.databind.JsonNode;
import media.dee.dcms.websocket.Session;
import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.distributed.AbstractTask;
import media.dee.dcms.websocket.distributed.BackendSessionClose;
import media.dee.dcms.websocket.distributed.SendMessage;
import media.dee.dcms.websocket.impl.ClusterSessionManager;
import media.dee.dcms.websocket.DistributedTaskService;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Websocket session created in a different node of the cluster, all communcation to the client should be managed through cluster api ( Hazelcast ) to the connected node to delegate the communication to the client.
 */
public class RemoteSession implements Session,Serializable {
    /**
     * DistributedTaskService to send messages to all cluster nodes
     * transient to avoid serialization over the cluster api.
     */
    private transient DistributedTaskService distributedTaskService;
    private transient ClusterSessionManager sessionManager;

    private final Map<String, Object> attributes = new HashMap<>();
    private String id;
    private String memberId;
    private String protocolVersion;
    private InetSocketAddress remoteAddress;
    private boolean secure;

    public RemoteSession(ClusterSessionManager sessionManager, LocalSession session){
        this.distributedTaskService = sessionManager.getDistributedTaskService();
        this.sessionManager = sessionManager;
        this.id = session.getId();
        this.memberId = session.getMemberId();
        this.protocolVersion = session.getProtocolVersion();
        this.remoteAddress = session.getRemoteAddress();
        this.secure = session.isSecure();
    }

    /**
     * construct a remote session from local session to be serialized over the cluster apis.
     * @param session the local session to create a remote session from.
     */
    public RemoteSession(LocalSession session){
        this.sessionManager = null;
        this.id = session.getId();
        this.protocolVersion = session.getProtocolVersion();
        this.remoteAddress = session.getRemoteAddress();
        this.secure = session.isSecure();
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
    public void close() {
        AbstractTask task = new BackendSessionClose(this);
        distributedTaskService.broadcast(task);
    }

    @Override
    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void send(JsonNode json){
        AbstractTask task = new SendMessage(id, json);
        distributedTaskService.broadcast(task, this.getMemberId());
    }

    @Override
    public Future<Void> sendByFuture(JsonNode json) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        UUID uuid = UUID.randomUUID();
        sessionManager.registerMessageCallback(uuid.toString(), completableFuture::complete);

        AbstractTask task = new SendMessage(id, json, uuid);
        distributedTaskService.broadcast(task, this.getMemberId());
        return completableFuture;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    @Override
    public void setAttributes(Map<String, Object> map){
        List<Map.Entry<String, Map>> changes = new LinkedList<>();
        synchronized (this.attributes ){
            //TODO calculate changes
            this.attributes.clear();
            this.attributes.putAll(map);
        }

        //TODO send session's attribute changes message over the cluster to synchronize session attributes. avoid message cycling.
    }

    public void setSessionManager(SessionManager sessionManager) {
        // TODO: 6/9/18 we shouldn't use class cast here
        this.sessionManager = (ClusterSessionManager) sessionManager;
        this.distributedTaskService = this.sessionManager.getDistributedTaskService();
    }
}
