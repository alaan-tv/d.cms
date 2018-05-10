package media.dee.dcms.webapp.cms.internal.ws;

import media.dee.dcms.webapp.cms.internal.CommunicationHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import java.util.function.Consumer;

@WebSocket
public class CommunicationWSEndpoint {

    private BundleContext bundleContext;

    public CommunicationWSEndpoint(){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        this.bundleContext = bundle.getBundleContext();
    }

    private void handle(Consumer<CommunicationHandler> consumer){
        ServiceReference<CommunicationHandler> serviceReference = this.bundleContext.getServiceReference(CommunicationHandler.class);
        CommunicationHandler handler = this.bundleContext.getService(serviceReference);
        consumer.accept(handler);
        this.bundleContext.ungetService(serviceReference);
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        handle( (handler)-> handler.registerConnection(session) );
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        handle( (handler)-> handler.unregisterConnection(session) );
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) {
        handle( (handler)-> handler.processCommand(session, message) );
    }

}
