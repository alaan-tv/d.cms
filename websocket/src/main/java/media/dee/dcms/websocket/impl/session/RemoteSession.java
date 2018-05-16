package media.dee.dcms.websocket.impl.session;

import com.hazelcast.core.ITopic;
import media.dee.dcms.websocket.Session;
import media.dee.dcms.websocket.impl.messages.CloseSession;
import media.dee.dcms.websocket.impl.messages.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public class RemoteSession implements Session,Serializable {
    private transient ITopic<Message> topic;
    private String id;
    private String protocolVersion;
    private InetSocketAddress remoteAddress;
    private boolean secure;

    public RemoteSession(ITopic<Message> topic, LocalSession session){
        this.topic = topic;
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
    public void close() {
        topic.publish(new CloseSession(this));
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
    public void sendBytes(ByteBuffer data) throws IOException {
        //TODO implement
    }

    @Override
    public Future<Void> sendBytesByFuture(ByteBuffer data) {
        //TODO implement
        return null;
    }

    @Override
    public void sendString(String text) throws IOException {
        //TODO implement
    }

    @Override
    public Future<Void> sendStringByFuture(String text) {
        //TODO implement
        return null;
    }

    public void setTopic(ITopic<Message> topic) {
        this.topic = topic;
    }
}
