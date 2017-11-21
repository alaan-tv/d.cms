package media.dee.dcms.web.launcher.websocket;

import media.dee.dcms.websocket.WebSocketDispatcher;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/*")
public class WebSocketProxy {

    private WebSocketDispatcherTracker dispatcherTracker;

    @PostConstruct
    public void init(){
        dispatcherTracker = new WebSocketDispatcherTracker(null);
    }
    @OnOpen
    public void open(Session session) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.open(session);
    }

    @OnClose
    public void close(Session session) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.close(session);
    }

    @OnError
    public void onError(Throwable error) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.onError(error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.handleMessage(message, session);
    }
}
