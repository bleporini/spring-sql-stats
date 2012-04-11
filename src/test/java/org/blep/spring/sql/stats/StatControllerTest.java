package org.blep.spring.sql.stats;

import lombok.AllArgsConstructor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.sql.DataSource;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * @author blep
 *         Date: 04/04/12
 *         Time: 07:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sql-stats-context.xml", "classpath:test-application-context.xml"})
public class StatControllerTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StatController controller;

    @Test
    public void testSlowQueries() throws Exception {
        controller.recordQueryTime("sql1", 1001l);
        controller.recordQueryTime("sql2", 1002l);
        controller.recordQueryTime("sql3", 1003l);
        controller.recordQueryTime("sql4", 1004l);
        controller.recordQueryTime("sql5", 1005l);
        controller.recordQueryTime("sql6", 1006l);
        controller.recordQueryTime("sql7", 1007l);
        controller.recordQueryTime("sql8", 1008l);
        controller.recordQueryTime("sql9", 1009l);
        controller.recordQueryTime("sql10", 1010l);

        controller.recordQueryTime("select 1", 10l);
        controller.recordQueryTime("select 2", 2000l);

        assertFalse(controller.getSlowestQueries().containsKey("select 1"));
        assertTrue(controller.getSlowestQueries().containsKey("select 2"));
        assertEquals(controller.getSlowestQueries().get("select 2"), new Long(2000l));

//        fail("to be continued");
    }

    @AllArgsConstructor
    private static class QueryRecorder implements Runnable {
        final static Random random = new Random();
        private final StatController controller;
        private static final AtomicInteger cpt = new AtomicInteger(0);


        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                long l = random.nextLong()%100;
                controller.recordQueryTime("sql " + l, l);
                cpt.incrementAndGet();
            }
        }

    }

    @Rule
    public JunitTimer junitTimer = new JunitTimer();


    /**
     * Test for profiling
     *
     * @throws Exception
     */
    @Test
    public void testConcurrency() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(400);

        for (int i = 0; i < 100; i++) {
            executorService.submit(new QueryRecorder(controller));
        }

        /*boolean j = true;
        while (j) {
            executorService.submit(new QueryRecorder(controller));
        }*/

//        Thread.sleep(3000);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        System.out.println("QR = " + QueryRecorder.cpt);
        assertEquals(QueryRecorder.cpt.get(), 10000);
//          Thread.sleep(30000);
    }

    @Test
    public void testShouldRetrieveMBean() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.blep.spring.sql.stats:name=statController,type=StatController");
        StatControllerMBean statController = MBeanServerInvocationHandler.newProxyInstance(mbs, name, StatControllerMBean.class, false);

        assertNotNull(statController);

//        assertEquals(0,statController.getActiveConnectionCount());

        statController.startRecording();
        statController.resetConnectionCounts();

        long activeConnectionCount = statController.getActiveConnectionCount();
        System.out.println("activeConnectionCount = " + activeConnectionCount);

        Connection connection = dataSource.getConnection();

        assertEquals(activeConnectionCount + 1, statController.getActiveConnectionCount());

        connection.close();

        assertEquals(activeConnectionCount, statController.getActiveConnectionCount());
    }


}
