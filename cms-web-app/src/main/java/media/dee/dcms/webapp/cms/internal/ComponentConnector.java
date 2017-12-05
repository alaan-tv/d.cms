package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.webapp.cms.components.GUIComponent;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

import javax.websocket.Session;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ComponentConnector {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final AtomicReference<WebSocketEndpoint> wsEndpoint = new AtomicReference<>();
    private final List<GUIComponent> guiComponents = new LinkedList<>();
    private final List<HttpService> httpServiceList = new LinkedList<>();

    private static JSONObject getInstallCommand(GUIComponent component){
        AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
        Bundle bundle = FrameworkUtil.getBundle(component.getClass());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "bundle.install");
            jsonObject.put("bundle", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value() ));
        } catch (JSONException e) {
            //error
        }
        return jsonObject;
    }

    private static JSONObject getUnInstallCommand(GUIComponent component){
        AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
        Bundle bundle = FrameworkUtil.getBundle(component.getClass());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "bundle.uninstall");
            jsonObject.put("bundle", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value() ));
        } catch (JSONException e) {
            //error
        }
        return jsonObject;
    }

    private void registerModuleResources(HttpService httpService, GUIComponent guiComponent){
        AdminModule adminModule = guiComponent.getClass().getAnnotation(AdminModule.class);
        String path = adminModule.value();
        File fPath = new File(path);
        File dir = fPath.getParentFile();
        Bundle bundle = FrameworkUtil.getBundle(guiComponent.getClass());
        ServiceReference<HttpService> ref = bundle.getBundleContext().getServiceReference(HttpService.class);
        HttpService bundleHttpService = bundle.getBundleContext().getService(ref);
        try {
            bundleHttpService.registerResources(String.format("/cms/%s/%s%s", bundle.getSymbolicName(), bundle.getVersion().toString(), dir ), dir.toString(), null );
        } catch (Exception exception) {
            logRef.get().log(LogService.LOG_ERROR, String.format("Error while registering httpService Resource of GUIComponent: %s", guiComponent.getClass().getName()), exception);
        }
    }

    private void unRegisterModuleResources(HttpService httpService, GUIComponent guiComponent){
        AdminModule adminModule = guiComponent.getClass().getAnnotation(AdminModule.class);
        String path = adminModule.value();
        File fPath = new File(path);
        File dir = fPath.getParentFile();
        Bundle bundle = FrameworkUtil.getBundle(guiComponent.getClass());
        ServiceReference<HttpService> ref = bundle.getBundleContext().getServiceReference(HttpService.class);
        HttpService bundleHttpService = bundle.getBundleContext().getService(ref);
        try {
            bundleHttpService.unregister( String.format("/cms/%s/%s%s", bundle.getSymbolicName(), bundle.getVersion().toString(), dir ) );
        } catch (Exception exception) {
            logRef.get().log(LogService.LOG_ERROR, String.format("Error while un-registering httpService Resource of GUIComponent: %s", guiComponent.getClass().getName()), exception);
        }
        bundle.getBundleContext().ungetService(ref);
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
            this.wsEndpoint.set((WebSocketEndpoint)wsEndpoint);
    }

    public void unbindWebSocketEndpoint(media.dee.dcms.websocket.WebSocketEndpoint wsEndpoint){
        if( wsEndpoint instanceof WebSocketEndpoint)
            this.wsEndpoint.compareAndSet((WebSocketEndpoint)wsEndpoint, null);
    }


    public void bindHttpService( HttpService httpService ) {
        synchronized (httpServiceList) {
            httpServiceList.add(httpService);
        }

        guiComponents.parallelStream()
                .forEach( guiComponent -> registerModuleResources(httpService, guiComponent ));
    }

    public void unbindHttpService(HttpService httpService){
        synchronized (httpServiceList) {
            httpServiceList.remove(httpService);
        }
        guiComponents.parallelStream()
                .forEach( guiComponent -> unRegisterModuleResources(httpService, guiComponent ));
    }

    public void bindEssentialComponent(GUIComponent component) {
        synchronized (guiComponents) {
            guiComponents.add(component);
            httpServiceList.parallelStream()
                    .forEach( httpService -> registerModuleResources(httpService, component));
            wsEndpoint.get().sendAll(getInstallCommand(component));
        }
    }

    public void unbindEssentialComponent( GUIComponent component ) {
        synchronized (guiComponents) {
            guiComponents.remove(component);
            httpServiceList.parallelStream()
                    .forEach( httpService -> unRegisterModuleResources(httpService, component));
            wsEndpoint.get().sendAll(getUnInstallCommand(component));
        }
    }

    public void newSession(Session session) {
        guiComponents.stream()
                .map(ComponentConnector::getInstallCommand)
                .forEach( msg -> wsEndpoint.get().sendMessage(session, msg));
    }
}
