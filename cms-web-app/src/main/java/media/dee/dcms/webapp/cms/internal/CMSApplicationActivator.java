package media.dee.dcms.webapp.cms.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class CMSApplicationActivator implements BundleActivator {
    private ServiceTracker httpTracker;

    public void start(BundleContext context) throws Exception {
        httpTracker = new ServiceTracker<HttpService,HttpService>(context, HttpService.class.getName(), null) {
            public void removedService(ServiceReference<HttpService> reference, HttpService service) {
                // HTTP service is no longer available, unregister our resources...
                try {
                    service.unregister("/cms");
                } catch (IllegalArgumentException exception) {
                    // Ignore; servlet registration probably failed earlier on...
                }
            }

            public HttpService addingService(ServiceReference<HttpService> reference) {
                // HTTP service is available, register our resources...
                HttpService httpService = this.context.getService(reference);
                try {
                    httpService.registerResources("/cms", "/webapp", null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return httpService;
            }
        };
        // start tracking all HTTP ServiceReferences...
        httpTracker.open();

    }

    public void stop(BundleContext context) throws Exception {
        // stop tracking all HTTP ServiceReferences...
        httpTracker.close();
    }
}
