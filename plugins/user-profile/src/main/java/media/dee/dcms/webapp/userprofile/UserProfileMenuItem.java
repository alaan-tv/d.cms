package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.webapp.cms.components.GUIComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

import java.util.concurrent.atomic.AtomicReference;


@AdminModule("js/layout/userprofile/userprofile")
public class UserProfileMenuItem implements GUIComponent {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();


    public void bindLong( LogService log ) {
        logRef.set(log);
    }

    public void unbindLong( LogService log ) {
        logRef.compareAndSet(log, null);
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
