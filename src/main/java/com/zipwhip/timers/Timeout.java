package com.zipwhip.timers;

/**
 * A handle associated with a {@link TimerTask} that is returned by a
 * {@link Timer}.
 */
public interface Timeout {

    /**
     * Returns the {@link Timer} that created this handle.
     */
    Timer getTimer();

    /**
     * Returns the {@link TimerTask} which is associated with this handle.
     */
    TimerTask getTask();

    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated
     * with this handle has been expired.
     */
    boolean isExpired();

    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated
     * with this handle has been cancelled.
     */
    boolean isCancelled();

    /**
     * Cancels the {@link TimerTask} associated with this handle.  It the
     * task has been executed or cancelled already, it will return with no
     * side effect.
     */
    void cancel();
}
