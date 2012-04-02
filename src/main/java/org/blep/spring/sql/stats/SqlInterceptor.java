package org.blep.spring.sql.stats;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 00:43
 */
@Component
@Aspect
public class SqlInterceptor implements ApplicationContextAware {

    @Getter
    @Setter
    private boolean recording = false;

    @Autowired
    private StatController controller;

    private AutowireCapableBeanFactory beanFactory;
    private ApplicationContext applicationContext;


    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Object doIt2(ProceedingJoinPoint pjp) throws Throwable {

        Connection conn = null;

        if (recording) {
            long delay = System.nanoTime();
            conn = (Connection) applicationContext.getBean("connectionWrapper", pjp.proceed());
            delay = System.nanoTime() - delay;
            controller.setLastConnectionTiming(delay);
        }else{
            conn = (Connection) pjp.proceed();
        }

        return conn;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            beanFactory = applicationContext.getAutowireCapableBeanFactory();
            this.applicationContext = applicationContext;
    }
}
