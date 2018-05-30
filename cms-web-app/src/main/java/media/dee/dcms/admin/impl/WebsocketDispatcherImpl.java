package media.dee.dcms.admin.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import media.dee.dcms.admin.internal.ShortCommandName;
import media.dee.dcms.admin.services.AdminWebsocketDispatcher;
import media.dee.dcms.admin.services.ComponentService;
import media.dee.dcms.core.components.UUID;
import media.dee.dcms.core.components.WebComponent;
import media.dee.dcms.websocket.Session;
import media.dee.dcms.websocket.SessionManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component(scope = ServiceScope.SINGLETON)
public class WebsocketDispatcherImpl implements AdminWebsocketDispatcher {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private LogService logService;
    private final Map<String, WebComponent.Command> commandMap = new Hashtable<>();

    private final WebComponent.Command errorCommand = (JsonNode... arguments) ->
            objectMapper.createObjectNode()
                .put("error", "not-fount");
    private SessionManager sessionManager;
    private ComponentService componentService;


    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setLogService(LogService log) {
        this.logService = log;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
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
                logService.log(LogService.LOG_ERROR, String.format("Command[%s] is already registered", cmdName));
        }
    }

    @SuppressWarnings("unused")
    void unbindCommand(WebComponent.Command command) {
        synchronized (this.commandMap) {
            this.commandMap.remove(getCommandName(command));
        }
    }


    @Activate
    public void activate() {
        logService.log(LogService.LOG_INFO, "CMS WebSocketDispatcher Activated");

        componentService.bindCommunicationHandler(this);

        /* test clustered socket */
        sessionManager.broadcast(
                objectMapper.createObjectNode()
                        .put("action", "console.log")
                        .put("message", String.format("Hello! I'm `%s`, I've just joined the cluster :)", getHostInformation().get("ip") ))
        );

    }


    @Deactivate
    public void deactivate(){
        componentService.unbindCommunicationHandler(this);
    }


    private ObjectNode getHostInformation(){
        try {
            InetAddress localhost =  InetAddress.getLocalHost();
            return objectMapper.createObjectNode()
                    .put("hostname", localhost.getHostName() )
                    .put("ip", localhost.getHostAddress());
        } catch (UnknownHostException e) {
            return objectMapper.createObjectNode();
        }
    }

    private void sendWelcomeMessage(Session session) {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        JsonNode jsonObject = objectMapper.createObjectNode()
                .put("action", "system.info")
                .put("SymbolicName", bundle.getSymbolicName())
                .put("Version", bundle.getVersion().toString())
                .set("Server-ID", getHostInformation() );
        sessionManager.send(session, jsonObject);
    }


    @Override
    public void sessionConnected(org.eclipse.jetty.websocket.api.Session session) {
        sendWelcomeMessage(sessionManager.sessionConnected(session));
    }

    @Override
    public void sessionClosed(org.eclipse.jetty.websocket.api.Session session) {
        sessionManager.sessionClosed(session);
    }

    @Override
    public void onMessage(org.eclipse.jetty.websocket.api.Session session, String message) {
        Session sessionWrapper = sessionManager.get(session);

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
                        sessionManager.send(sessionWrapper, robj);
                        return;
                    }
                }

                if( !jsonMsg.has("requestID"))
                    return;

                ObjectNode responseMsg = objectMapper.createObjectNode()
                        .put("action", String.format("response:data:%s", jsonMsg.get("requestID").asInt() ));
                responseMsg.set("response", response);
                sessionManager.send(sessionWrapper, responseMsg );
            }

        });

    }

    @Override
    public long send(JsonNode message) {
        return sessionManager.send(message);
    }
}
