package com.zipwhip.reliable.retry;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    private static final int DEFAULT_STARTING_INTERVAL = 1;
    private static final double DEFAULT_RETRY_MULTIPLIER = 1.5d;
    private static final int DEFAULT_MAX_RETRIES = 19;

    private int maxAttemptCount;
    private int startingInterval;
    private double retryMultiplier;

    public ExponentialBackoffRetryStrategy() {
        this(DEFAULT_STARTING_INTERVAL, DEFAULT_RETRY_MULTIPLIER);
    }

    public ExponentialBackoffRetryStrategy(int startingInterval, double retryMultiplier) {
        this(startingInterval, retryMultiplier, DEFAULT_MAX_RETRIES);
    }

    /**
     * @param startingInterval The interval returned for retryIntervalInSeconds where failedAttemptCount == 1.
     * @param retryMultiplier  The amount by which we should multiply each subsequent interval after the first.
     *                         After the first attempt, interval is (startingInterval).  After the second attempt, interval is (startingInterval * retryMultiplier).  Third attempt is (startingInterval * retryMultiplier^2)
     *                         NOTE: In situations where the retry multiplier is less than one, we change the value to one (To prevent situations where the interval gets shorter and shorter after each call).
     */
    public ExponentialBackoffRetryStrategy(int startingInterval, double retryMultiplier, int maxAttemptCount) {
        this.maxAttemptCount = maxAttemptCount <= 0 ? DEFAULT_MAX_RETRIES : maxAttemptCount;
        this.startingInterval = startingInterval;
        this.retryMultiplier = retryMultiplier < 1 ? 1 : retryMultiplier;
    }

    @Override
    public int retryIntervalInSeconds(int attemptCount) {
        if (attemptCount <= 1) {
            return this.startingInterval;
        } else if (attemptCount > maxAttemptCount) {
            return (int) Math.floor(this.startingInterval * Math.pow(this.retryMultiplier, maxAttemptCount - 1));
        } else {
            return (int) Math.floor(this.startingInterval * Math.pow(this.retryMultiplier, attemptCount - 1));
        }
    }
}
