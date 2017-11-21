package media.dee.dcms.web.launcher.websocket;

import media.dee.dcms.websocket.WebSocketDispatcher;
import org.osgi.framework.BundleContext;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static media.dee.dcms.web.launcher.bridge.BundleContextFilter.BUNDLE_CONTEXT_THREAD_LOCAL;

@ServerEndpoint(value = "/{path}")
public class WebSocketProxy {

    private WebSocketDispatcherTracker dispatcherTracker;

    public WebSocketProxy(){
        BundleContext bundleContext = BUNDLE_CONTEXT_THREAD_LOCAL.get();
        dispatcherTracker = new WebSocketDispatcherTracker(bundleContext);
        dispatcherTracker.open();
    }
    @OnOpen
    public void open(Session session, @PathParam("path") String path) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.open(path, session);
    }

    @OnClose
    public void close(Session session, @PathParam("path") String path) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.close(path, session);
    }

    @OnError
    public void onError(Throwable error, @PathParam("path") String path) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.onError(path, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session, @PathParam("path") String path) {
        WebSocketDispatcher dispatcher = dispatcherTracker.getService();
        if( dispatcher != null )
            dispatcher.handleMessage(path, message, session);
    }

    @Override
    protected void finalize() throws Throwable {
        dispatcherTracker.close();
    }
}
