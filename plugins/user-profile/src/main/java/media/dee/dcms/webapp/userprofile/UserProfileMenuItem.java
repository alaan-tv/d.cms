package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.webapp.cms.components.GUIComponent;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


@AdminModule("/webapp/userprofile")
@Component(property= EventConstants.EVENT_TOPIC + "=component/userpofile")
public class UserProfileMenuItem implements GUIComponent, EventHandler {
    private final AtomicReference<LogService> logRef = new AtomicReference<>();


    @Reference
    void setLogService( LogService log ) {
        logRef.set(log);
    }


    @Activate
    void activate(ComponentContext ctx) {
        LogService log = logRef.get();
        log.log(LogService.LOG_INFO, "UserProfileMenuItem Activated");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(Event event) {

        Consumer<String> sendMessage = (Consumer<String>) event.getProperty("sendMessage");
        JSONObject message = (JSONObject) event.getProperty("message");

        int rest = 100;
        LinkedList<Integer> data = new LinkedList<>();

        for( int i = 0; i < 3; ++i) {
            int value = (int)(Math.random() * rest);
            rest -= value;
            data.add(value);
        }


        sendMessage.accept(String.format("\"datasets\": [{\n" +
                "    \"data\": [%d,%d,%d] ,\n" +
                "    \"backgroundColor\": [\n" +
                "        \"#FF6384\",\n" +
                "        \"#36A2EB\",\n" +
                "        \"#FFCE56\"\n" +
                "    ],\n" +
                "    \"hoverBackgroundColor\": [\n" +
                "        \"#FF6384\",\n" +
                "        \"#36A2EB\",\n" +
                "        \"#FFCE56\"\n" +
                "    ]\n" +
                "}]", data.get(0), data.get(1), data.get(2)));
    }
}
