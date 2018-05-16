package media.dee.dcms.websocket.impl.messages;

import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.session.RemoteSession;

public class CloseSession implements Message {
    private RemoteSession session;
    public CloseSession(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void dispatch(SessionManager sessionManager) {
        sessionManager.closeSession(session);
    }
}
