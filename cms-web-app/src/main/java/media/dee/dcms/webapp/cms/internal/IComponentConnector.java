package media.dee.dcms.webapp.cms.internal;


import org.eclipse.jetty.websocket.api.Session;

public interface IComponentConnector {
    void newSession(Session session);
}
