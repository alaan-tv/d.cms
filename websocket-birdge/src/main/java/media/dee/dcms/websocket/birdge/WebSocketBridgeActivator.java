package media.dee.dcms.websocket.birdge;

import endpoints.WebSocketServiceServiceTracker;
import media.dee.dcms.websocket.WebSocketDispatcher;
import media.dee.dcms.websocket.WebSocketService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class WebSocketBridgeActivator implements BundleActivator {
    private ServiceRegistration<WebSocketDispatcher> webSocketDispatcherServiceRegistration;
    private ServiceRegistration<WebSocketService> webSocketServiceServiceRegistration;
    private WebSocketServiceServiceTracker webSocketServiceServiceTracker;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        WebSocketServiceDispatcher dispatcher = new WebSocketServiceDispatcher();
        webSocketServiceServiceRegistration = bundleContext.registerService(WebSocketService.class, dispatcher, null);
        webSocketDispatcherServiceRegistration = bundleContext.registerService(WebSocketDispatcher.class, dispatcher, null);
        webSocketServiceServiceTracker = new WebSocketServiceServiceTracker(bundleContext);
        webSocketServiceServiceTracker.open();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        webSocketDispatcherServiceRegistration.unregister();
        webSocketServiceServiceRegistration.unregister();
        webSocketServiceServiceTracker.close();
    }
}
