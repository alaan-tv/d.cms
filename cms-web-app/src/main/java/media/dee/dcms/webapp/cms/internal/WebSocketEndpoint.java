package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.websocket.WebSocketService;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@ServerEndpoint("cms")
public class WebSocketEndpoint implements media.dee.dcms.websocket.WebSocketEndpoint{

    private Map<String, Session> sessionMap = new HashMap<>();
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private ServiceTracker<ComponentConnector, ComponentConnector> connectorServiceTracker;

    public void bindLong( LogService log ) {
        logRef.set(log);
    }

    public void unbindLong( LogService log ) {
        logRef.compareAndSet(log, null);
    }

    public void activate(ComponentContext ctx){
        connectorServiceTracker = new ServiceTracker<ComponentConnector, ComponentConnector>(ctx.getBundleContext(), ComponentConnector.class, null);
        connectorServiceTracker.open();
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    public void deactivate(ComponentContext ctx){
        if( connectorServiceTracker != null )
            connectorServiceTracker.close();
    }

    public void bindWebSocketService(WebSocketService wsService) {
        try {
            wsService.addEndpoint(this);
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }

    public void unbindWebSocketService( WebSocketService wsService ) {
        try {
            wsService.addEndpoint(this);
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    public Future<Void> sendMessageAsync(Session session, JSONObject message){
        return session.getAsyncRemote().sendText(message.toString());
    }

    public Future<Void> sendMessageAsync(Session session, String message){
        return session.getAsyncRemote().sendText(message);
    }

    public void sendMessage(Session session, JSONObject message){
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
            sessionMap.remove(session.getId());
        }
    }

    public void sendMessage(Session session, String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            sessionMap.remove(session.getId());
        }
    }

    protected void sendAll(JSONObject message){
        String txtMessage = message.toString();
        sessionMap.values()
                .parallelStream()
                .forEach( session -> sendMessageAsync(session, txtMessage));
    }

    @Override
    public void open(String path, Session session) {
        sessionMap.put(session.getId(), session);
        connectorServiceTracker.getService().newSession(session);
    }

    @Override
    public void close(String path, Session session) {
        sessionMap.remove(session.getId());
    }

    @Override
    public void onError(String path, Throwable error) {
    }

    @Override
    public void handleMessage(String path, String message, Session session) {

    }
}
