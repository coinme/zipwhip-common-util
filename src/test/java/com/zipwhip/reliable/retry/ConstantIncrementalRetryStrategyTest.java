package com.zipwhip.reliable.retry;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Ali Serghini
 * Date: 6/10/13
 * Time: 11:52 AM
 */
public class ConstantIncrementalRetryStrategyTest {

    @Test
    public void testGetNextRetryInterval() throws Exception {
        final int listSize = 17878;
        final int interval = 23 * 1000;
        final int maxRetry = (5 * 60) / 23;
        final ConstantIncrementalRetryStrategy strategy = new ConstantIncrementalRetryStrategy(interval, maxRetry);

        final List<Long> retryIntervalsStrategy = new ArrayList<Long>(listSize);
        for (int i = 1; i <= listSize; i++) {
            retryIntervalsStrategy.add(strategy.getNextRetryInterval(i));
        }

        final List<Long> testRetryIntervals = getRetryInterval(interval, maxRetry, listSize);
        Assert.assertEquals(testRetryIntervals.size(), retryIntervalsStrategy.size());


        for (int i = 0; i < testRetryIntervals.size(); i++) {
            Assert.assertEquals(testRetryIntervals.get(i), retryIntervalsStrategy.get(i));
        }

    }

    private List<Long> getRetryInterval(final long interval, final int maxRetry, final int listSize) {
        final List<Long> retryIntervals = new ArrayList<Long>(maxRetry);

        for (int i = 1; i <= maxRetry; i++) {
            retryIntervals.add(i * interval);
        }

        final int iterations = listSize / maxRetry;
        final int lastIteration = listSize % maxRetry;

        final List<Long> retryIntervals2 = new ArrayList<Long>(listSize);
        for (int i = 1; i <= iterations; i++) {
            retryIntervals2.addAll(retryIntervals);
        }

        if (lastIteration > 0) {
            for (int i = 0; i < lastIteration; i++) {
                retryIntervals2.add(retryIntervals.get(i));
            }
        }

        retryIntervals2.add(0, 0l);
        retryIntervals2.remove(retryIntervals2.size() - 1);
        return retryIntervals2;
    }
}
