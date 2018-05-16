package media.dee.dcms.websocket.impl.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import media.dee.dcms.websocket.SessionManager;

import java.io.IOException;

public class BroadcastMessage implements Message {
    private String message;


    public BroadcastMessage(JsonNode message) {
        this.message = message.toString();
    }

    @Override
    public void dispatch(SessionManager sessionManager) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            sessionManager.send(mapper.readValue(message, JsonNode.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
