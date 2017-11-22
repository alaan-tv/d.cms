package media.dee.dcms.websocket.birdge;

import media.dee.dcms.websocket.WebSocketDispatcher;
import media.dee.dcms.websocket.WebSocketService;

import javax.websocket.DeploymentException;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketServiceDispatcher implements WebSocketDispatcher, WebSocketService {

    private final ConcurrentHashMap<String, Class<?>> endpoints = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @Override
    public void addEndpoint(Class<?> endpoint) throws DeploymentException {
        ServerEndpoint serverEndpoint = endpoint.getAnnotation(ServerEndpoint.class);
        if( serverEndpoint == null )
            throw new IllegalArgumentException(String.format("class %s should be annotated with @ServerEndpoint annotation", endpoint.getName()));
        endpoints.put(serverEndpoint.value(), endpoint);
    }

    @Override
    public void addEndpoint(ServerEndpointConfig config) throws DeploymentException {
        Class<?> endpoint = config.getEndpointClass();
        endpoints.put(config.getPath(), endpoint);
    }

    @Override
    public void removeEndpoint(Class<?> endpoint) {
        ServerEndpoint serverEndpoint = endpoint.getAnnotation(ServerEndpoint.class);
        if( serverEndpoint == null )
            throw new IllegalArgumentException(String.format("class %s should be annotated with @ServerEndpoint annotation", endpoint.getName()));
        endpoints.remove(serverEndpoint.value());
    }

    @Override
    public void open(String path, Session session) {
        Class<?> endpointClass = endpoints.get(path);
        if( endpointClass == null )
            return;
        //instate object
        Object instance = null;
        synchronized (instances) {
            instance = instances.get(endpointClass);
            if (instance == null) {
                try {
                    instance = endpointClass.newInstance();
                    instances.put(endpointClass, instance);
                } catch (IllegalAccessException |InstantiationException e) {
                    e.printStackTrace(System.err);
                    return;
                }
            }
        }
        for(Method method : endpointClass.getMethods()){
            OnOpen onOpen = method.getAnnotation(OnOpen.class);
            if( onOpen != null )
                try {
                    method.invoke(instance, session);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void close(String path, Session session) {

    }

    @Override
    public void onError(String path, Throwable error) {

    }

    @Override
    public void handleMessage(String path, String message, Session session) {

    }
}
