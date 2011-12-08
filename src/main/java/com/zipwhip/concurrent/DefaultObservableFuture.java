package com.zipwhip.concurrent;

import com.zipwhip.events.ObservableHelper;
import com.zipwhip.events.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class DefaultObservableFuture<V> implements ObservableFuture<V> {

    private boolean cancelled;
    private boolean success;
    private Throwable cause;
    private ObservableHelper<ObservableFuture<V>> observableHelper;
    private CountDownLatch doneCountDownLatch = new CountDownLatch(1);
    private V result;
    private Object sender;

    public DefaultObservableFuture(Object sender) {
        this(sender, null);
    }

    public DefaultObservableFuture(Object sender, Executor executor) {
        this.sender = sender;
        observableHelper = new ObservableHelper<ObservableFuture<V>>(executor);
    }

    @Override
    public boolean isDone() {
        return isCancelled() || isSuccess() || (cause != null);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public synchronized boolean cancel() {
        if (isCancelled()) {
            return false;
        }

        cancelled = true;
        success = false;
        doneCountDownLatch.countDown();

        notifyObservers();

        return true;
    }

    @Override
    public boolean setSuccess(V result) {
        if (isDone()) {
            return false;
        }

        this.result = result;
        cancelled = false;
        success = true;
        doneCountDownLatch.countDown();

        notifyObservers();

        return true;
    }

    @Override
    public boolean setFailure(Throwable cause) {
        if (isDone()){
            return false;
        }

        this.cause = cause;
        this.success = false;
        this.cancelled = false;
        this.doneCountDownLatch.countDown();

        notifyObservers();

        return true;
    }

    @Override
    public V getResult() {
        return result;
    }

    @Override
    public void addObserver(Observer<ObservableFuture<V>> observer) {
        if (isDone()){
            notifyObserver(observer);
            return;
        }

        observableHelper.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer<ObservableFuture<V>> observer) {
        if (isDone()){
            notifyObserver(observer);
            return;
        }

        observableHelper.removeObserver(observer);
    }

    @Override
    public void await() throws InterruptedException {
        doneCountDownLatch.await();
    }

    @Override
    public void awaitUninterruptibly() {

        try {
            doneCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {

        doneCountDownLatch.await(timeout, unit);

        return this.isDone();
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {

        try {
            doneCountDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.isDone();
    }

    private void notifyObserver(Observer<ObservableFuture<V>> observer) {
        observableHelper.notifyObserver(observer, this.sender, this);
    }

    private void notifyObservers() {
        observableHelper.notifyObservers(this.sender, this);
    }

}
