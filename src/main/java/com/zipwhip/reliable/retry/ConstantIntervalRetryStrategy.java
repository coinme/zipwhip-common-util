package com.zipwhip.reliable.retry;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 6:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstantIntervalRetryStrategy implements RetryStrategy {

    private int maxAttemptCount;
    private long retryInterval;

    /**
     * @param maxAttemptCount The maximum number of times that this operation should be re-attempted.  A non-positive value will be interpreted to be 1.
     * @param retryInterval The interval returned for getNextRetryInterval under all circumstances;
     */
    public ConstantIntervalRetryStrategy(int maxAttemptCount, long retryInterval){
        this.maxAttemptCount = maxAttemptCount <= 0 ? 1 : maxAttemptCount;
        this.retryInterval = retryInterval;
    }

    @Override
    public long getNextRetryInterval(int failedAttemptCount) {
        return retryInterval;
    }

    @Override
    public boolean continueReattempts(int failedAttemptCount) {
        return failedAttemptCount < maxAttemptCount;
    }
}