package media.dee.dcms.admin.components;

import media.dee.dcms.core.components.AdminModule;
import media.dee.dcms.core.components.UUID;
import media.dee.dcms.core.components.WebComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import java.util.concurrent.atomic.AtomicReference;


@AdminModule(
        value = "Menubar",
        resource = "/webapp/js/layout",
        autoInstall = true
)
@UUID("dashboard")
@Component
public class DashboardPanel implements WebComponent {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();

    @Reference
    void setLogService(LogService log) {
        logRef.set(log);
    }


    @Activate
    void activate(ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "ProfilePieItem Activated");
    }
}
