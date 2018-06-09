package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.distributed.session.LocalSession;
import media.dee.dcms.websocket.distributed.session.RemoteSession;

public class SessionConnected extends AbstractTask {
    private RemoteSession session;

    public SessionConnected(LocalSession session) {
        this.session = new RemoteSession(session);
    }

    @Override
    public void run() {
        SessionManager sessionManager = getSessionManager();
        this.session.setSessionManager(sessionManager);
        sessionManager.addSession(session);
    }
}