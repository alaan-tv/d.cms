package media.dee.dcms.admin.services;

import com.fasterxml.jackson.databind.JsonNode;
import media.dee.dcms.websocket.WebsocketDispatcher;

public interface AdminWebsocketDispatcher extends WebsocketDispatcher {

    /**
     * send a message to directly connected clients to the current node.
     * @param message message to be sent to client
     * @return number of sent messages
     */
    long send(JsonNode message);

}
