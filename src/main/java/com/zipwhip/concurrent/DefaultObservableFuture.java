package com.zipwhip.concurrent;

import com.zipwhip.events.ObservableHelper;
import com.zipwhip.events.Observer;

import java.util.concurrent.*;

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
    public boolean isFailed() {
        return cause != null;
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
    public synchronized boolean setSuccess(V result) {
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
    public synchronized boolean setFailure(Throwable cause) {
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
        // remove it regardless of completion. Otherwise is infinite loop if observer removes self.

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

        if (!doneCountDownLatch.await(timeout, unit)) {
            return false;
        }

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

    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.await();
        return result;
    }

    @Override
    public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!this.await(l, timeUnit)) {
            throw new TimeoutException("Didn't complete within " + l + " " + timeUnit);
        }

        return result;
    }

    @Override
    public boolean cancel(boolean b) {
        return cancel();
    }

    private void notifyObserver(Observer<ObservableFuture<V>> observer) {
        observableHelper.notifyObserver(observer, this.sender, this);
    }

    private void notifyObservers() {
        observableHelper.notifyObservers(this.sender, this);
        observableHelper.destroy();
    }

}
