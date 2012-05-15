package com.zipwhip.binding;

import com.zipwhip.concurrent.DefaultObservableFuture;
import com.zipwhip.concurrent.ObservableFuture;

import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 5:50 PM
 *
 * Just something simple
 */
public class MemoryDataProxy<T> implements DataProxy<T> {

    T data;
    Executor executor;

    /**
     * You can pass in the executor if you want to have control of the Future behavior.
     *
     * @param data
     * @param executor
     */
    public MemoryDataProxy(T data, Executor executor) {
        this(data);
        this.executor = executor;
    }

    /**
     * When "load()" is called, return the data that is passed in here.
     *
     * @param data
     */
    public MemoryDataProxy(T data) {
        this.data = data;
    }

    @Override
    public ObservableFuture<T> load() throws Exception {
        DefaultObservableFuture<T> future = new DefaultObservableFuture<T>(this, executor);

        future.setSuccess(data);

        return future;
    }
}
