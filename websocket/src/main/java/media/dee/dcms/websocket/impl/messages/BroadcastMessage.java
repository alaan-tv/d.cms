package media.dee.dcms.websocket.impl.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import media.dee.dcms.websocket.SessionManager;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

public class BroadcastMessage implements Message {
    private String message;
    private Predicate<Map<String, Object>> filter;


    public BroadcastMessage(JsonNode message, Predicate<Map<String, Object>> filter) {
        this.message = message.toString();
        this.filter = filter;
    }

    @Override
    public void dispatch(SessionManager sessionManager) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            sessionManager.send(mapper.readValue(message, JsonNode.class), this.filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
