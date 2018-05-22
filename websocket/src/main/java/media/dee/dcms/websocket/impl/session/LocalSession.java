package media.dee.dcms.websocket.impl.session;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Websocket session directly connected ( locally ), all communication to client can be done via Jetty Websocket session.
 */
public class LocalSession implements media.dee.dcms.websocket.Session {

    private transient Session session;
    private final Map<String, Object> attributes = new HashMap<>();
    private String id;

    public LocalSession(Session session){
        this.session = session;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return this.id;
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
