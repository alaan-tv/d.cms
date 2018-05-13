package media.dee.dcms.webapp.cms.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import media.dee.dcms.components.UUID;
import media.dee.dcms.components.WebComponent;
import org.eclipse.jetty.websocket.api.Session;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class AdminCommunicationHandler implements CommunicationHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final Map<String, WebComponent.Command> commandMap = new Hashtable<>();



    private final WebComponent.Command errorCommand = (JsonNode... arguments) ->
            objectMapper.createObjectNode()
                .put("error", "not-fount");


    @Reference
    void setLogService(LogService log) {
        logRef.set(log);
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

    @SuppressWarnings("unused")
    void unbindCommand(WebComponent.Command command) {
        synchronized (this.commandMap) {
            this.commandMap.remove(getCommandName(command));
        }
    }


    @Activate
    public void activate(@SuppressWarnings("unused") ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    @Deactivate
    public void deactivate(@SuppressWarnings("unused") ComponentContext ctx) {
    }


    private void sendMessage(final Session session, final JsonNode message) {
        try {
            session.getRemote().sendString( objectMapper.writeValueAsString(message) );
        } catch (JsonProcessingException e) {
            logRef.get().log(LogService.LOG_ERROR, String.format("Error Sending Message via Websocket to client: %s.", session.getRemoteAddress()));
        } catch (IOException e) {
            try{ session.close(); } catch (IOException ex){ /* ignore */}
            logRef.get().log(LogService.LOG_ERROR, String.format("Error Sending Message via Websocket to client: %s.", session.getRemoteAddress()));
        }
    }


    public void sendAll(JsonNode message) {
        clientSessions
                .parallelStream()
                .forEach(session -> sendMessage(session, message));
    }

    private void sendWelcomeMessage(Session session) {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        JsonNode jsonObject = objectMapper.createObjectNode()
                .put("action", "system.info")
                .put("SymbolicName", bundle.getSymbolicName())
                .put("Version", bundle.getVersion().toString());
        sendMessage(session, jsonObject);
    }

    @Override
    public void registerConnection(Session session) {
        clientSessions.add(session);
        sendWelcomeMessage(session);
    }

    @Override
    public void unregisterConnection(Session session) {
        clientSessions.remove(session);
    }


    @Override
    public void processCommand(Session session, String message) {

        CompletableFuture.runAsync(() -> {

            String cmdName;
            ObjectNode jsonMsg = objectMapper.createObjectNode();
            try {
                jsonMsg = objectMapper.readValue(message, ObjectNode.class);
                cmdName = jsonMsg.get("action").asText();
            } catch (IOException e) {
                cmdName = null;
            }
            WebComponent.Command command = this.commandMap.getOrDefault(cmdName, errorCommand);

            if (command != null) {
                JsonNode parameters = jsonMsg.get("parameters");
                JsonNode response;
                if (parameters instanceof ArrayNode) {
                    ArrayNode paramList = (ArrayNode) parameters;
                    JsonNode[] nodes = new JsonNode[paramList.size()];
                    for( int i=0; i< nodes.length ; ++i)
                        nodes[i] = paramList.get(i);
                    response = command.execute(nodes);
                } else {
                    response = command.execute(parameters);
                }
                if (response instanceof ObjectNode) {
                    ObjectNode robj = (ObjectNode) response;
                    if (robj.has("action")) {
                        sendMessage(session, robj);
                        return;
                    }
                }

                ObjectNode responseMsg = objectMapper.createObjectNode()
                        .put("action", String.format("response:data:%s", jsonMsg.get("requestID").asInt() ));
                responseMsg.set("response", response);
                sendMessage(session, responseMsg );
            }

        });

    }
}
