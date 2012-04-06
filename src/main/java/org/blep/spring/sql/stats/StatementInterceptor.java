package org.blep.spring.sql.stats;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    @Around("execution(* org.blep.spring.sql.stats.*StatementWrapper.execute*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable{
        controller.newQuery();
        return pjp.proceed();
    }
}
