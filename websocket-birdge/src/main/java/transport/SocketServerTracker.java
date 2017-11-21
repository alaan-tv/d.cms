package transport;

import endpoints.ChatEndpoint;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

public class SocketServerTracker extends ServiceTracker<ServerContainer,ServerContainer> {
    private LogService logService = null;


    public SocketServerTracker(BundleContext context) {
        super(context, ServerContainer.class, null);
    }

    private void log(int level, String message){
        ServiceReference<LogService> ref = context.getServiceReference(LogService.class);
        if (ref != null) {
            LogService log = context.getService(ref);
            log.log(level, message);
        }
    }

    @Override
    public ServerContainer addingService(ServiceReference<ServerContainer> reference) {
        log(LogService.LOG_INFO, String.format("Websocket Server added from bundle: %s-%s", reference.getBundle().getSymbolicName(), reference.getBundle().getVersion()));
        ServerContainer wsContainer = this.context.getService(reference);
        try {
            wsContainer.addEndpoint(ChatEndpoint.class);
        } catch (DeploymentException e) {
            log(LogService.LOG_INFO, "Websocket Server failed to add endpoints");
            e.printStackTrace();
        }
        return super.addingService(reference);
    }

    @Override
    public void removedService(ServiceReference<ServerContainer> reference, ServerContainer service) {
        log(LogService.LOG_INFO, String.format("Websocket Server removed from bundle: %s-%s", reference.getBundle().getSymbolicName(), reference.getBundle().getVersion()));
        super.removedService(reference, service);
    }


    @Override
    public void modifiedService(ServiceReference<ServerContainer> reference, ServerContainer service) {
        log(LogService.LOG_INFO, String.format("Websocket Server modified from bundle: %s-%s", reference.getBundle().getSymbolicName(), reference.getBundle().getVersion()));
        super.modifiedService(reference, service);
    }
}
