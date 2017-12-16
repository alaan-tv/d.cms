package media.dee.dcms.webapp.cms.components;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.components.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


@AdminModule(value = "/webapp/js/layout/Menubar", autoInstall = true)
@Component(property= EventConstants.EVENT_TOPIC + "=component/8a001058-5c6e-43d1-8e41-7868d9789817", immediate = true)
@UUID("8a001058-5c6e-43d1-8e41-7868d9789817")
public class DashboardPanel implements GUIComponent, EventHandler {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private Map<String, BiConsumer<JSONObject, Consumer<JSONObject>>> commands = new HashMap<>();

    public DashboardPanel(){
        commands.put("getData", (message, sendMessage)->{

            try {
                JSONObject response = new JSONObject();
                JSONObject w1 = new JSONObject();
                w1.put("SymbolicName", "meida.dee.dcms.user-profile");
                w1.put("Version", "0.0.1.SNAPSHOT");
                w1.put("id", "3ecbd060-dd59-4d9a-a2cc-ca41f1562a4a");
                w1.put("instanceID", 0);
                w1.put("cls", "d.cms.ui.component.Dashboard.Card");
                w1.put("bundle", "userprofile.js");

                JSONObject w2 = new JSONObject();
                w2.put("SymbolicName", "meida.dee.dcms.user-profile");
                w2.put("Version", "0.0.1.SNAPSHOT");
                w2.put("id", "5d4b2f67-ee47-4a84-947d-d9b65d94e3ab");
                w2.put("instanceID", 1);
                w2.put("cls", "d.cms.ui.component.Dashboard.Card");
                w2.put("bundle", "userprofile.js");


                JSONObject w3 = new JSONObject();
                w3.put("SymbolicName", "meida.dee.dcms.user-profile");
                w3.put("Version", "0.0.1.SNAPSHOT");
                w3.put("id", "5d4b2f67-ee47-4a84-947d-d9b65d94e3ab");
                w3.put("instanceID", 2);
                w3.put("cls", "d.cms.ui.component.Dashboard.Card");
                w3.put("bundle", "userprofile.js");

                JSONArray widgets = new JSONArray();
                widgets.put(w2);
                widgets.put(w1);
                widgets.put(w3);

                response.put("data", widgets);
                sendMessage.accept(response);

            } catch (JSONException ex){
                logRef.get().log(LogService.LOG_ERROR, "JSON Write Error", ex);
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

        Consumer<JSONObject> sendMessage = (Consumer<JSONObject>) event.getProperty("sendMessage");
        JSONObject message = (JSONObject) event.getProperty("message");

        try {
            JSONArray cmdList = message.getJSONArray("parameters");
            for( int i = 0 ; i < cmdList.length(); ++i) {
                JSONObject cmdObject = cmdList.getJSONObject(i);
                String command = cmdObject.getString("command");
                if (commands.containsKey(command))
                    commands.get(command).accept(message, sendMessage);
            }
        } catch (JSONException e) {
            logRef.get().log(LogService.LOG_ERROR, "JSON READ Error", e);
        }
    }
}
