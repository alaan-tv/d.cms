package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.webapp.cms.components.EssentialComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class UserProfileMenuItem implements EssentialComponent {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();


    public void bindLong( LogService log ) {
        logRef.set(log);
    }

    public void unbindLong( LogService log ) {
        logRef.compareAndSet(log, null);
    }

    @Override
    public List<String> getJavascriptModules() {
        return Arrays.asList("js/layout/userprofile/userprofile");
    }

    public void activate(ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "UserProfileMenuItem Activated");
    }


    public void bindHttpService( HttpService httpService ) {
        try {
            httpService.registerResources("/cms/js/layout/userprofile", "/webapp/js/layout/userprofile", null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void unbindHttpService(HttpService httpService){
        try {
            httpService.unregister("/cms/js/layout/userprofile");
        } catch (IllegalArgumentException exception) {
            // Ignore; servlet registration probably failed earlier on...
        }
    }
}
