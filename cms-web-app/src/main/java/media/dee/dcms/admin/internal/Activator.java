package media.dee.dcms.admin.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.util.function.Consumer;

public class Activator implements BundleActivator {
    private BundleContext context;

    private void log(Consumer<LogService> logServiceConsumer){
        ServiceReference<LogService> logServiceServiceReference = context.getServiceReference(LogService.class);
        LogService logService = context.getService(logServiceServiceReference);
        try{
            logServiceConsumer.accept(logService);
        } catch (Throwable th){
            logService.log(LogService.LOG_ERROR, "Error while logging", th);
        }finally {
            context.ungetService(logServiceServiceReference);
        }

    }

    public void start(BundleContext context) {
        this.context = context;
        log( log ->
                log.log(LogService.LOG_INFO, String.format("Dee.CMS Admin - Version: %s\t Started.", context.getBundle().getVersion()))
        );

    }

    public void stop(BundleContext context) {

        log( log ->
                log.log(LogService.LOG_INFO, String.format("Dee.CMS Admin - Version: %s\t Stopped.", context.getBundle().getVersion()))
        );
    }
}
