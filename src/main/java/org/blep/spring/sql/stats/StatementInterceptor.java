package org.blep.spring.sql.stats;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author blep
 *         Date: 05/04/12
 *         Time: 07:56
 */
@Component
@Aspect
public class StatementInterceptor {
    
    @Autowired
    private StatController controller;


    @Around("execution(* org.blep.spring.sql.stats.PreparedStatementWrapper.execute*())")
    private Object execute(ProceedingJoinPoint pjp) throws Throwable {
        PreparedStatementWrapper target = (PreparedStatementWrapper) pjp.getTarget();
        String sql = target.getSql();
        return proceed(pjp, sql);
    }
    
    

    @Around("execution(* org.blep.spring.sql.stats.*StatementWrapper.execute*(..)) && args(sql,..)")
    public Object around(ProceedingJoinPoint pjp, String sql) throws Throwable {
        return proceed(pjp, sql);
    }

    private Object proceed(ProceedingJoinPoint pjp, String sql) throws Throwable {
        controller.newQuery();
        long delay  = System.nanoTime();
        Object proceed = pjp.proceed();
        controller.recordQueryTime(sql, System.nanoTime() - delay);
        return proceed;

    }
}
