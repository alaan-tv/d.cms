package media.dee.dcms.admin.websocket;

import media.dee.dcms.admin.services.AdminWebsocketDispatcher;
import media.dee.dcms.websocket.WebsocketEndpoint;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class CommunicationWSEndpoint extends WebsocketEndpoint {

    public CommunicationWSEndpoint(){
        super(AdminWebsocketDispatcher.class);
    }

}
