package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.websocket.WebSocketService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ServerEndpoint("cms")
@Component(immediate = true)
public class WebSocketEndpoint implements media.dee.dcms.websocket.WebSocketEndpoint{

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

    private void sendWelcomeMessage(Session session){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "system.info");
            jsonObject.put("SymbolicName",  bundle.getSymbolicName());
            jsonObject.put("Version",  bundle.getVersion().toString() );

            sendMessage(session, jsonObject);

        }catch (JSONException ex){
            //pass
        }
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
        try {
            JSONObject jsonMsg = new JSONObject(message);
            if ( jsonMsg.getString("action").equals("dashboard:widgets:installed")){
                try{
                    JSONObject errMsg = new JSONObject();
                    errMsg.put("action", String.format("response:data:%s", jsonMsg.get("requestID")));

                    JSONObject w1 = new JSONObject();
                    w1.put("SymbolicName", "meida.dee.dcms.user-profile");
                    w1.put("Version", "0.0.1.SNAPSHOT");
                    w1.put("id", "profile-stats");
                    w1.put("instanceID", 0);
                    w1.put("cls", "d.cms.ui.component.Dashboard.Card");
                    w1.put("bundle", "userprofile.js");

                    JSONObject w2 = new JSONObject();
                    w2.put("SymbolicName", "meida.dee.dcms.user-profile");
                    w2.put("Version", "0.0.1.SNAPSHOT");
                    w2.put("id", "profile-engagement");
                    w2.put("instanceID", 1);
                    w2.put("cls", "d.cms.ui.component.Dashboard.Card");
                    w2.put("bundle", "userprofile.js");


                    JSONObject w3 = new JSONObject();
                    w3.put("SymbolicName", "meida.dee.dcms.user-profile");
                    w3.put("Version", "0.0.1.SNAPSHOT");
                    w3.put("id", "profile-stats");
                    w3.put("instanceID", 2);
                    w3.put("cls", "d.cms.ui.component.Dashboard.Card");
                    w3.put("bundle", "userprofile.js");

                    JSONArray widgets = new JSONArray();
                    widgets.put(w1);
                    widgets.put(w2);
                    widgets.put(w3);

                    errMsg.put("data", widgets);
                    sendMessage(session, errMsg);
                }catch(JSONException ex){
                    //ignore
                }
            } else if ( jsonMsg.getString("action").equals("meida.dee.dcms.user-profile:0.0.1.SNAPSHOT:ProfileProgressItem:request:config")){

                Hashtable<String,Object> dict = new Hashtable<>();
                Consumer<String> sendMessage = (msg) -> {
                    try {
                        sendMessage(session, String.format("{\"action\": \"response:data:%s\", %s}", jsonMsg.get("requestID"), msg));
                    }catch(JSONException ex){
                        //ignore
                    }
                };
                dict.put("sendMessage",  sendMessage );
                dict.put("message",  jsonMsg );
                Event event = new Event( "component/userpofile", dict );
                eventAdmin.postEvent(event);

            }
        } catch (JSONException e) {
            try{
                JSONObject errMsg = new JSONObject();
                errMsg.put("action", "error");
                errMsg.put("message", e.getLocalizedMessage());
                sendMessage(session, errMsg);
            }catch(JSONException ex){
                //ignore
            }
        }
    }
}
