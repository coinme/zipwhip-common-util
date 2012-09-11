package com.zipwhip.reliable.retry;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 6:03 PM
 *
 * Always return the same value.
 */
public class ConstantIntervalRetryStrategy implements RetryStrategy {

    private long retryInterval;

    /**
     * @param retryInterval The interval returned for getNextRetryInterval under all circumstances;
     */
    public ConstantIntervalRetryStrategy(long retryInterval){
        this.retryInterval = retryInterval;
    }

    @Override
    public long getNextRetryInterval(int attemptCount) {
        return retryInterval;
    }

}