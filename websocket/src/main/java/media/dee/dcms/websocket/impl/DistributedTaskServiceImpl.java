package media.dee.dcms.websocket.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiExecutionCallback;
import media.dee.dcms.websocket.DistributedTaskService;
import media.dee.dcms.websocket.distributed.AbstractTask;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Optional<Member> memberOptional = hazelcastNode.getCluster().getMembers()
                .stream().filter(member -> member.getUuid().equals(memberUuid))
                .findFirst();
        if (memberOptional.isPresent()) {
            hazelcastNode.getExecutorService(WS_EXECUTOR_SERVICE).submitToMember(task,
                    memberOptional.get(), new ExecutionCallback() {
                        @Override
                        public void onResponse(Object response) {
                            log.log(LogService.LOG_DEBUG, "Task " + task + " completed");
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            log.log(LogService.LOG_ERROR, "Task " + task + " failed. Cause: " + t.getCause());
                        }
                    });

            log.log(LogService.LOG_DEBUG, "Task [" + task + "] sent to memberUuid: " + memberUuid);
        }

    }

    @Override
    public void broadcast(AbstractTask task, Set<String> excludeMembersUuid) {
        if (hazelcastNode.getCluster().getMembers().size() > 1) {
            Collection<Member> members = hazelcastNode.getCluster().getMembers()
                    .stream().filter(member -> !excludeMembersUuid.contains(member.getUuid()))
                    .collect(Collectors.toSet());
            hazelcastNode.getExecutorService(WS_EXECUTOR_SERVICE).submitToMembers(task, members,
                    new MultiExecutionCallback() {
                        @Override
                        public void onResponse(Member member, Object value) {
                            log.log(LogService.LOG_DEBUG, "Task for member [" + member.getUuid() + "] completed");
                        }

                        @Override
                        public void onComplete(Map<Member, Object> values) {
                            log.log(LogService.LOG_DEBUG, "Task " + task + " completed for all members");
                        }
                    });
            log.log(LogService.LOG_DEBUG, "Task [" + task + "] broadcasted excluding Uuid: " + excludeMembersUuid);
        }
    }

    @Override
    public void broadcast(AbstractTask task) {
        hazelcastNode.getExecutorService(WS_EXECUTOR_SERVICE).submitToAllMembers(task,
                new MultiExecutionCallback() {
                    @Override
                    public void onResponse(Member member, Object value) {
                        log.log(LogService.LOG_DEBUG, "Task for member [" + member.getUuid() + "] completed");
                    }

                    @Override
                    public void onComplete(Map<Member, Object> values) {
                        log.log(LogService.LOG_DEBUG, "Task " + task + " completed for all members");
                    }
                });
        log.log(LogService.LOG_DEBUG, "Task [" + task + "] broadcasted");
    }
}