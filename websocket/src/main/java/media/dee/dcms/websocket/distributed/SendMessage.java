package media.dee.dcms.websocket.distributed;

import com.fasterxml.jackson.databind.JsonNode;
import media.dee.dcms.websocket.Session;
import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.ClusterSessionManager;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SendMessage extends AbstractTask {
    private String sessionId;
    private JsonNode data;
    private UUID uuid;


    public SendMessage(String id, JsonNode data, UUID uuid) {
        this.sessionId = id;
        this.data = data;
        this.uuid = uuid;
    }

    public SendMessage(String id, JsonNode data) {
        this(id, data, null);
    }

    @Override
    public void run() {
        SessionManager sessionManager = getSessionManager();
        Future<Void> result = sessionManager.send(sessionId, data);
        try {
            result.get();

            //send acknowledge message to callee node.
            if (uuid != null && sessionManager instanceof ClusterSessionManager) {
                ((ClusterSessionManager) sessionManager).sendAcknowledgeMessage(this.uuid.toString());
            }
        } catch (InterruptedException e) {
            /* send is canceled, ignore the exception */
        } catch (ExecutionException e) {
            /* failed to send the message, mainly the session is closed, remove the session */
            Session session = sessionManager.get(sessionId);
            session.close();
        }
    }
}
