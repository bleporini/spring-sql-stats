package org.blep.spring.sql.stats;

import lombok.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 10:26
 */
@Component
@Scope("prototype")
class StatementWrapper implements Statement {

    StatementWrapper(Statement statement) {
        delegate = statement;
    }

    private interface ExcludeMethods {

        ResultSet executeQuery(java.lang.String s) throws java.sql.SQLException;
        int executeUpdate(java.lang.String s) throws java.sql.SQLException;

        boolean execute(java.lang.String s) throws java.sql.SQLException;

        int executeUpdate(java.lang.String s, int i) throws java.sql.SQLException;

        int executeUpdate(java.lang.String s, int[] ints) throws java.sql.SQLException;

        int executeUpdate(java.lang.String s, java.lang.String[] strings) throws java.sql.SQLException;

        boolean execute(java.lang.String s, int i) throws java.sql.SQLException;

        boolean execute(java.lang.String s, int[] ints) throws java.sql.SQLException;

        boolean execute(java.lang.String s, java.lang.String[] strings) throws java.sql.SQLException;

    }

    @Autowired
    private StatController controller;

    @Delegate(excludes = ExcludeMethods.class)
    private Statement delegate;

    @Override
    public ResultSet executeQuery(java.lang.String s) throws java.sql.SQLException {
        controller.newQuery();
        return delegate.executeQuery(s);
    }

    @Override
    public int executeUpdate(java.lang.String s) throws java.sql.SQLException {
        controller.newQuery();
        return delegate.executeUpdate(s);
    }

    @Override
    public boolean execute(java.lang.String s) throws java.sql.SQLException{
        controller.newQuery();
        return delegate.execute(s);
    }
    
    @Override
    public int executeUpdate(java.lang.String s, int i) throws java.sql.SQLException{
        controller.newQuery();
        return delegate.executeUpdate(s, i);
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        controller.newQuery();
        return delegate.executeUpdate(s, ints);
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        controller.newQuery();
        return delegate.executeUpdate(s, strings);
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        controller.newQuery();
        return delegate.execute(s, i);
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        controller.newQuery();
        return delegate.execute(s, ints);
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        controller.newQuery();
        return delegate.execute(s, strings);
    }



}
