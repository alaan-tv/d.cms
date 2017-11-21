package media.dee.dcms.websocket;

import javax.websocket.*;

public interface WebSocketDispatcher {
    @OnOpen
    void open(Session session);
    @OnClose
    void close(Session session);
    @OnError
    void onError(Throwable error);
    @OnMessage
    void handleMessage(String message, Session session);
}
