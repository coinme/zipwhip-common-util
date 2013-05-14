package com.zipwhip.concurrent;

import com.zipwhip.events.Observer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: jed
 * Date: 8/22/12
 * Time: 4:07 PM
 *
 * An observable future where the result is already known.
 */
public class FakeObservableFuture<V> implements ObservableFuture<V> {

    private V result;
    private Object sender;

    public FakeObservableFuture(Object sender, V v) {
        this.result = v;
        this.sender = sender;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailed() {
        return false;
    }

    @Override
    public Throwable getCause() {
        return null;
    }

    @Override
    public boolean setSuccess(V result) {
        return false;
    }

    @Override
    public boolean setFailure(Throwable cause) {
        return false;
    }

    @Override
    public void addObserver(Observer<ObservableFuture<V>> observer) {
        observer.notify(sender, this);
    }

    @Override
    public void removeObserver(Observer<ObservableFuture<V>> observer) {

    }

    @Override
    public void await() throws InterruptedException {

    }

    @Override
    public void awaitUninterruptibly() {

    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return true;
    }

    @Override
    public V getResult() {
        return result;
    }
}
