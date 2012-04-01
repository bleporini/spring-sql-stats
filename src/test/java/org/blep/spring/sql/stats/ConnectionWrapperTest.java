package org.blep.spring.sql.stats;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Scanner;

import static junit.framework.Assert.*;

/**
 * @author blep
 *         Date: 22/03/12
 *         Time: 07:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:sql-stats-context.xml", "classpath:test-application-context.xml"})
public class ConnectionWrapperTest {


    @Autowired
    private StatController controller;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testNewConnection() throws Exception {
        controller.startRecording();
        controller.resetConnectionCounts();
        assertEquals(0, controller.getActiveConnectionCount());
        assertEquals(0, controller.getTotalConnectionUsed());

        Connection connection = dataSource.getConnection();

        assertEquals(1, controller.getActiveConnectionCount());
        assertEquals(1, controller.getTotalConnectionUsed());
        
        connection.close();

        assertEquals(0, controller.getActiveConnectionCount());
        assertEquals(1, controller.getTotalConnectionUsed());

    }



    @Test
    public void testShouldNotRetain() throws Exception {
        WeakReference<Connection> reference = null;
        controller.startRecording();

        Connection connection = dataSource.getConnection();
        reference = new WeakReference<Connection>(connection);
        assertNotNull(reference.get());
        assertEquals(connection, reference.get());

//        JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");
        connection = null;

        System.gc();
        System.gc();
        System.gc();
        System.gc();

//        JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");

        assertNull(reference.get());
       
    }



    @Test
    public void testReference() throws Exception {

        WeakReference<Wrapper> reference;
        {
            Wrapper wrapper = new Wrapper() {
                @Override
                public <T> T unwrap(Class<T> tClass) throws SQLException {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public boolean isWrapperFor(Class<?> aClass) throws SQLException {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }
            };
            reference = new WeakReference<Wrapper>(wrapper);
            wrapper = null;

        }
        System.gc();

/*
        Thread.sleep(100);
        System.gc();
        Thread.sleep(100);
        System.gc();
        Thread.sleep(100);
        System.gc();
*/


        assertNull(reference.get());

    }
}
