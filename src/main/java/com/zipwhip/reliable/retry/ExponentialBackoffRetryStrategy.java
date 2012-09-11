package com.zipwhip.reliable.retry;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    private int maxAttemptCount;
    private int startingInterval;
    private double retryMultiplier;

    private static final int DEFAULT_MAX_ATTEMPT_COUNT = 10;
    private static final int DEFAULT_STARTING_INTERVAL = 1000;
    private static final double DEFAULT_RETRY_MULTIPLIER = 10;

    public ExponentialBackoffRetryStrategy() {
        this(DEFAULT_MAX_ATTEMPT_COUNT, DEFAULT_STARTING_INTERVAL, DEFAULT_RETRY_MULTIPLIER);
    }

    /**
     * @param maxAttemptCount The maximum number of times that this operation should be re-attempted.  A non-positive value will be interpreted to be 1.
     * @param startingInterval The interval returned for getNextRetryInterval where failedAttemptCount == 1.
     * @param retryMultiplier The amount by which we should multiply each subsequent interval after the first.
     *                        After the first attempt, interval is (startingInterval).  After the second attempt, interval is (startingInterval * retryMultiplier).  Third attempt is (startingInterval * retryMultiplier^2)
     *                        NOTE: In situations where the retry multiplier is less than one, we change the value to one (To prevent situations where the interval gets shorter and shorter after each call).
     */
    public ExponentialBackoffRetryStrategy(int maxAttemptCount, int startingInterval, double retryMultiplier){
        this.maxAttemptCount = maxAttemptCount <= 0 ? 1 : maxAttemptCount;
        this.startingInterval = startingInterval;
        this.retryMultiplier = retryMultiplier < 1 ? 1 : retryMultiplier;
    }

    @Override
    public long getNextRetryInterval(int failedAttemptCount) {
        if (failedAttemptCount <= 1){
            return this.startingInterval;
        } else {
            return (long)Math.floor(this.startingInterval * Math.pow(this.retryMultiplier, failedAttemptCount - 1));
        }
    }

    @Override
    public boolean continueReattempts(int failedAttemptCount) {
        return failedAttemptCount < maxAttemptCount;
    }
}
