package org.blep.spring.sql.stats;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 09:15
 */
@Component
public class StatController implements StatControllerMBean {

    @Autowired
    private SqlInterceptor sqlInterceptor;

    private AtomicLong queryCount = new AtomicLong(0);

    private AtomicLong activeConnectionCount = new AtomicLong(0);
    private AtomicLong totalConnectionUsed = new AtomicLong(0);
    private long lastConnectionTiming = 0;
    private long averageConnectionTiming = 0;
    private long fasterSlowest = Long.MIN_VALUE;

    //TODO: make it settable 
    private int MAX_SLOWEST_QUERIES = 10;

    private Map<String, Long> slowestQueries = Collections.synchronizedMap(new HashMap<String, Long>(MAX_SLOWEST_QUERIES));


    /**
     * @param sql
     * @param time in nanoseconds
     */
    public void recordQueryTime(final String sql, final Long time) {
        if (StringUtils.isBlank(sql) || time == null || time<fasterSlowest) {
            return;
        }

        synchronized (slowestQueries) {
            Map<String, Long> slowerQueries = Maps.filterEntries(slowestQueries, new Predicate<Map.Entry<String, Long>>() {
                @Override
                public boolean apply(@Nullable Map.Entry<String, Long> stringLongEntry) {
                    return stringLongEntry.getValue() > time;
                }
            });

            if (slowerQueries.size() >= MAX_SLOWEST_QUERIES) {
                return; // The current query cannot be promoted as on of the slowest queries
            }

            slowestQueries.put(sql, time);
            //If you're faster than the faster then you're the faster!
            fasterSlowest = time<fasterSlowest?time:fasterSlowest;
        }
    }

    public Map<String, Long> getSlowestQueries() {
        return Collections.unmodifiableMap(slowestQueries);
    }

    public void newConnection() {
        activeConnectionCount.incrementAndGet();
        totalConnectionUsed.incrementAndGet();
    }

    public long getLastConnectionTiming() {
        return lastConnectionTiming;
    }

    /**
     * @param lastConnectionTiming in nano second.
     */
    public void setLastConnectionTiming(long lastConnectionTiming) {
        this.lastConnectionTiming = lastConnectionTiming;
        averageConnectionTiming = (averageConnectionTiming * totalConnectionUsed.get() + lastConnectionTiming) / totalConnectionUsed.get() + 1;
    }

    public void connectionClosed() {
        activeConnectionCount.decrementAndGet();
    }

    public void newQuery() {
        queryCount.incrementAndGet();
    }

    @Override
    public long getAverageConnectionTiming() {
        return averageConnectionTiming;
    }

    @Override
    public void resetConnectionCounts() {
        activeConnectionCount.set(0);
        totalConnectionUsed.set(0);
    }

    @Override
    public long getQueryCount() {
        return queryCount.get();
    }

    @Override
    public void startRecording() {
        sqlInterceptor.setRecording(true);
    }

    @Override
    public void stopRecording() {
        sqlInterceptor.setRecording(false);
    }

    @Override
    public void resetCounter() {
        queryCount.set(0);
    }

    @Override
    public long getActiveConnectionCount() {
        return activeConnectionCount.get();
    }

    @Override
    public long getTotalConnectionUsed() {
        return totalConnectionUsed.get();
    }
}
