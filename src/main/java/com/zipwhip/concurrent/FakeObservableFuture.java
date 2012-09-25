package com.zipwhip.concurrent;

import com.zipwhip.concurrent.DefaultObservableFuture;

/**
 * Created with IntelliJ IDEA.
 * User: jed
 * Date: 8/22/12
 * Time: 4:07 PM
 */
public class FakeObservableFuture<V> extends DefaultObservableFuture<V> {

    public FakeObservableFuture(Object sender, V v) {
        super(sender);
        this.setSuccess(v);
    }
}
