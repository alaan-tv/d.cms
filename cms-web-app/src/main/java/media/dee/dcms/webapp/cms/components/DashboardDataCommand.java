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
    void setLogService( LogService log ) {
        logRef.set(log);
    }

    @Override
    public JsonValue execute(JsonValue... command) {
        int instanceID = ((JsonObject)command[0]).getInt("instanceID");

        if (instanceID <= 0) {

            JsonArray widgets = Json.createArrayBuilder()
                    .add(
                            Json.createObjectBuilder()
                                    .add("SymbolicName", "meida.dee.dcms.user-profile")
                                    .add("Version", "0.0.1.SNAPSHOT")
                                    .add("id", "3ecbd060-dd59-4d9a-a2cc-ca41f1562a4a")
                                    .add("instanceID", 0)
                                    .add("cls", "d.cms.ui.component.Dashboard.Card")
                                    .add("bundle", "userprofile.js")
                                    .build()
                    )
                    .add(
                            Json.createObjectBuilder()
                                    .add("SymbolicName", "meida.dee.dcms.user-profile")
                                    .add("Version", "0.0.1.SNAPSHOT")
                                    .add("id", "5d4b2f67-ee47-4a84-947d-d9b65d94e3ab")
                                    .add("instanceID", 1)
                                    .add("cls", "d.cms.ui.component.Dashboard.Card")
                                    .add("bundle", "userprofile.js")
                                    .build()
                    )
                    .add(
                            Json.createObjectBuilder()
                                    .add("SymbolicName", "meida.dee.dcms.user-profile")
                                    .add("Version", "0.0.1.SNAPSHOT")
                                    .add("id", "5d4b2f67-ee47-4a84-947d-d9b65d94e3ab")
                                    .add("instanceID", 2)
                                    .add("cls", "d.cms.ui.component.Dashboard.Card")
                                    .add("bundle", "userprofile.js")
                                    .build()
                    )
                    .build();


            return widgets;
        } else {
            JsonArray widgets = Json.createArrayBuilder()
                    .add(
                            Json.createObjectBuilder()
                                    .add("SymbolicName", "meida.dee.dcms.user-profile")
                                    .add("Version", "0.0.1.SNAPSHOT")
                                    .add("id", "3ecbd060-dd59-4d9a-a2cc-ca41f1562a4a")
                                    .add("instanceID", 0)
                                    .add("cls", "d.cms.ui.component.Dashboard.Card")
                                    .add("bundle", "userprofile.js")
                                    .build()
                    )
                    .add(
                            Json.createObjectBuilder()
                                    .add("SymbolicName", "meida.dee.dcms.user-profile")
                                    .add("Version", "0.0.1.SNAPSHOT")
                                    .add("id", "5d4b2f67-ee47-4a84-947d-d9b65d94e3ab")
                                    .add("instanceID", 1)
                                    .add("cls", "d.cms.ui.component.Dashboard.Card")
                                    .add("bundle", "userprofile.js")
                                    .build()
                    )
                    .build();
            return widgets;

        }
    }
}
