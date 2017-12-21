package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.websocket.WebSocketService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import javax.json.*;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ServerEndpoint("cms")
@Component(immediate = true, property= EventConstants.EVENT_TOPIC + "=transport")
public class WebSocketEndpoint implements media.dee.dcms.websocket.WebSocketEndpoint, EventHandler{

    private Map<String, Session> sessionMap = new HashMap<>();
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final List<IComponentConnector> componentConnectors = new LinkedList<>();
    private EventAdmin eventAdmin;

    @Reference
    void setEventAdmin( EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

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

    public <T extends JsonObject> Future<Void> sendMessageAsync(Session session, T message){
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeObject(message);
        writer.close();
        return session.getAsyncRemote().sendText(stringWriter.toString());
    }


    public <T extends JsonObject> void sendMessage(Session session, T message){
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter writer = Json.createWriter(stringWriter);
            writer.writeObject(message);
            writer.close();

            session.getBasicRemote().sendText(stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
            sessionMap.remove(session.getId());
        }
    }



    protected <T extends JsonObject> void sendAll(T message){
        sessionMap.values()
                .parallelStream()
                .forEach( session -> sendMessageAsync(session, message));
    }

    private void sendWelcomeMessage(Session session){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("action", "system.info")
                .add("SymbolicName", bundle.getSymbolicName() )
                .add("Version", bundle.getVersion().toString() )
                .build();
        sendMessage(session, jsonObject);
    }

    @Override
    public void open(String path, Session session) {
        sessionMap.put(session.getId(), session);
        sendWelcomeMessage(session);
        this.componentConnectors.forEach( connector -> connector.newSession(session));
    }

    @Override
    public void close(String path, Session session) {
        sessionMap.remove(session.getId());
    }

    @Override
    public void onError(String path, Throwable error) {
        logRef.get().log(LogService.LOG_ERROR, "WebSocket Error", error);
    }

    @Override
    public void handleMessage(String path, String message, Session session) {
        JsonReader reader = Json.createReader(new StringReader(message));
        JsonObject jsonMsg = reader.readObject();

        Hashtable<String,Object> dict = new Hashtable<>();
        Consumer<JsonValue> responseFunction = (msg) -> {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("action", String.format("response:data:%s", jsonMsg.getInt("requestID") ));
            response.add("response", msg);
            sendMessage(session, response.build() );
        };
        Consumer<JsonObject> basicResponse = (msg) -> {
            sendMessage(session, msg );
        };
        dict.put("response",  responseFunction );
        dict.put("basicResponse",  basicResponse );
        dict.put("message",  jsonMsg );
        Event event = new Event( jsonMsg.getString("action"), dict );
        eventAdmin.postEvent(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleEvent(Event event) {
        Consumer<JsonObject> response = (Consumer<JsonObject>) event.getProperty("basicResponse");
        JsonObject message = (JsonObject) event.getProperty("message");
        response.accept(message);
    }
}
