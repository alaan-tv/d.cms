package media.dee.dcms.websocket.impl.messages;

import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.session.RemoteSession;

public class SessionClose implements Message {

    private RemoteSession session;

    public SessionClose(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void dispatch(SessionManager sessionManager) {
        sessionManager.sessionClosed(this.session);
    }
}
