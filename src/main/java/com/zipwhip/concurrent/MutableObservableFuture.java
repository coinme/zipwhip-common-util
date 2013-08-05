package com.zipwhip.concurrent;

/**
 * Date: 7/31/13
 * Time: 2:37 PM
 *
 * @author Michael
 * @version 1
 */
public interface MutableObservableFuture<V> extends ObservableFuture<V> {

    boolean setFailure(Throwable throwable);

    boolean setSuccess(V value);

}
