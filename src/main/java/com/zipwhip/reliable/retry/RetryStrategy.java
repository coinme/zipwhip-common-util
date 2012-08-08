package com.zipwhip.reliable.retry;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RetryStrategy {


    /**
     * The interval we should wait before re-attempting this operation, which may require failedAttemptCount to make said determination.
     * @param failedAttemptCount
     * @return
     */
    public long getNextRetryInterval(int failedAttemptCount);

    /**
     * Whether or not we should continue to attempt this operation, given failedAttemptCount previous failing attempts.
     * @param failedAttemptCount
     * @return
     */
    public boolean continueReattempts(int failedAttemptCount);
}
