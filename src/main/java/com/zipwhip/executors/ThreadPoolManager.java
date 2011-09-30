package com.zipwhip.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author jdinsel
 */
public class ThreadPoolManager {

    private static final int MAX_SCHEDULED_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 300;

    private static final ThreadPoolManager instance = new ThreadPoolManager();

    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(MAX_SCHEDULED_POOL_SIZE);
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(MAX_POOL_SIZE);

    private ThreadPoolManager() {
    }

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    /**
     * Warning: Use of the shutdown method may cause other threads to receive rejections
     *
     * @return ScheduledExecutorService to schedule future tasks.
     */
    public synchronized final ScheduledExecutorService getScheduledThreadPool() {
        if (scheduledThreadPool.isShutdown()) {
            scheduledThreadPool = Executors.newScheduledThreadPool(MAX_SCHEDULED_POOL_SIZE);
        }
        return scheduledThreadPool;
    }

    /**
     * Warning: Use of the shutdown method may cause other threads to receive rejections
     *
     * @return ExecutorService to execute tasks.
     */
    public synchronized final ExecutorService getFixedThreadPool() {
        if (fixedThreadPool.isShutdown()) {
            fixedThreadPool = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        }
        return fixedThreadPool;
    }

    /**
     * Will always throw {@code CloneNotSupportedException}
     *
     * @return ThreadPoolManager
     * @throws CloneNotSupportedException
     */
    @Override
    public ThreadPoolManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("The ThreadPoolManager cannot be cloned.");
    }
}
