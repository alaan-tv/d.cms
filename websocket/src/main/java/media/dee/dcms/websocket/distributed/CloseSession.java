package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.distributed.session.RemoteSession;

public class CloseSession extends AbstractTask {

    private RemoteSession session;

    public CloseSession(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        getSessionManager().closeSession(session);
    }
}