package media.dee.dcms.websocket.impl.session;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Websocket session directly connected ( locally ), all communication to client can be done via Jetty Websocket session.
 */
public class LocalSession implements media.dee.dcms.websocket.Session {

    private transient Session session;
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
