package media.dee.dcms.web.launcher.websocket;

import media.dee.dcms.websocket.WebSocketDispatcher;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.util.LinkedList;
import java.util.List;

public class WebSocketDispatcherTracker extends ServiceTracker<WebSocketDispatcher, WebSocketDispatcher>{
    private List<WebSocketDispatcher> dispatchers = new LinkedList<>();

    public WebSocketDispatcherTracker(BundleContext context) {
        super(context, WebSocketDispatcher.class, null);
    }

    @Override
    public WebSocketDispatcher addingService(ServiceReference<WebSocketDispatcher> reference) {
        dispatchers.add(context.getService(reference));
        return super.addingService(reference);
    }

    @Override
    public void removedService(ServiceReference<WebSocketDispatcher> reference, WebSocketDispatcher service) {
        dispatchers.remove(service);
        super.removedService(reference, service);
    }
}
