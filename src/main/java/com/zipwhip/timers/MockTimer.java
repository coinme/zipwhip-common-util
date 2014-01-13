package com.zipwhip.timers;

import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.Directory;
import com.zipwhip.util.ListDirectory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael
 * @date 1/13/14
 */
public class MockTimer implements Timer {

    private Directory<Long, Timeout> map = new ListDirectory<Long, Timeout>();
    private long currentTime;

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        MockTimeout timeout = new MockTimeout(this, task);

        map.add(currentTime + unit.toMillis(delay), timeout);

        return timeout;
    }

    public void moveForward(long delayForward, TimeUnit timeUnit) throws Exception {
        if (delayForward == 0) {
            return;
        }

        long start = currentTime;
        long end = currentTime + timeUnit.toMillis(delayForward);

        for(long i = start; i <= end; i++) {
            currentTime = i;

            Collection<Timeout> timeouts = map.get(i);

            if (CollectionUtil.isNullOrEmpty(timeouts)) {
                continue;
            }

            for (Timeout timeout : timeouts) {
                timeout.getTask().run(timeout);
            }
        }
    }

    @Override
    public Set<Timeout> stop() {
        return null; // Worth implementing this?
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
    }
}
