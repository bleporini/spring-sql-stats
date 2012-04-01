package org.blep.spring.sql.stats;

import lombok.Delegate;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

//@RequiredArgsConstructor
@Component
@Scope("prototype")
public class ConnectionWrapper implements Connection, ApplicationContextAware, InitializingBean{


    private interface ExcludedMethods {

        PreparedStatement prepareStatement(String s) throws SQLException;

        Statement createStatement() throws java.sql.SQLException;

        void close() throws java.sql.SQLException;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        controller.newConnection();
    }


    public ConnectionWrapper(Connection connection) {
        delegate = connection;
        if (delegate == null) {
            throw new RuntimeException("No connection findable");
        }
    }

    @NonNull
    @Delegate(excludes = ExcludedMethods.class)
    private Connection delegate;
    private ApplicationContext context;

    @Autowired
    private StatController controller;

    @Override
    public void close() throws java.sql.SQLException{
        controller.connectionClosed();
        delegate.close();
    }

    @Override
    public PreparedStatement prepareStatement(String s) throws SQLException {
        return (PreparedStatement) context.getBean("preparedStatementWrapper", delegate.prepareStatement(s));
    }

    @Override
    public Statement createStatement() throws java.sql.SQLException {
        return (Statement) context.getBean("statementWrapper" , delegate.createStatement());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }



}