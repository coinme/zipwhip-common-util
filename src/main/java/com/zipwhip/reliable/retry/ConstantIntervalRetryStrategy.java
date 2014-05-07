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

    private int retryInterval;

    /**
     * @param retryInterval The interval returned for retryIntervalInSeconds under all circumstances;
     */
    public ConstantIntervalRetryStrategy(int retryInterval){
        this.retryInterval = retryInterval;
    }

    @Override
    public int retryIntervalInSeconds(int attemptCount) {
        return retryInterval;
    }

}