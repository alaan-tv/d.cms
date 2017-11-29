package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.webapp.cms.internal.WebSocketEndpoint;
import media.dee.dcms.websocket.WebSocketService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import javax.websocket.DeploymentException;

public class WebSocketServiceServiceTracker extends ServiceTracker<WebSocketService, WebSocketService> {
    public WebSocketServiceServiceTracker(BundleContext context) {
        super(context, WebSocketService.class, null);
    }

    @Override
    public WebSocketService addingService(ServiceReference<WebSocketService> reference) {
        try {
            context.getService(reference).addEndpoint(WebSocketEndpoint.class);
        } catch (DeploymentException e) {
            e.printStackTrace(System.err);
        }
        return super.addingService(reference);
    }

    @Override
    public void removedService(ServiceReference<WebSocketService> reference, WebSocketService service) {
        service.removeEndpoint(WebSocketEndpoint.class);
        super.removedService(reference, service);
    }
}
