package com.zipwhip.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/1/11
 * Time: 11:07 PM
 */
public abstract class SelectionStrategyBase<T> implements SelectionStrategy<T> {

    protected List<T> options;
    protected int size;

    @Override
    public void setOptions(List<T> ts) {
        // clone the list to a non-mutable list
        this.options = Arrays.asList(ts.toArray((T[]) new Object[]{}));
        this.size = this.options.size();
    }

    @Override
    public List<T> getOptions() {
        // clone so you cant change the size on me :(
        return options;
    }

}
