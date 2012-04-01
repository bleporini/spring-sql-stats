package org.blep.spring.sql.stats;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.Statement;

import static junit.framework.Assert.*;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 09:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sql-stats-context.xml", "classpath:test-application-context.xml"})
public class StatementWrapperTest {

    @Autowired
    private StatController controller;

    @Autowired
    private DataSource dataSource;
    private static final String SQL = "select * from INFORMATION_SCHEMA.TABLES";
    private static final String SQL_UPDATE = "update INFORMATION_SCHEMA.TABLES set table_name='tutu' where table_name='do not exist'";

    @Test
    public void testShoutNotRenaming() throws Exception {
        WeakReference<Statement> reference = null;
        controller.startRecording();

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        reference = new WeakReference<Statement>(statement);
        assertNotNull(reference.get());
        assertEquals(statement, reference.get());

//        JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");
        statement= null;

        System.gc();
        System.gc();
        System.gc();
        System.gc();

//        JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");

        assertNull(reference.get());

    }

/*
    @Test
    public void testShouldNotInstanciate() throws Exception {
        controller.startRecording();
        try {
            new StatementWrapper();
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(),"No statement findable");
        }

        dataSource.getConnection().createStatement();
        try {
            new StatementWrapper();
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(),"No statement findable");
        }

    }
*/

    @Test
    public void shouldNotIncrementQueryCount() throws Exception {
        controller.stopRecording();
        controller.resetCounter();
        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.execute("select * from INFORMATION_SCHEMA.TABLES");

        statement.close();
        connection.close();

        assertEquals(0, controller.getQueryCount());

    }


    @Test
    public void testShouldIncrementQueryCount() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.execute(SQL);

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteKeys() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.execute(SQL, Statement.NO_GENERATED_KEYS);

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteColumnIndexes() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.execute(SQL, new int[]{});

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteColumnNames() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.execute(SQL, new String[]{});

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecutequery() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.executeQuery(SQL);

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteUpdate() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate(SQL_UPDATE);

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteUpdateGeneratedKey() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate(SQL_UPDATE, 0);

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteUpdateGeneratedColIdx() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate(SQL_UPDATE, new int[]{1});

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }

    @Test
    public void testExecuteUpdateGeneratedColNames() throws Exception {
        controller.startRecording();
        controller.resetCounter();
        assertEquals(0, controller.getQueryCount());

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate(SQL_UPDATE, new String[]{});

        statement.close();
        connection.close();

        assertEquals(1, controller.getQueryCount());

    }


}
