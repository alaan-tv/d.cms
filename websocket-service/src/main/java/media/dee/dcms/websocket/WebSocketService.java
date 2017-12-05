package media.dee.dcms.websocket;

import javax.websocket.DeploymentException;

public interface WebSocketService {
    void addEndpoint(WebSocketEndpoint endpoint) throws DeploymentException;

    void removeEndpoint(WebSocketEndpoint endpoint);
}
