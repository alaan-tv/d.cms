package media.dee.dcms.webapp.cms.components;

import media.dee.dcms.components.WebComponent;
import media.dee.dcms.webapp.cms.internal.ShortCommandName;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

@Component
@ShortCommandName("component/dashboard")
public class DashboardDataCommand implements WebComponent.Command {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();

    @Reference
    void setLogService(LogService log) {
        logRef.set(log);
    }

    @Override
    public JsonValue execute(JsonValue... command) {
        int instanceID = ((JsonObject) command[0]).getInt("instanceID");

        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        URL dataURL = bundle.getResource(String.format("/data/dashboard/%s.json", instanceID) );
        if( dataURL == null ){
            return Json.createObjectBuilder()
                    .add("action","error")
                    .add("code", "not-fount")
                    .build();
        }
        try (InputStream dataInStream = dataURL.openStream()){

            return Json.createReader(dataInStream).readArray();


        } catch (IOException ex){
            logRef.get().log(LogService.LOG_ERROR, "Error Reading data", ex);
            return Json.createObjectBuilder()
                    .add("action","error")
                    .add("code", ex.getMessage())
                    .build();
        }
    }
}
