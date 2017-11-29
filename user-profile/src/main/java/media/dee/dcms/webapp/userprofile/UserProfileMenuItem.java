package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.webapp.cms.MenuItem;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Component
public class UserProfileMenuItem implements MenuItem{
    private final AtomicReference<LogService> logRef = new AtomicReference<>();

    @Reference(
            cardinality=ReferenceCardinality.MULTIPLE,
            policy=ReferencePolicy.DYNAMIC
    )
    public void setLog( LogService log ) {
        logRef.set(log);
    }

    public void unsetLog( LogService log ) {
        logRef.compareAndSet(log, null);
    }

    @Override
    public List<String> getJavascriptModules() {
        return Arrays.asList("js/layout/userprofile/userprofile");
    }

    @Activate
    void activate() {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "UserProfileMenuItem Activated");
    }

    @Reference(
            cardinality= ReferenceCardinality.MULTIPLE,
            policy= ReferencePolicy.DYNAMIC
    )
    void setHttpService( HttpService httpService) {
        try {
            httpService.registerResources("/cms/js/layout/userprofile", "/webapp/js/layout/userprofile", null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void unsetHttpService(HttpService httpService){
        try {
            httpService.unregister("/cms/js/layout/userprofile");
        } catch (IllegalArgumentException exception) {
            // Ignore; servlet registration probably failed earlier on...
        }
    }
}
