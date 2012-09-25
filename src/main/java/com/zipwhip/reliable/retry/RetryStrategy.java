package com.zipwhip.reliable.retry;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 5:45 PM
 *
 * This class lets us encapsulate different delay algorithms. For example, we might want to reconnect (or retry)
 * every 5 seconds. Another strategy might be "expontial decay" where it starts out fast and then slows down.
 */
public interface RetryStrategy {

    /**
     * The interval we should wait before re-attempting this operation, which may require attemptCount to make said determination.
     *
     * @param attemptCount
     * @return
     */
    public long getNextRetryInterval(int attemptCount);

}
