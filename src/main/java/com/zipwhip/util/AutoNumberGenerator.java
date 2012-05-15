package com.zipwhip.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:49 PM
 *
 * Auto incrementing number with no bound.
 */
public class AutoNumberGenerator implements Generator<Long> {

    private static final long DELTA = 1;

    private AtomicLong number = new AtomicLong();

    public Long next() {
        return number.addAndGet(DELTA);
    }

}
