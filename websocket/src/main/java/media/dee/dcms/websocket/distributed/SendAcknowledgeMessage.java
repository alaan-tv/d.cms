package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.ClusterSessionManager;

public class SendAcknowledgeMessage extends AbstractTask {
    private String id;


    public SendAcknowledgeMessage(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        SessionManager sessionManager = getSessionManager();
        if(sessionManager instanceof ClusterSessionManager)
            ((ClusterSessionManager)sessionManager).messageAcknowledged(id);
    }
}
