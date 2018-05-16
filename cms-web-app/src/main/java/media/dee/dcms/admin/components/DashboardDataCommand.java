package media.dee.dcms.admin.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import media.dee.dcms.components.WebComponent;
import media.dee.dcms.admin.internal.ShortCommandName;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

@Component
@ShortCommandName("component/dashboard")
public class DashboardDataCommand implements WebComponent.Command {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicReference<LogService> logRef = new AtomicReference<>();

    @Reference
    void setLogService(LogService log) {
        logRef.set(log);
    }

    @Override
    public JsonNode execute(JsonNode... command) {
        int instanceID = command[0].get("instanceID").asInt();

        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        URL dataURL = bundle.getResource(String.format("/data/dashboard/%s.json", instanceID));
        if (dataURL == null) {
            return objectMapper.createObjectNode()
                    .put("action", "error")
                    .put("code", "not-fount");
        }
        try (InputStream dataInStream = dataURL.openStream()) {

            return objectMapper.readValue(dataInStream, JsonNode.class);

        } catch (IOException ex) {
            logRef.get().log(LogService.LOG_ERROR, "Error Reading data", ex);
            return objectMapper.createObjectNode()
                    .put("action", "error")
                    .put("code", ex.getMessage());
        }
    }
}
