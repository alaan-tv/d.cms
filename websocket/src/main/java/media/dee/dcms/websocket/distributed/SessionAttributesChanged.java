package media.dee.dcms.websocket.distributed;

import media.dee.dcms.websocket.distributed.session.RemoteSession;

public class SessionAttributesChanged extends AbstractTask {
    private RemoteSession session;

    public SessionAttributesChanged(RemoteSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        getSessionManager().changeSessionAttributes(session);
    }
}