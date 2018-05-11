package media.dee.dcms.webapp.cms.internal;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.websocket.api.Session;

public interface CommunicationHandler {
    void registerConnection(Session session);
    void unregisterConnection(Session session);
    void processCommand(Session session, String message);
    void sendAll(JsonNode message);
}
