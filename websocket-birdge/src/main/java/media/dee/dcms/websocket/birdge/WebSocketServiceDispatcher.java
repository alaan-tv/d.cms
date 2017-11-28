package media.dee.dcms.websocket.birdge;

import media.dee.dcms.websocket.WebSocketDispatcher;
import media.dee.dcms.websocket.WebSocketService;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.annotation.Annotation;
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
        instateEndpointInstance(serverEndpoint.value());
    }

    @Override
    public void addEndpoint(ServerEndpointConfig config) throws DeploymentException {
        Class<?> endpoint = config.getEndpointClass();
        endpoints.put(config.getPath(), endpoint);
        instateEndpointInstance(config.getPath());
    }

    @Override
    public void removeEndpoint(Class<?> endpoint) {
        ServerEndpoint serverEndpoint = endpoint.getAnnotation(ServerEndpoint.class);
        if( serverEndpoint == null )
            throw new IllegalArgumentException(String.format("class %s should be annotated with @ServerEndpoint annotation", endpoint.getName()));
        endpoints.remove(serverEndpoint.value());
        instances.remove(endpoint);
    }

    private void instateEndpointInstance(String path){
        Class<?> endpointClass = endpoints.get(path);
        if( endpointClass == null )
            return;
        //instate object
        Object instance = instances.get(endpointClass);
        if (instance == null) {
            try {
                instance = endpointClass.newInstance();
                instances.put(endpointClass, instance);
            } catch (IllegalAccessException |InstantiationException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private <T extends Annotation> Method getEndpointMethod(Class endpointClass, Class<T> annotationClass){
        for(Method method : endpointClass.getMethods()){
            T annotation = method.getAnnotation(annotationClass);
            if( annotation != null )
                return method;
        }
        return null;
    }

    private void proxyCall(String path, Class<? extends  Annotation> annotation, Object... args){
        Class<?> endpoint = endpoints.get(path);
        Object instance = instances.get(endpoint);
        if( instance == null )
            return;
        Method method = getEndpointMethod(instance.getClass(), annotation);
        if( method == null )
            return;
        try {
            method.invoke(instance, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(String path, Session session) {
        proxyCall(path, OnOpen.class, session);
    }

    @Override
    public void close(String path, Session session) {
        proxyCall(path, OnClose.class, session);
    }

    @Override
    public void onError(String path, Throwable error) {
        proxyCall(path, OnError.class, error);
    }

    @Override
    public void handleMessage(String path, String message, Session session) {
        proxyCall(path, OnMessage.class, message, session);
    }
}
