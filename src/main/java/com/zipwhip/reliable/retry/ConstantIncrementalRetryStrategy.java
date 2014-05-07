package com.zipwhip.reliable.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Ali Serghini
 * Date: 6/10/13
 * Time: 11:30 AM
 */
public class ConstantIncrementalRetryStrategy implements RetryStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstantIncrementalRetryStrategy.class);
    private final int constantInterval;
    private final int maxRetry;

    /**
     * @param constantInterval - in milliseconds
     * @param maxRetry         - maximum number of retry before we reset the counter
     */
    public ConstantIncrementalRetryStrategy(int constantInterval, int maxRetry) {
        if (constantInterval < 1) throw new IllegalArgumentException("Constant Interval must be > 1 millisecond");
        if (maxRetry < 2) throw new IllegalArgumentException("Maximum retry must be > 1");

        this.constantInterval = constantInterval;
        this.maxRetry = maxRetry;
    }

    @Override
    public int retryIntervalInSeconds(final int attemptCount) {
        // try immediately if it is the first attempt
        final int tmpAttemptCount = attemptCount - 1;
        if (tmpAttemptCount < 1) return 0;

        final int retryCount = tmpAttemptCount < 1 ? 1 : (tmpAttemptCount <= maxRetry ? tmpAttemptCount : (tmpAttemptCount % maxRetry));
        final int nextRetry = retryCount < 1 ? constantInterval * maxRetry : retryCount * constantInterval;
        LOGGER.debug(String.format("==> %s: [constantInterval: %d], [maxRetry: %d], [attemptCount: %d], [nextRetry: %d], [total Waiting interval: %d]", RetryStrategy.class.getSimpleName(), constantInterval, maxRetry, attemptCount, nextRetry, (tmpAttemptCount * constantInterval)));

        return nextRetry;
    }
}
