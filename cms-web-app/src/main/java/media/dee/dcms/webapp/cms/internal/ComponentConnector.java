package media.dee.dcms.webapp.cms.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import media.dee.dcms.components.AdminModule;
import media.dee.dcms.components.WebComponent;
import org.eclipse.jetty.websocket.api.Session;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component(immediate = true)
@ShortCommandName("components/essential/bundles")
@SuppressWarnings("unused")
public class ComponentConnector implements IComponentConnector, WebComponent.Command {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private final AtomicReference<CommunicationHandler> communicationHandler = new AtomicReference<>();
    private final List<WebComponent> guiComponents = new LinkedList<>();
    private final List<HttpService> httpServiceList = new LinkedList<>();
    private Map<String, BiConsumer<JsonNode, Consumer<JsonNode>>> commands = new HashMap<>();

    private JsonNode getCommand(WebComponent component, CommandType commandType) {


        AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
        Bundle bundle = FrameworkUtil.getBundle(component.getClass());

        ObjectNode cmd = objectMapper.createObjectNode()
                .put("action", commandType.toString());
        cmd.putObject("bundle")
                .put("bundlePath", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value()))
                .put("SymbolicName", bundle.getSymbolicName())
                .put("Version", bundle.getVersion().toString());

        return cmd;

    }

    private void ModuleResourcesAction(WebComponent guiComponent, ComponentResourcesAction resourcesAction) {
        AdminModule adminModule = guiComponent.getClass().getAnnotation(AdminModule.class);
        String path = adminModule.value();
        File fPath = new File(path);
        File dir = fPath.getParentFile();
        Bundle bundle = FrameworkUtil.getBundle(guiComponent.getClass());
        ServiceReference<HttpService> ref = bundle.getBundleContext().getServiceReference(HttpService.class);
        HttpService bundleHttpService = bundle.getBundleContext().getService(ref);
        try {
            if (resourcesAction == ComponentResourcesAction.Register) {
                bundleHttpService.registerResources(String.format("/cms/%s/%s%s", bundle.getSymbolicName(), bundle.getVersion().toString(), dir), dir.toString(), null);

            } else if (resourcesAction == ComponentResourcesAction.UnRegister) {
                bundleHttpService.unregister(String.format("/cms/%s/%s%s", bundle.getSymbolicName(), bundle.getVersion().toString(), dir));
            }
        } catch (Exception exception) {
            switch (resourcesAction){
                case Register:
                    logRef.get().log(LogService.LOG_ERROR, String.format("Error while registering httpService Resource of GUIComponent: %s", guiComponent.getClass().getName()), exception);
                    break;
                case UnRegister:
                    logRef.get().log(LogService.LOG_ERROR, String.format("Error while un-registering httpService Resource of GUIComponent: %s", guiComponent.getClass().getName()), exception);
                    break;
            }
        }

        bundle.getBundleContext().ungetService(ref);
    }

    @Activate
    public void activate() {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "CMS WebSocket Activated");
    }

    @Reference
    public void setLogService(LogService log) {
        logRef.set(log);
    }


    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, unbind = "unbindCommunicationHandler", policy = ReferencePolicy.DYNAMIC)
    public void bindCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler.set(communicationHandler);
    }

    public void unbindCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler.compareAndSet(communicationHandler, null);
    }


    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, unbind = "unbindHttpService", policy = ReferencePolicy.DYNAMIC)
    public void bindHttpService(HttpService httpService) {
        synchronized (httpServiceList) {
            httpServiceList.add(httpService);
        }

        try {
            httpService.registerResources("/cms/fe", "/webapp", null);
        } catch (NamespaceException e) {
            throw new RuntimeException(e);
        }

        guiComponents.parallelStream()
                .forEach(guiComponent -> ModuleResourcesAction(guiComponent, ComponentResourcesAction.Register));
    }

    public void unbindHttpService(HttpService httpService) {
        synchronized (httpServiceList) {
            httpServiceList.remove(httpService);
        }

        httpService.unregister("/cms/fe");

        guiComponents.parallelStream()
                .forEach(guiComponent -> ModuleResourcesAction(guiComponent, ComponentResourcesAction.UnRegister));

    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, unbind = "unbindEssentialComponent", policy = ReferencePolicy.DYNAMIC)
    public void bindEssentialComponent(WebComponent component) {
        synchronized (guiComponents) {
            guiComponents.add(component);
            httpServiceList.parallelStream()
                    .forEach(httpService -> ModuleResourcesAction(component, ComponentResourcesAction.Register));
            AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
            if (adminModule.autoInstall())

                communicationHandler.get().sendAll(getCommand(component, CommandType.Install));
        }
    }

    public void unbindEssentialComponent(WebComponent component) {
        synchronized (guiComponents) {
            guiComponents.remove(component);
            httpServiceList.parallelStream()
                    .forEach(httpService -> ModuleResourcesAction(component, ComponentResourcesAction.UnRegister));
            communicationHandler.get().sendAll(getCommand(component, CommandType.Uninstall));
        }
    }

    @Override
    public void newSession(Session session) {

    }

    @Override
    public JsonNode execute(JsonNode... arguments) {
        final ArrayNode bundles = objectMapper.createArrayNode();

        guiComponents.stream()
                .filter(guiComponent -> guiComponent.getClass().getAnnotation(AdminModule.class).autoInstall())
                .map((component) -> {
                    AdminModule adminModule = component.getClass().getAnnotation(AdminModule.class);
                    Bundle bundle = FrameworkUtil.getBundle(component.getClass());
                    return objectMapper.createObjectNode()
                            .put("bundlePath", String.format("/cms/%s/%s%s.js", bundle.getSymbolicName(), bundle.getVersion().toString(), adminModule.value()))
                            .put("SymbolicName", bundle.getSymbolicName())
                            .put("Version", bundle.getVersion().toString());
                })
                .forEach(bundles::add);
        return bundles;
    }
}
