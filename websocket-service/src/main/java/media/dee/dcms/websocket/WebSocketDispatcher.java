package media.dee.dcms.websocket;

import javax.websocket.*;

public interface WebSocketDispatcher {
    void open(String path, Session session);
    void close(String path, Session session);
    void onError(String path, Throwable error);
    void handleMessage(String path, String message, Session session);
}
