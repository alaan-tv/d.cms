package media.dee.dcms.websocket.impl.messages;

import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.ClusterSessionManager;

public class SendAcknowledgeMessage implements Message {
    private String id;


    public SendAcknowledgeMessage(String id) {
        this.id = id;
    }

    @Override
    public void dispatch(SessionManager sessionManager) {
        if(sessionManager instanceof ClusterSessionManager)
            ((ClusterSessionManager)sessionManager).messageAcknowledged(id);
    }
}
