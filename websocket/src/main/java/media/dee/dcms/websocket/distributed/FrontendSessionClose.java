package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.distributed.session.RemoteSession;

/**
 * Frontend issued task to close session
 */
public class FrontendSessionClose extends AbstractTask {

    private RemoteSession session;

    public FrontendSessionClose(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        getSessionManager().sessionClosed(this.session);
    }
}
