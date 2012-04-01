package org.blep.spring.sql.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 09:15
 */
@ManagedResource
@Component
public class StatController {
    
    @Autowired
    private SqlInterceptor sqlInterceptor;

    private long queryCount = 0;

    private long activeConnectionCount = 0;
    private long totalConnectionUsed=0;
    
    public void newConnection(){
        activeConnectionCount++;
        totalConnectionUsed++;
    }   
    
    public void connectionClosed(){
        activeConnectionCount--;
    }

    public void newQuery(){
        queryCount++;
    }

    @ManagedOperation
    public void resetConnectionCounts(){
        activeConnectionCount = 0;
        totalConnectionUsed = 0;
    }

    @ManagedAttribute
    public long getQueryCount() {
        return queryCount;
    }

    @ManagedOperation
    public void startRecording(){
        sqlInterceptor.setRecording(true);
    }

    @ManagedOperation
    public void stopRecording(){
        sqlInterceptor.setRecording(false);
    }

    @ManagedOperation
    public void resetCounter(){
        queryCount = 0;
    }

    @ManagedAttribute
    public long getActiveConnectionCount() {
        return activeConnectionCount;
    }

    @ManagedAttribute
    public long getTotalConnectionUsed() {
        return totalConnectionUsed;
    }
}
