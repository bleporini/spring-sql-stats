package org.blep.spring.sql.stats;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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

    private BlockingQueue<TimedQuery> queue;



    @RequiredArgsConstructor
    private static class BackGroundQueryRecorder implements Runnable{
        @NonNull
        private BlockingQueue<TimedQuery> queue;
        private long fasterSlowest = Long.MIN_VALUE;

        private Map<String, Long> slowestQueries = new HashMap<String, Long>(MAX_SLOWEST_QUERIES);


        //TODO: make it settable
        private static int MAX_SLOWEST_QUERIES = 10;


        @Override
        public void run() {
            while (true) {
                try {
                    final TimedQuery timedQuery = queue.take();

                    if (StringUtils.isBlank(timedQuery.sql) || timedQuery.time == null || timedQuery.time<fasterSlowest) {
                        return;
                    }

                    Map<String, Long> slowerQueries = Maps.filterEntries(slowestQueries, new Predicate<Map.Entry<String, Long>>() {
                        @Override
                        public boolean apply(@Nullable Map.Entry<String, Long> stringLongEntry) {
                            return stringLongEntry.getValue() > timedQuery.time;
                        }
                    });

                    if (slowerQueries.size() >= MAX_SLOWEST_QUERIES) {
                        continue; // The current query cannot be promoted as on of the slowest queries
                    }

                    slowestQueries.put(timedQuery.sql, timedQuery.time);
                    //If you're faster than the faster then you're the faster!
                    fasterSlowest = timedQuery.time<fasterSlowest?timedQuery.time:fasterSlowest;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public StatController() {
        queue = new LinkedBlockingQueue<TimedQuery>();
        queryRecorder = new BackGroundQueryRecorder(queue);
        Thread t = new Thread(queryRecorder, "BackGroundQueryRecorder");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @AllArgsConstructor
    @ToString
    private class TimedQuery{
        private String sql;
        private Long time;
    }

    private BackGroundQueryRecorder queryRecorder;

    /**
     *
     * @return the number of query waiting for processing
     */
    public int inProcessCount() {
        return queue.size();
    }


    /**
     * @param sql
     * @param time in nanoseconds
     */
    public void recordQueryTime(final String sql, final Long time) {
        if( !queue.offer(new TimedQuery(sql, time)))
            System.err.println("Problem during insertion");
    }

    public Map<String, Long> getSlowestQueries() {
        return Collections.unmodifiableMap(queryRecorder.slowestQueries);
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
