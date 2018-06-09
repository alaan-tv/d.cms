package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.distributed.session.RemoteSession;

/**
 * Backend issued task to close session
 */
public class BackendSessionClose extends AbstractTask {

    private RemoteSession session;

    public BackendSessionClose(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        getSessionManager().closeSession(session);
    }
}