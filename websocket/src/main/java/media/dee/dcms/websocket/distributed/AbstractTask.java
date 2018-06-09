package media.dee.dcms.websocket.distributed;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import media.dee.dcms.websocket.SessionManager;
import media.dee.dcms.websocket.impl.ClusterSessionManager;

import java.io.Serializable;

public abstract class AbstractTask implements Runnable, Serializable, HazelcastInstanceAware {

    private HazelcastInstance hazelcastNode;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastNode = hazelcastInstance;
    }

    public SessionManager getSessionManager() {
        Object o = hazelcastNode.getUserContext().get(ClusterSessionManager.class.getName());
        return (ClusterSessionManager) o;
    }
}