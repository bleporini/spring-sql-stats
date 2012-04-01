package org.blep.spring.sql.stats;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author blep
 *         Date: 24/03/12
 *         Time: 13:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sql-stats-context.xml", "classpath:test-application-context.xml"})
public class PreparedStatementWrapperTest {
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private StatController controller;

    
    private static final String SQL = "select * from INFORMATION_SCHEMA.TABLES";


    @Test
    public void testNormal() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0,controller.getQueryCount());
        
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(SQL);
        preparedStatement.execute();
        preparedStatement.close();
        
        assertEquals(1, controller.getQueryCount());
        
    }

    @Test
    public void testShouldIncrement() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0,controller.getQueryCount());

        Statement statement = dataSource.getConnection().createStatement();
        statement.execute(SQL);
        statement.close();

        assertEquals(1,controller.getQueryCount());
    }
}
