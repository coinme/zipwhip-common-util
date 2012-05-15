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

    private static Filter<Record> instance;

    private boolean value;

    public NullFilter() {
        this(false);
    }

    public NullFilter(boolean value) {
        this.value = value;
    }

    /**
     * Defaults to false value.
     *
     * @return
     */
    public static Filter<Record> getInstance() {
        if (instance == null){
            instance = new NullFilter<Record>(false);
        }

        return instance;
    }

    public boolean call(T item) throws Exception {
        return value;
    }

}

