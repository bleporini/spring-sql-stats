package org.blep.spring.sql.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 09:15
 */
@Component
public class StatController implements StatControllerMBean {
    
    @Autowired
    private SqlInterceptor sqlInterceptor;

    private long queryCount = 0;

    private long activeConnectionCount = 0;
    private long totalConnectionUsed=0;
    private long lastConnectionTiming = 0;
    private long averageConnectionTiming = 0;

    public void newConnection(){
        activeConnectionCount++;
        totalConnectionUsed++;
    }

    public long getLastConnectionTiming() {
        return lastConnectionTiming;
    }

    /**
     *
     * @param lastConnectionTiming in nano second.
     */
    public void setLastConnectionTiming(long lastConnectionTiming) {
        this.lastConnectionTiming = lastConnectionTiming;
        averageConnectionTiming = (averageConnectionTiming * totalConnectionUsed + lastConnectionTiming)/totalConnectionUsed +1;
    }

    public void connectionClosed(){
        activeConnectionCount--;
    }

    public void newQuery(){
        queryCount++;
    }

    @Override
    public long getAverageConnectionTiming(){
        return averageConnectionTiming;
    }
    
    @Override
    public void resetConnectionCounts(){
        activeConnectionCount = 0;
        totalConnectionUsed = 0;
    }

    @Override
    public long getQueryCount() {
        return queryCount;
    }

    @Override
    public void startRecording(){
        sqlInterceptor.setRecording(true);
    }

    @Override
    public void stopRecording(){
        sqlInterceptor.setRecording(false);
    }

    @Override
    public void resetCounter(){
        queryCount = 0;
    }

    @Override
    public long getActiveConnectionCount() {
        return activeConnectionCount;
    }

    @Override
    public long getTotalConnectionUsed() {
        return totalConnectionUsed;
    }
}
