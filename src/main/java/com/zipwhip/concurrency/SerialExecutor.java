package com.zipwhip.concurrency;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 4/19/11
 * Time: 12:37 PM
 */
class SerialExecutor implements Executor {

    private final Executor executor;
    private final Queue<Runnable> tasks;

    private Runnable active;

    SerialExecutor(Executor executor) {
        this(executor, 10);
    }

    SerialExecutor(Executor executor, int capacity) {
        this.executor = executor;
        this.tasks = new LinkedBlockingQueue<Runnable>(capacity);
    }

    public synchronized void execute(final Runnable r) {

        tasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });

        if (active == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {

        active = tasks.poll();

        if (active != null) {
            executor.execute(active);
        }
    }

}