package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.websocket.WebSocketService;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.LogService;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@ServerEndpoint("cms")
@Component(immediate = true)
public class WebSocketEndpoint implements media.dee.dcms.websocket.WebSocketEndpoint{

    private Map<String, Session> sessionMap = new HashMap<>();
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final List<IComponentConnector> componentConnectors = new LinkedList<>();

    @Reference
    void setLogService( LogService log ) {
        logRef.set(log);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "unbindComponentConnector")
    void bindComponentConnector(IComponentConnector componentConnector){
        synchronized (this.componentConnectors) {
            this.componentConnectors.add(componentConnector);
        }
    }

    void unbindComponentConnector(IComponentConnector componentConnector){
        synchronized (this.componentConnectors) {
            this.componentConnectors.remove(componentConnector);
        }
    }


    @Activate
    public void activate(ComponentContext ctx){
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    @Deactivate
    public void deactivate(ComponentContext ctx){
    }

    @Reference(unbind = "unbindWebSocketService", cardinality = ReferenceCardinality.AT_LEAST_ONE)
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
        this.componentConnectors.forEach( connector -> connector.newSession(session));
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
