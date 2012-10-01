package com.zipwhip.executors;

import com.zipwhip.lifecycle.Destroyable;
import com.zipwhip.lifecycle.DestroyableBase;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
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

    private static SimpleExecutor INSTANCE;

    private final CountDownLatch latch = new CountDownLatch(1);

    public static SimpleExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SimpleExecutor() {
                @Override
                public void shutdown() {
                    throw new RuntimeException("Cannot shutdown the shared instance");
                }
            };
        }
        return INSTANCE;
    }

    private boolean destroyed = false;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public void shutdown() {
        latch.countDown();
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
        return latch.await(timeout, unit);
    }

    @Override
    public String toString() {
        return "[SimpleExecutor]";
    }

}
