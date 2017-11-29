package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.webapp.cms.MenuItem;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("cms")
public class WebSocketEndpoint {

    private Map<String, Session> sessionMap = new HashMap<>();
    private ServiceTracker<MenuItem, MenuItem> serviceTracker;
    final Bundle bundle;
    final BundleContext bundleContext;

    public WebSocketEndpoint(){
        bundle = FrameworkUtil.getBundle(this.getClass());
        bundleContext = bundle.getBundleContext();
        serviceTracker = new ServiceTracker<MenuItem, MenuItem>(bundleContext, MenuItem.class, null){
            @Override
            public MenuItem addingService(ServiceReference<MenuItem> reference) {
                MenuItem menuItem = bundleContext.getService(reference);
                menuItem.getJavascriptModules()
                        .forEach( module -> installModule(module) );
                return super.addingService(reference);
            }

            @Override
            public void removedService(ServiceReference<MenuItem> reference, MenuItem menuItem) {
                menuItem.getJavascriptModules()
                        .forEach( module -> unInstallModule(module) );
                super.removedService(reference, menuItem);
            }
        };
        serviceTracker.open();
    }

    private void installModule(String module){
        sessionMap.forEach( (id, session) -> sendMessage(session, String.format("{\"action\": \"bundle.install\", \"bundle\": \"%s\"}", module)) );
    }

    private void unInstallModule(String module){
        sessionMap.forEach( (id, session) -> sendMessage(session, String.format("{\"action\": \"bundle.uninstall\", \"bundle\": \"%s\"}", module)) );
    }

    @Override
    protected void finalize() throws Throwable {
        serviceTracker.close();
        super.finalize();
    }

    private void log(int level, String message){
        BundleContext context = FrameworkUtil.getBundle(WebSocketEndpoint.class).getBundleContext();
        ServiceReference<LogService> ref = context.getServiceReference(LogService.class);
        if (ref != null) {
            LogService log = context.getService(ref);
            log.log(level, message);
        }
    }

    private void sendMessage(Session session, String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            sessionMap.remove(session.getId());
            e.printStackTrace();
        }
    }

    @OnOpen
    public void open(Session session) {
        log(LogService.LOG_INFO, String.format("[WS] new session with id: %s", session.getId()));
        sessionMap.put(session.getId(), session);
        sendMessage(session, "{\"action\": \"bundle.install\", \"bundle\": \"js/layout/Menubar\"}" );

        try {
            bundleContext.getServiceReferences(MenuItem.class, null)
                    .forEach( reference -> {
                        MenuItem menuItem = bundleContext.getService(reference);
                        menuItem.getJavascriptModules().forEach(this::installModule);
                    });
        } catch (InvalidSyntaxException e) {
            e.printStackTrace(System.err);
        }
    }

    @OnClose
    public void close(Session session) {
        log(LogService.LOG_INFO, String.format("[WS] session close with id: %s", session.getId()));
        sessionMap.remove(session.getId());
    }

    @OnError
    public void onError(Throwable error) {
        log(LogService.LOG_INFO, String.format("[WS] Error: %s", error.toString() ));
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        log(LogService.LOG_INFO, String.format("[WS] session[id=%s] sent message: %s", session.getId(), message));
        sessionMap.forEach( (id, s) -> sendMessage(s, message) );
    }
}
