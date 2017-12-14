package media.dee.dcms.webapp.cms.components;

import media.dee.dcms.components.AdminModule;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import java.util.concurrent.atomic.AtomicReference;


@AdminModule("/webapp/js/layout/Menubar")
@Component
public class NavigationMenu implements GUIComponent {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();


    @Reference
    void setLogService( LogService log ) {
        logRef.set(log);
    }


    @Activate
    void activate(ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "NavigationMenu Activated.");
    }
}
