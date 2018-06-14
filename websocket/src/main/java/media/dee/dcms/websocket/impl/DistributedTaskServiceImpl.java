package media.dee.dcms.websocket.impl;

import com.hazelcast.core.HazelcastInstance;
import media.dee.dcms.websocket.DistributedTaskService;
import media.dee.dcms.websocket.distributed.AbstractTask;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;

import java.util.Set;

@Component
public class DistributedTaskServiceImpl implements DistributedTaskService {


    private LogService log;
    private HazelcastInstance hazelcastNode;

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setLogService(LogService log) {
        this.log = log;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    void setHazelcastNode(HazelcastInstance instance) {
        this.hazelcastNode = instance;
    }

    @Override
    public void sendToMember(AbstractTask task, String memberUuid) {
        hazelcastNode.getExecutorService(WS_EXECUTOR_SERVICE).execute(task,
                member -> member.getUuid().equals(memberUuid));
        log.log(LogService.LOG_DEBUG, "Task [" + task + "] sent to memberUuid: " + memberUuid);
    }

    @Override
    public void broadcast(AbstractTask task, Set<String> excludeMembersUuid) {
        if (hazelcastNode.getCluster().getMembers().size() > 1) {
            hazelcastNode.getExecutorService(WS_EXECUTOR_SERVICE).execute(task,
                    member -> !excludeMembersUuid.contains(member.getUuid()));
            log.log(LogService.LOG_DEBUG, "Task [" + task + "] broadcasted excluding Uuid: " + excludeMembersUuid);
        }
    }

    @Override
    public void broadcast(AbstractTask task) {
        hazelcastNode.getExecutorService(WS_EXECUTOR_SERVICE).execute(task);
        log.log(LogService.LOG_DEBUG, "Task [" + task + "] broadcasted");
    }
}