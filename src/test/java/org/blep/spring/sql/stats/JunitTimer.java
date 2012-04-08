package org.blep.spring.sql.stats;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author blep
 *         Date: 13/03/12
 *         Time: 07:24
 */
public class JunitTimer extends TestWatcher {

    private long delay=0l;

    @Override
    protected void starting(Description description) {
        System.out.println("Starting test " + description.getMethodName());
        delay = System.currentTimeMillis();
    }

    @Override
    protected void succeeded(Description description) {
        delay = System.currentTimeMillis() - delay;
        System.out.println(description.getMethodName() + " execution : " + delay + " ms");
    }
}
