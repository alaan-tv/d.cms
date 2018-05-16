package media.dee.dcms.websocket.impl.messages;

import media.dee.dcms.websocket.SessionManager;

import java.io.Serializable;

public interface Message extends Serializable {
    void dispatch(SessionManager sessionManager);
}
