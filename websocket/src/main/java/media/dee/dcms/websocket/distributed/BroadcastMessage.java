package media.dee.dcms.websocket.distributed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class BroadcastMessage extends AbstractTask {
    private String message;


    public BroadcastMessage(JsonNode message) {
        this.message = message.toString();
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            getSessionManager().send(mapper.readValue(message, JsonNode.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
