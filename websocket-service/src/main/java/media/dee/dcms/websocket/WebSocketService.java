package media.dee.dcms.websocket;

import javax.websocket.server.ServerContainer;

public interface WebSocketService extends ServerContainer{
   void removeEndpoint(String path);
}
