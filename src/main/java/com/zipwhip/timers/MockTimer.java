package com.zipwhip.timers;

import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.Directory;
import com.zipwhip.util.ListDirectory;
import com.zipwhip.util.LocalDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael
 * @date 1/13/14
 */
public class MockTimer implements Timer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockTimer.class);

    private LocalDirectory<Long, Timeout> map = new ListDirectory<Long, Timeout>();
    private long currentTime;

    @Override
    public synchronized Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        final long exitTime = currentTime + unit.toMillis(delay);

        MockTimeout timeout = new MockTimeout(this, task) {
            @Override
            public void cancel() {
                super.cancel();

                map.remove(exitTime, this);
            }
        };

        map.add(exitTime, timeout);

        LOGGER.debug("(Timeout: {}) scheduled for {}", timeout, currentTime + unit.toMillis(delay));

        notifyAll();

        return timeout;
    }

    public synchronized void waitUntilSomethingScheduled() {
        try {
            if (map.isEmpty()) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void waitUntilSomethingNewIsScheduled() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveForward(long delayForward, TimeUnit timeUnit) throws Exception {
        if (delayForward == 0) {
            return;
        }

        long start = currentTime;
        long end = currentTime + timeUnit.toMillis(delayForward);
        LocalDirectory<Long, Timeout> removes = new ListDirectory<Long, Timeout>();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Moving forward from {} to {}. Timeouts: {}", start, end, map);
        }

        for(long i = start; i <= end; i++) {
            currentTime = i;

            Collection<Timeout> timeouts = map.get(i);

            if (CollectionUtil.isNullOrEmpty(timeouts)) {
                continue;
            }

            for (Timeout timeout : timeouts) {
                LOGGER.debug("(Timeout: {}) ran at {}", timeout, i);
                timeout.getTask().run(timeout);
                removes.add(i, timeout);
            }
        }

        for (Long time : removes.keySet()) {
            Collection<Timeout> timeouts = removes.get(time);

            if (CollectionUtil.exists(timeouts)) {
                for (Timeout timeout : timeouts) {
                    map.remove(time, timeout);
                }
            }
        }
    }

    @Override
    public Set<Timeout> stop() {
        return null; // Worth implementing this?
    }

    public boolean isSomethingScheduled() {
        return !map.isEmpty();
    }

    private static class MockTimeout implements Timeout {

        final Timer timer;
        final TimerTask task;
        boolean expired;
        boolean cancelled;

        private MockTimeout(Timer timer, TimerTask task) {
            this.timer = timer;
            this.task = task;
        }

        @Override
        public Timer getTimer() {
            return timer;
        }

        @Override
        public TimerTask getTask() {
            return task;
        }

        @Override
        public boolean isExpired() {
            return expired;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void cancel() {
            cancelled = true;
        }

        @Override
        public String toString() {
            if (task == null) {
                return "[MockTimeout task:null]";
            }

            return task.toString();
        }
    }
}
