package com.zipwhip.binding;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 5:26 PM
 *
 * Always returns a constant value
 */
public class NullFilter<T> implements Filter<T> {

    private boolean value;

    public NullFilter() {
        this(false);
    }

    public NullFilter(boolean value) {
        this.value = value;
    }

    public boolean call(T item) throws Exception {
        return value;
    }

}

