package org.blep.spring.sql.stats;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

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

    @Test
    public void testGetLogWriter() throws Exception {
        dataSource.getLogWriter();
        
    }
}
