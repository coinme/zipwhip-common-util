package com.zipwhip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Runnable} that changes the current thread name and reverts it back
 * when its execution ends.  To change the default thread names set by Netty,
 * use {@link #setThreadNameDeterminer(ThreadNameDeterminer)}.
 *
 * @apiviz.landmark
 * @apiviz.has org.jboss.netty.util.ThreadNameDeterminer oneway - -
 */
public class ThreadRenamingRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadRenamingRunnable.class);

    private static volatile ThreadNameDeterminer threadNameDeterminer =
        ThreadNameDeterminer.PROPOSED;

    /**
     * Returns the {@link ThreadNameDeterminer} which overrides the proposed
     * new thread name.
     */
    public static ThreadNameDeterminer getThreadNameDeterminer() {
        return threadNameDeterminer;
    }

    /**
     * Sets the {@link ThreadNameDeterminer} which overrides the proposed new
     * thread name.  Please note that the specified {@link ThreadNameDeterminer}
     * affects only new {@link ThreadRenamingRunnable}s; the existing instances
     * are not affected at all.  Therefore, you should make sure to call this
     * method at the earliest possible point (i.e. before any Netty worker
     * thread starts) for consistent thread naming.  Otherwise, you might see
     * the default thread names and the new names appear at the same time in
     * the full thread dump.
     */
    public static void setThreadNameDeterminer(ThreadNameDeterminer threadNameDeterminer) {
        if (threadNameDeterminer == null) {
            throw new NullPointerException("threadNameDeterminer");
        }
        ThreadRenamingRunnable.threadNameDeterminer = threadNameDeterminer;
    }

    private final Runnable runnable;
    private final String proposedThreadName;

    /**
     * Creates a new instance which wraps the specified {@code runnable}
     * and changes the thread name to the specified thread name when the
     * specified {@code runnable} is running.
     */
    public ThreadRenamingRunnable(Runnable runnable, String proposedThreadName) {
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }
        if (proposedThreadName == null) {
            throw new NullPointerException("proposedThreadName");
        }
        this.runnable = runnable;
        this.proposedThreadName = proposedThreadName;
    }

    public void run() {
        final Thread currentThread = Thread.currentThread();
        final String oldThreadName = currentThread.getName();
        final String newThreadName = getNewThreadName(oldThreadName);

        // Change the thread name before starting the actual runnable.
        boolean renamed = false;
        if (!oldThreadName.equals(newThreadName)) {
            try {
                currentThread.setName(newThreadName);
                renamed = true;
            } catch (SecurityException e) {
                LOGGER.debug(
                        "Failed to rename a thread " +
                                "due to security restriction.", e);
            }
        }

        // Run the actual runnable and revert the name back when it ends.
        try {
            runnable.run();
        } finally {
            if (renamed) {
                // Revert the name back if the current thread was renamed.
                // We do not check the exception here because we know it works.
                currentThread.setName(oldThreadName);
            }
        }
    }

    private String getNewThreadName(String currentThreadName) {
        String newThreadName = null;

        try {
            newThreadName =
                getThreadNameDeterminer().determineThreadName(
                        currentThreadName, proposedThreadName);
        } catch (Throwable t) {
            LOGGER.warn("Failed to determine the thread name", t);
        }

        return newThreadName == null? currentThreadName : newThreadName;
    }
}
