package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.components.WebComponent;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.websocket.Session;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component
@ShortCommandName("components/essential/bundles")
@SuppressWarnings("unused")
public class ComponentConnector implements IComponentConnector, WebComponent.Command {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final AtomicReference<WebSocketEndpoint> wsEndpoint = new AtomicReference<>();
    private final List<WebComponent> guiComponents = new LinkedList<>();
    private final List<HttpService> httpServiceList = new LinkedList<>();
    private Map<String, BiConsumer<JsonObject, Consumer<JsonValue>>> commands = new HashMap<>();

    private static JsonObject getInstallCommand(WebComponent component){
        AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
        Bundle bundle = FrameworkUtil.getBundle(component.getClass());

        JsonObject jsonBundle = Json.createObjectBuilder()
                .add("bundlePath", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value() ))
                .add("SymbolicName", bundle.getSymbolicName() )
                .add("Version", bundle.getVersion().toString() )
                .build();

        return Json.createObjectBuilder()
                .add("action", "bundle.install")
                .add("bundle", jsonBundle )
                .build();
    }

    private static JsonObject getUnInstallCommand(WebComponent component){


        AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
        Bundle bundle = FrameworkUtil.getBundle(component.getClass());

        JsonObject jsonBundle = Json.createObjectBuilder()
                .add("bundlePath", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value() ))
                .add("SymbolicName", bundle.getSymbolicName() )
                .add("Version", bundle.getVersion().toString() )
                .build();

        return Json.createObjectBuilder()
                .add("action", "bundle.uninstall")
                .add("bundle", jsonBundle )
                .build();

    }

    private void registerModuleResources(@SuppressWarnings("unused") HttpService httpService, WebComponent guiComponent){
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

    private void unRegisterModuleResources(@SuppressWarnings("unused") HttpService httpService, WebComponent guiComponent){
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


    @Activate
    public void activate(){
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    @Reference
    public void setLogService( LogService log ) {
        logRef.set(log);
    }


    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, unbind = "unbindWebSocketEndpoint", policy = ReferencePolicy.DYNAMIC)
    public void bindWebSocketEndpoint(media.dee.dcms.websocket.WebSocketEndpoint wsEndpoint){
        if( wsEndpoint instanceof WebSocketEndpoint)
            this.wsEndpoint.set((WebSocketEndpoint)wsEndpoint);
    }

    public void unbindWebSocketEndpoint(media.dee.dcms.websocket.WebSocketEndpoint wsEndpoint){
        if( wsEndpoint instanceof WebSocketEndpoint)
            this.wsEndpoint.compareAndSet((WebSocketEndpoint)wsEndpoint, null);
    }


    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, unbind = "unbindHttpService", policy = ReferencePolicy.DYNAMIC)
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, unbind = "unbindEssentialComponent", policy = ReferencePolicy.DYNAMIC)
    public void bindEssentialComponent(WebComponent component) {
        synchronized (guiComponents) {
            guiComponents.add(component);
            httpServiceList.parallelStream()
                    .forEach( httpService -> registerModuleResources(httpService, component));
            AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
            if( adminModule.autoInstall() )
                wsEndpoint.get().sendAll(getInstallCommand(component));
        }
    }

    public void unbindEssentialComponent( WebComponent component ) {
        synchronized (guiComponents) {
            guiComponents.remove(component);
            httpServiceList.parallelStream()
                    .forEach( httpService -> unRegisterModuleResources(httpService, component));
            wsEndpoint.get().sendAll(getUnInstallCommand(component));
        }
    }

    @Override
    public void newSession(Session session) {

    }

    @Override
    public JsonValue execute(JsonValue... arguments) {
        final JsonArrayBuilder bundles = Json.createArrayBuilder();

        guiComponents.stream()
                .filter( guiComponent -> guiComponent.getClass().getAnnotation(AdminModule.class).autoInstall() )
                .map( (component)->{
                    AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
                    Bundle bundle = FrameworkUtil.getBundle(component.getClass());
                    return Json.createObjectBuilder()
                            .add("bundlePath", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value()))
                            .add("SymbolicName", bundle.getSymbolicName() )
                            .add("Version", bundle.getVersion().toString())
                            .build();
                })
                .forEach( bundles::add );
        return bundles.build();
    }
}
