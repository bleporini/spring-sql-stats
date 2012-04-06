package org.blep.spring.sql.stats;

import lombok.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 11:22
 */
@Component
@Scope("prototype")
class PreparedStatementWrapper extends StatementWrapper implements PreparedStatement{
    @Autowired
    private StatController controller;


    private interface ExcludeMethod {

        ResultSet executeQuery() throws java.sql.SQLException;

        int executeUpdate() throws java.sql.SQLException;

        boolean execute() throws java.sql.SQLException;
    }

    @Delegate(excludes = {ExcludeMethod.class,StatementWrapper.class})
    private PreparedStatement delegate;

    PreparedStatementWrapper(PreparedStatement preparedStatement) {
        super(preparedStatement);
        delegate =  preparedStatement;

    }

    @Override
    public boolean execute() throws java.sql.SQLException{
        controller.newQuery();
        return delegate.execute();
    }    


    @Override
    public ResultSet executeQuery() throws java.sql.SQLException{
        controller.newQuery();
        return delegate.executeQuery();
    }



    @Override
    public int executeUpdate() throws java.sql.SQLException{
        controller.newQuery();
        return delegate.executeUpdate();
    }

}
