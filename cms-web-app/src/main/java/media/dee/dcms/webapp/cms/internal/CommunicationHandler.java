package media.dee.dcms.webapp.cms.internal;

import org.eclipse.jetty.websocket.api.Session;

import javax.json.JsonObject;

public interface CommunicationHandler {
    void registerConnection(Session session);
    void unregisterConnection(Session session);
    void processCommand(Session session, String message);
    <T extends JsonObject> void sendAll(T message);
}
