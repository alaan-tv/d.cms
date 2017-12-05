package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.webapp.cms.components.GUIComponent;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import javax.websocket.Session;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ComponentConnector {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private WebSocketEndpoint wsEndpoint;
    private List<GUIComponent> GUIComponentList = new LinkedList<>();

    public static JSONObject getInstallCommand(GUIComponent component){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "bundle.install");
            jsonObject.put("bundle", component.getClass().getAnnotation(AdminModule.class).value());
        } catch (JSONException e) {
            //error
        }
        return jsonObject;
    }

    public static JSONObject getUnInstallCommand(GUIComponent component){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "bundle.uninstall");
            jsonObject.put("bundle", component.getClass().getAnnotation(AdminModule.class).value() );
        } catch (JSONException e) {
            //error
        }
        return jsonObject;
    }

    public void activate(ComponentContext ctx){
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    public void bindLong( LogService log ) {
        logRef.set(log);
    }

    public void unbindLong( LogService log ) {
        logRef.compareAndSet(log, null);
    }


    public void bindWebSocketEndpoint(media.dee.dcms.websocket.WebSocketEndpoint wsEndpoint){
        if( wsEndpoint instanceof WebSocketEndpoint)
            this.wsEndpoint = (WebSocketEndpoint)wsEndpoint;
    }

    public void unbindWebSocketEndpoint(media.dee.dcms.websocket.WebSocketEndpoint wsEndpoint){
        if( wsEndpoint instanceof WebSocketEndpoint)
            this.wsEndpoint = null;
    }

    public void bindEssentialComponent(GUIComponent component) {
        GUIComponentList.add(component);
        wsEndpoint.sendAll(getInstallCommand(component));
    }

    public void unbindEssentialComponent( GUIComponent component ) {
        GUIComponentList.remove(component);
        wsEndpoint.sendAll(getUnInstallCommand(component));
    }

    public void newSession(Session session) {
        GUIComponentList.stream()
                .map(ComponentConnector::getInstallCommand)
                .forEach( msg -> wsEndpoint.sendMessage(session, msg));
    }
}
