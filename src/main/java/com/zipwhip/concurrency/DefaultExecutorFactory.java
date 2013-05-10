package com.zipwhip.concurrency;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/2/12
 * Time: 7:05 PM
 * <p/>
 * Creates executors with a fixed size.
 */
public class DefaultExecutorFactory implements ExecutorFactory {

    private int size = 1;
    private int depth = 5;

    @Override
    public Executor create(String name) {
        return new ThreadPoolExecutor(size,
                size, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(depth), new DefaultThreadFactory(name));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
