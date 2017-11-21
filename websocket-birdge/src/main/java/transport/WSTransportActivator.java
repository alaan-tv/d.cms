package transport;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class WSTransportActivator implements BundleActivator {
    private SocketServerTracker socketServerTracker;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        socketServerTracker = new SocketServerTracker(bundleContext);
        socketServerTracker.open();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        socketServerTracker.close();
    }
}
