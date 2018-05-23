package media.dee.dcms.websocket.impl.messages;

import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.ClusterSessionManager;
import media.dee.dcms.websocket.impl.session.LocalSession;
import media.dee.dcms.websocket.impl.session.RemoteSession;

public class SessionConnected implements Message {
    private RemoteSession session;

    public SessionConnected(LocalSession session) {
        this.session = new RemoteSession(session);
    }

    @Override
    public void dispatch(SessionManager sessionManager) {
        ClusterSessionManager clusterSessionManager = (ClusterSessionManager) sessionManager;
        this.session.setSessionManager( clusterSessionManager );
        sessionManager.addSession( session );
    }
}
