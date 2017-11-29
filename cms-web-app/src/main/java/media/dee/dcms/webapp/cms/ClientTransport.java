package media.dee.dcms.webapp.cms;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public interface ClientTransport {

    class Filter extends HashMap<String, String> {
        public boolean apply(JSONObject message){
            return this.entrySet().stream()
                    .map( entry->{
                        try {
                            return entry.getValue().equals(message.get(entry.getKey()));
                        } catch (JSONException e) {
                            return false;
                        }
                    })
                    .reduce( true, (entry, value) -> entry && value);
        }
    }

    void send(String clientID, JSONObject message);
    void sendAll(JSONObject message);

    void addListener(ClientTransportListener listener);
    void removeListener(ClientTransportListener listener);
}
