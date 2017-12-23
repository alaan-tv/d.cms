package media.dee.dcms.webapp.cms.components;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.components.UUID;
import media.dee.dcms.components.WebComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


@AdminModule(value = "/webapp/js/layout/Menubar", autoInstall = true)
@Component(property= EventConstants.EVENT_TOPIC + "=component/8a001058-5c6e-43d1-8e41-7868d9789817", immediate = true)
@UUID("8a001058-5c6e-43d1-8e41-7868d9789817")
public class DashboardPanel implements WebComponent, EventHandler {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private Map<String, BiConsumer<JsonObject, Consumer<JsonValue>>> commands = new HashMap<>();

    public DashboardPanel(){
        commands.put("getData", (message, response)->{

            int instanceID = message.getInt("instanceID");

            if( instanceID <= 0 ) {

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


                response.accept(widgets);
            } else{
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


                response.accept(widgets);
            }

        });
    }


    @Reference
    void setLogService( LogService log ) {
        logRef.set(log);
    }


    @Activate
    void activate(ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "ProfilePieItem Activated");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(Event event) {

        Consumer<JsonValue> response = (Consumer<JsonValue>) event.getProperty("response");
        JsonObject message = (JsonObject) event.getProperty("message");

        JsonArray cmdList = message.getJsonArray("parameters");
        cmdList.forEach( (cmdObject)->{
            String command = ((JsonObject)cmdObject).getString("command");
            if (commands.containsKey(command))
                commands.get(command).accept((JsonObject)cmdObject, response);
        });
    }
}
