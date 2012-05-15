package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 9:05 PM
 *
 * Generates content using a proprietary strategy. Example uses include generating auto-numbers,
 * Twitter snowflakes, UUIDS, or random numbers. Kind of like an Iterator
 */
public interface Generator<T> {

    /**
     * Generate content using your strategy.
     *
     * @return The next unique content.
     */
    T next();

}
