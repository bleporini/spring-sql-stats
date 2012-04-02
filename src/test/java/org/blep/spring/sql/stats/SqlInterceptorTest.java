package org.blep.spring.sql.stats;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author blep
 *         Date: 23/03/12
 *         Time: 08:12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sql-stats-context.xml", "classpath:test-application-context.xml"})
public class SqlInterceptorTest {
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private StatController controller;

    @Test
    public void testGetLogWriter() throws Exception {
        dataSource.getLogWriter();
    }

    @Test
    public void testShouldTime() throws Exception {
        controller.startRecording();
        
        dataSource.getConnection().close();

        long averageConnectionTiming = controller.getAverageConnectionTiming();
        System.out.println("controller.getAverageConnectionTiming() = " + averageConnectionTiming);

        assertTrue(averageConnectionTiming > 0);
        
        dataSource.getConnection().close();
        System.out.println("controller.getAverageConnectionTiming() = " + controller.getAverageConnectionTiming());

        assertFalse(controller.getAverageConnectionTiming() == averageConnectionTiming);
        
    }

    
}
