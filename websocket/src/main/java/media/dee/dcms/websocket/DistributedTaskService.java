package media.dee.dcms.websocket;

import media.dee.dcms.websocket.distributed.AbstractTask;

import java.util.Set;

public interface DistributedTaskService {

    String WS_EXECUTOR_SERVICE = "ws-sync";

    /**
     * Send distributed task to specified cluster member.
     *
     * @param task       Task to be sent
     * @param memberUuid ID of the cluster member
     */
    void sendToMember(AbstractTask task, String memberUuid);

    /**
     * Broadcast distributed task to all member excluding specified.
     *
     * @param task               Task to be sent
     * @param excludeMembersUuid Set of members IDs which will be excluded from broadcasting
     */
    void broadcast(AbstractTask task, Set<String> excludeMembersUuid);

    /**
     * Broadcast distributed task to all member excluding specified.
     *
     * @param task               Task to be sent
     * @param excludeMemberUuid Member IDs which will be excluded from broadcasting
     */
    void broadcast(AbstractTask task, String excludeMemberUuid);

    /**
     * Broadcast distributed task to all cluster members
     *
     * @param task Task to be sent
     */
    void broadcast(AbstractTask task);
}