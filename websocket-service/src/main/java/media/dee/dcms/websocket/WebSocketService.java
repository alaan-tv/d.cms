package media.dee.dcms.websocket;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

public interface WebSocketService {
    void addEndpoint(Class<?> endpoint) throws DeploymentException;

    void addEndpoint(ServerEndpointConfig config) throws DeploymentException;

    void removeEndpoint(Class<?> endpoint);
}
