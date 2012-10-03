package com.zipwhip.util;

/**
 * Overrides the thread name proposed by {@link com.zipwhip.util.ThreadRenamingRunnable}.
 */
public interface ThreadNameDeterminer {

    /**
     * {@link ThreadNameDeterminer} that accepts the proposed thread name
     * as is.
     */
    ThreadNameDeterminer PROPOSED = new ThreadNameDeterminer() {
        public String determineThreadName(String currentThreadName,
                String proposedThreadName) throws Exception {
            return proposedThreadName;
        }
    };

    /**
     * {@link ThreadNameDeterminer} that rejects the proposed thread name and
     * retains the current one.
     */
    ThreadNameDeterminer CURRENT = new ThreadNameDeterminer() {
        public String determineThreadName(String currentThreadName,
                String proposedThreadName) throws Exception {
            return null;
        }
    };

    /**
     * Overrides the thread name proposed by {@link com.zipwhip.util.ThreadRenamingRunnable}.
     *
     * @param currentThreadName   the current thread name
     * @param proposedThreadName  the proposed new thread name
     * @return the actual new thread name.
     *         If {@code null} is returned, the proposed thread name is
     *         discarded (i.e. no rename).
     */
    String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception;
}
