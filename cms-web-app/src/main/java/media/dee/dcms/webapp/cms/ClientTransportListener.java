package media.dee.dcms.webapp.cms;

import org.json.JSONObject;

public interface ClientTransportListener {

    default void onOpen(ClientTransport transport, String clientID){}

    default void onClose(ClientTransport transport, String clientID){ }

    default void onMessage(ClientTransport transport, String clientID, JSONObject message){ }
}
