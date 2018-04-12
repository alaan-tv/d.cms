package media.dee.dcms.websocket;

import javax.websocket.Session;

public interface WebSocketEndpoint {
    void open(String path, Session session);

    void close(String path, Session session);

    void onError(String path, Throwable error);

    void handleMessage(String path, String message, Session session);
}
