package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.components.UUID;
import media.dee.dcms.components.WebComponent;
import media.dee.dcms.websocket.WebSocketService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import javax.json.*;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ServerEndpoint("cms")
@Component(immediate = true, property = EventConstants.EVENT_TOPIC + "=transport")
public class WebSocketEndpoint implements media.dee.dcms.websocket.WebSocketEndpoint, EventHandler {

    private Map<String, Session> sessionMap = new HashMap<>();
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final List<IComponentConnector> componentConnectors = new LinkedList<>();
    private final Map<String, WebComponent.Command> commandMap = new Hashtable<>();
    static private final WebComponent.Command errorCommand = new WebComponent.Command() {
        @Override
        public JsonValue execute(JsonValue... arguments) {
            return Json.createObjectBuilder()
                    .add("error", "not-fount")
                    .build();
        }
    };


    @Reference
    void setLogService(LogService log) {
        logRef.set(log);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "unbindComponentConnector")
    void bindComponentConnector(IComponentConnector componentConnector) {
        synchronized (this.componentConnectors) {
            this.componentConnectors.add(componentConnector);
        }
    }

    void unbindComponentConnector(IComponentConnector componentConnector) {
        synchronized (this.componentConnectors) {
            this.componentConnectors.remove(componentConnector);
        }
    }

    private String getCommandName(WebComponent.Command command) {
        Bundle bundle = FrameworkUtil.getBundle(command.getClass());
        WebComponent.Command.For forAnnotation = command.getClass().getAnnotation(WebComponent.Command.For.class);
        ShortCommandName shortCommandName = command.getClass().getAnnotation(ShortCommandName.class);
        UUID uuid = forAnnotation == null ? null : forAnnotation.component().getAnnotation(UUID.class);
        return String.format(
                shortCommandName != null ? shortCommandName.value() : "component/%s/%s/%s/%s",
                bundle.getSymbolicName(),
                bundle.getVersion(),
                uuid == null ? "" : uuid.value(),
                forAnnotation == null ? "" : forAnnotation.value()
        );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "unbindCommand")
    void bindCommand(WebComponent.Command command) {
        synchronized (this.commandMap) {
            String cmdName = getCommandName(command);
            WebComponent.Command cmd = this.commandMap.putIfAbsent(cmdName, command);
            if (cmd != null)
                logRef.get().log(LogService.LOG_ERROR, String.format("Command[%s] is already registered", cmdName));
        }
    }

    void unbindCommand(WebComponent.Command command) {
        synchronized (this.commandMap) {
            this.commandMap.remove(getCommandName(command));
        }
    }


    @Activate
    public void activate(ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    @Deactivate
    public void deactivate(ComponentContext ctx) {
    }

    @Reference(unbind = "unbindWebSocketService", cardinality = ReferenceCardinality.AT_LEAST_ONE)
    public void bindWebSocketService(WebSocketService wsService) {
        try {
            wsService.addEndpoint(this);
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }

    public void unbindWebSocketService(WebSocketService wsService) {
        wsService.removeEndpoint(this);
    }

    private <T extends JsonStructure> Future<Void> sendMessageAsync(final Session session, T message) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.write(message);
        writer.close();
        synchronized (session) {
            return session.getAsyncRemote().sendText(stringWriter.toString());
        }
    }


    private <T extends JsonStructure> void sendMessage(final Session session, T message) {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter writer = Json.createWriter(stringWriter);
            writer.write(message);
            writer.close();
            synchronized (session) {
                session.getBasicRemote().sendText(stringWriter.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (!session.isOpen())
                sessionMap.remove(session.getId());
        }
    }


    protected <T extends JsonObject> void sendAll(T message) {
        sessionMap.values()
                .parallelStream()
                .forEach(session -> sendMessageAsync(session, message));
    }

    private void sendWelcomeMessage(Session session) {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("action", "system.info")
                .add("SymbolicName", bundle.getSymbolicName())
                .add("Version", bundle.getVersion().toString())
                .build();
        sendMessage(session, jsonObject);
    }

    @Override
    public void open(String path, Session session) {
        sessionMap.put(session.getId(), session);
        sendWelcomeMessage(session);
        this.componentConnectors.forEach(connector -> connector.newSession(session));
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

        CompletableFuture.runAsync(() -> {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JsonReader reader = Json.createReader(new StringReader(message));
            JsonObject jsonMsg = reader.readObject();

            String cmdName = jsonMsg.getString("action");
            WebComponent.Command command = this.commandMap.getOrDefault(cmdName, errorCommand);

            if (command != null) {
                JsonValue parameters = jsonMsg.get("parameters");
                JsonValue response;
                if (parameters instanceof JsonArray) {
                    JsonArray paramList = (JsonArray) parameters;
                    JsonValue[] values = new JsonValue[paramList.size()];
                    values = paramList.toArray(values);
                    response = command.execute(values);
                } else {
                    response = command.execute(parameters);
                }
                if (response instanceof JsonObject) {
                    JsonObject robj = (JsonObject) response;
                    if (robj.containsKey("action")) {
                        sendMessage(session, robj);
                        return;
                    }
                }

                sendMessage(
                        session, Json.createObjectBuilder()
                                .add("action", String.format("response:data:%s", jsonMsg.getInt("requestID")))
                                .add("response", response).build()
                );
            }

        });

    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleEvent(Event event) {
        Consumer<JsonValue> response = (Consumer<JsonValue>) event.getProperty("basicResponse");
        JsonObject message = (JsonObject) event.getProperty("message");
        JsonValue parameters = message.get("parameters");
        if (parameters instanceof JsonArray)
            ((JsonArray) parameters).forEach(response);
        else response.accept(parameters);
    }
}
