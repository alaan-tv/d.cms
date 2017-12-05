package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.webapp.cms.components.GUIComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import java.util.concurrent.atomic.AtomicReference;


@AdminModule("/webapp/js/layout/userprofile/userprofile")
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
}
