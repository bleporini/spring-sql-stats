package org.blep.spring.sql.stats;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author blep
 *         Date: 03/04/12
 *         Time: 07:55
 */
@ManagedResource
public interface StatControllerMBean {
    @ManagedAttribute
    long getAverageConnectionTiming();

    @ManagedOperation
    void resetConnectionCounts();

    @ManagedAttribute
    long getQueryCount();

    @ManagedOperation
    void startRecording();

    @ManagedOperation
    void stopRecording();

    @ManagedOperation
    void resetCounter();

    @ManagedAttribute
    long getActiveConnectionCount();

    @ManagedAttribute
    long getTotalConnectionUsed();
}
