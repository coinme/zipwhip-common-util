package com.zipwhip.executors;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 8/2/11
 * Time: 2:42 PM
 * <p/>
 * Executes something synchronously
 */
public class SimpleExecutor extends AbstractExecutorService {

    private static SimpleExecutor instance;

    public static SimpleExecutor getInstance() {
        if (instance == null) {
            instance = new SimpleExecutor() {
                @Override
                public void shutdown() {
                    throw new RuntimeException("Cannot shutdown the shared instance");
                }
            };
        }
        return instance;
    }

    private boolean destroyed = false;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public void shutdown() {
        destroyed = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return null;
    }

    @Override
    public boolean isShutdown() {
        return destroyed;
    }

    @Override
    public boolean isTerminated() {
        return destroyed;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        destroyed = true;
        return true;
    }
}
