package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.components.UUID;
import media.dee.dcms.webapp.cms.components.GUIComponent;
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


@AdminModule(value = "/webapp/userprofile", autoInstall = true)
@Component(property= EventConstants.EVENT_TOPIC + "=component/5d4b2f67-ee47-4a84-947d-d9b65d94e3ab")
@UUID("5d4b2f67-ee47-4a84-947d-d9b65d94e3ab")
public class ProfileProgresstem implements GUIComponent, EventHandler {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();
    private Map<String, BiConsumer<JSONObject, Consumer<JSONObject>>> commands = new HashMap<>();

    public ProfileProgresstem(){
        commands.put("getData", (message, sendMessage)->{
            int rest = 100;
            JSONArray data = new JSONArray();

            for( int i = 0; i < 3; ++i) {
                int value = (int)(Math.random() * rest);
                rest -= value;
                data.put(value);
            }

            JSONObject result = new JSONObject();
            JSONArray labels = new JSONArray();
            JSONObject dataset = new JSONObject();
            JSONArray datasets = new JSONArray();

            JSONArray backgroundColor = new JSONArray();
            JSONArray hoverBackgroundColor = new JSONArray();

            labels.put("Red");
            labels.put("Green");
            labels.put("Yellow");

            backgroundColor.put("#FF6384");
            backgroundColor.put("#36A2EB");
            backgroundColor.put("#FFCE56");

            hoverBackgroundColor.put("#FF6384");
            hoverBackgroundColor.put("#36A2EB");
            hoverBackgroundColor.put("#FFCE56");

            try {
                result.put("datasets", datasets);
                result.put("labels", labels);

                dataset.put("data", data);
                dataset.put("backgroundColor", backgroundColor);
                dataset.put("hoverBackgroundColor", hoverBackgroundColor);

                datasets.put(dataset);

                sendMessage.accept(result);

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
        log.log(LogService.LOG_INFO, "ProfileProgresstem Activated");
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
