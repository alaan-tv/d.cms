package media.dee.dcms.websocket.impl.session;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.Future;

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
    public void sendBytes(ByteBuffer byteBuffer) throws IOException {
        this.session.getRemote().sendBytes(byteBuffer);
    }

    @Override
    public Future<Void> sendBytesByFuture(ByteBuffer byteBuffer) {
        return this.session.getRemote().sendBytesByFuture(byteBuffer);
    }

    @Override
    public void sendString(String string) throws IOException {
        this.session.getRemote().sendString(string);
    }

    @Override
    public Future<Void> sendStringByFuture(String string) {
        return this.session.getRemote().sendStringByFuture(string);
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
