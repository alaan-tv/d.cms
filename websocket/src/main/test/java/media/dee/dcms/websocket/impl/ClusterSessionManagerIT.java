package media.dee.dcms.websocket.impl;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import media.dee.dcms.websocket.distributed.session.LocalSession;
import media.dee.dcms.websocket.distributed.session.RemoteSession;
import org.eclipse.jetty.websocket.api.Session;
import org.osgi.service.log.LogService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

// TODO: 6/19/18 refactoring needed
public class ClusterSessionManagerIT {
    private HazelcastInstance hazelcastInstance1;
    private HazelcastInstance hazelcastInstance2;
    private HazelcastInstance hazelcastInstance3;
    private ClusterSessionManager sessionManager1;
    private ClusterSessionManager sessionManager2;
    private ClusterSessionManager sessionManager3;

    @BeforeClass
    public void startup() {
        hazelcastInstance1 = Hazelcast.newHazelcastInstance();
        hazelcastInstance2 = Hazelcast.newHazelcastInstance();
        hazelcastInstance3 = Hazelcast.newHazelcastInstance();

        sessionManager1 = new ClusterSessionManager();
        sessionManager1.setHazelcastNode(hazelcastInstance1);

        sessionManager2 = new ClusterSessionManager();
        sessionManager2.setHazelcastNode(hazelcastInstance2);

        sessionManager3 = new ClusterSessionManager();
        sessionManager3.setHazelcastNode(hazelcastInstance3);

        hazelcastInstance1.getUserContext().put(sessionManager1.getClass().getName(), sessionManager1);
        hazelcastInstance2.getUserContext().put(sessionManager2.getClass().getName(), sessionManager2);
        hazelcastInstance3.getUserContext().put(sessionManager3.getClass().getName(), sessionManager3);

        LogService logService = mock(LogService.class);
        sessionManager1.setLogService(logService);
        sessionManager2.setLogService(logService);
        sessionManager3.setLogService(logService);

        DistributedTaskServiceImpl taskService = new DistributedTaskServiceImpl();
        taskService.setHazelcastNode(hazelcastInstance1);
        taskService.setLogService(logService);
        sessionManager1.setDistributedTaskService(taskService);

        DistributedTaskServiceImpl taskService2 = new DistributedTaskServiceImpl();
        taskService2.setHazelcastNode(hazelcastInstance2);
        taskService2.setLogService(logService);
        sessionManager2.setDistributedTaskService(taskService2);

        DistributedTaskServiceImpl taskService3 = new DistributedTaskServiceImpl();
        taskService3.setHazelcastNode(hazelcastInstance3);
        taskService3.setLogService(logService);
        sessionManager3.setDistributedTaskService(taskService3);
    }

    @AfterClass
    public void shutdown() {
        hazelcastInstance1.shutdown();
        hazelcastInstance2.shutdown();
        hazelcastInstance3.shutdown();
    }

    @Test
    public void testSessionsClustering() throws Exception {
        Session session = mock(Session.class);
        sessionManager1.sessionConnected(session);
        Thread.sleep(100);

        Map<Session, media.dee.dcms.websocket.Session> localSessions = sessionManager1.getLocallyConnectedSessions();
        Assert.assertEquals(localSessions.size(), 1);

        Map<String, media.dee.dcms.websocket.Session> clusterSessions = sessionManager2.getClusterSessions();
        Assert.assertEquals(clusterSessions.size(), 1);

        Map<String, media.dee.dcms.websocket.Session> clusterSessions3 = sessionManager3.getClusterSessions();
        Assert.assertEquals(clusterSessions3.size(), 1);

        sessionManager1.sessionClosed(session);
        Thread.sleep(100);

        Map<org.eclipse.jetty.websocket.api.Session, media.dee.dcms.websocket.Session> local = sessionManager1.getLocallyConnectedSessions();
        Assert.assertEquals(local.size(), 0);

        Map<String, media.dee.dcms.websocket.Session> clusterSessionsClosed2 = sessionManager2.getClusterSessions();
        Assert.assertEquals(clusterSessionsClosed2.size(), 0);

        Map<String, media.dee.dcms.websocket.Session> clusterSessionsClosed3 = sessionManager3.getClusterSessions();
        Assert.assertEquals(clusterSessionsClosed3.size(), 0);
    }

    @Test
    public void testRemoteSessionClose() throws Exception {
        Session session = mock(Session.class);
        sessionManager1.sessionConnected(session);
        Thread.sleep(100);

        Map<Session, media.dee.dcms.websocket.Session> localSessions = sessionManager1.getLocallyConnectedSessions();
        Assert.assertEquals(localSessions.size(), 1);

        Map<String, media.dee.dcms.websocket.Session> clusterSessions = sessionManager2.getClusterSessions();
        Assert.assertEquals(clusterSessions.size(), 1);

        Map<String, media.dee.dcms.websocket.Session> clusterSessions3 = sessionManager3.getClusterSessions();
        Assert.assertEquals(clusterSessions3.size(), 1);

        LocalSession localSession = (LocalSession) localSessions.get(session);
        RemoteSession remoteSession = new RemoteSession(sessionManager1, localSession);
        remoteSession.close();
        Thread.sleep(100);

        Map<org.eclipse.jetty.websocket.api.Session, media.dee.dcms.websocket.Session> local = sessionManager1.getLocallyConnectedSessions();
        Assert.assertEquals(local.size(), 0);

        Map<String, media.dee.dcms.websocket.Session> clusterSessionsClosed2 = sessionManager2.getClusterSessions();
        Assert.assertEquals(clusterSessionsClosed2.size(), 0);

        Map<String, media.dee.dcms.websocket.Session> clusterSessionsClosed3 = sessionManager3.getClusterSessions();
        Assert.assertEquals(clusterSessionsClosed3.size(), 0);
    }

    //    @Test
    public void testLocalSessionClose() throws Exception {
        //todo implement test with local session closing as LocalSession.close().
        //todo to implement this - close session message should be broadcasted from LocalSession.close() method
    }
}