package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.distributed.session.RemoteSession;

public class SessionClose extends AbstractTask {

    private RemoteSession session;

    public SessionClose(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        getSessionManager().sessionClosed(this.session);
    }
}
