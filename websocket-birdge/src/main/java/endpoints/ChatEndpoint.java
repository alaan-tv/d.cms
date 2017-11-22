package endpoints;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("chat")
public class ChatEndpoint {

    private void log(int level, String message){
        BundleContext context = FrameworkUtil.getBundle(ChatEndpoint.class).getBundleContext();
        ServiceReference<LogService> ref = context.getServiceReference(LogService.class);
        if (ref != null) {
            LogService log = context.getService(ref);
            log.log(level, message);
        }
    }
    @OnOpen
    public void open(Session session) {
        log(LogService.LOG_INFO, String.format("[WS] new session with id: %s", session.getId()));
        try {
            session.getBasicRemote().sendText("{\"action\": \"greetings\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close(Session session) {
        log(LogService.LOG_INFO, String.format("[WS] session close with id: %s", session.getId()));
    }

    @OnError
    public void onError(Throwable error) {
        log(LogService.LOG_INFO, String.format("[WS] Error: %s", error.toString() ));
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        log(LogService.LOG_INFO, String.format("[WS] session[id=%s] sent message: %s", session.getId(), message));
    }
}
