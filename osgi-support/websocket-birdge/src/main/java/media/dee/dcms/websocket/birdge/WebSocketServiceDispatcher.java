package media.dee.dcms.websocket.birdge;

import media.dee.dcms.websocket.WebSocketDispatcher;
import media.dee.dcms.websocket.WebSocketEndpoint;
import media.dee.dcms.websocket.WebSocketService;
import org.osgi.service.component.annotations.Component;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketServiceDispatcher implements WebSocketDispatcher, WebSocketService {

    private final ConcurrentHashMap<String, WebSocketEndpoint> endpoints = new ConcurrentHashMap<>();

    @Override
    public void addEndpoint(WebSocketEndpoint endpoint) throws DeploymentException {
        ServerEndpoint serverEndpoint = endpoint.getClass().getAnnotation(ServerEndpoint.class);
        if( serverEndpoint == null )
            throw new IllegalArgumentException(String.format("class %s should be annotated with @ServerEndpoint annotation", endpoint.getClass().getName()));
        endpoints.put(serverEndpoint.value(), endpoint);
    }

    @Override
    public void removeEndpoint(WebSocketEndpoint endpoint) {
        ServerEndpoint serverEndpoint = endpoint.getClass().getAnnotation(ServerEndpoint.class);
        if( serverEndpoint == null )
            throw new IllegalArgumentException(String.format("class %s should be annotated with @ServerEndpoint annotation", endpoint.getClass().getName()));
        endpoints.remove(serverEndpoint.value());
    }


    @Override
    public void open(String path, Session session) {
        WebSocketEndpoint instance = endpoints.get(path);
        instance.open(path, session);
    }

    @Override
    public void close(String path, Session session) {
        WebSocketEndpoint instance = endpoints.get(path);
        instance.close(path, session);
    }

    @Override
    public void onError(String path, Throwable error) {
        WebSocketEndpoint instance = endpoints.get(path);
        instance.onError(path, error);
    }

    @Override
    public void handleMessage(String path, String message, Session session) {
        WebSocketEndpoint instance = endpoints.get(path);
        instance.handleMessage(path, message, session);
    }
}
