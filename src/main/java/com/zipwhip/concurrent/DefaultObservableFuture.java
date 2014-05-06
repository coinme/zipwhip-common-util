package com.zipwhip.concurrent;

import com.zipwhip.events.ObservableHelper;
import com.zipwhip.events.Observer;
import com.zipwhip.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 *
 */
public class DefaultObservableFuture<V> implements MutableObservableFuture<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultObservableFuture.class);

    private volatile boolean cancelled;
    private volatile boolean success;
    private volatile boolean failed;
    private volatile V result;
    private volatile Throwable cause;

    private final ObservableHelper<ObservableFuture<V>> observableHelper;
    private final CountDownLatch doneCountDownLatch = new CountDownLatch(1);
    private final Object sender;
    private final String name;

    public DefaultObservableFuture(Object sender) {
        this(sender, null);
    }

    public DefaultObservableFuture(Object sender, Executor executor) {
        this(sender, executor, null);
    }

    public DefaultObservableFuture(Object sender, Executor executor, String name) {
        this.sender = sender;
        this.observableHelper = new ObservableHelper<ObservableFuture<V>>(name, executor);
        this.name = name;
    }

    @Override
    public boolean isDone() {
        return isCancelled() || isSuccess() || isFailed();
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
        return failed;
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

        // setFailure accepts null as a possible "cause". In order to recognize the done state, we need to set
        // failed = true.
        this.failed = true;
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
            LOGGER.error("Count down threw exception", e);
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
            LOGGER.error("Count down threw exception", e);
        }

        return this.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.await();

        if (isFailed()) {
            throw new ExecutionException(getCause());
        } else if (isCancelled()) {
            throw new CancellationException("Cancelled before finished");
        }

        return result;
    }

    @Override
    public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!this.await(l, timeUnit)) {
            throw new TimeoutException("Didn't complete within " + l + " " + timeUnit);
        }

        if (isFailed()) {
            throw new ExecutionException(getCause());
        } else if (isCancelled()) {
            throw new CancellationException("Cancelled before finished");
        }

        return result;
    }

    @Override
    public boolean cancel(boolean b) {
        return cancel();
    }

    @Override
    public String toString() {
        String state;

        if (isFailed()) {
            state = String.format("FAILED[%s]", cause != null ? cause.getMessage() : null);
        } else if (isCancelled()) {
            state = "CANCELLED";
        } else if (isSuccess()) {
            state = String.format("SUCCESS[%s]", getResult());
        } else {
            state = "PENDING";
        }

        if (StringUtil.isNullOrEmpty(name)) {
            return String.format("[DefaultObservableFuture state:\"%s\"]", state);
        } else {
            return String.format("[DefaultObservableFuture name:\"%s\", state:\"%s\"]", name, state);
        }
    }

    private void notifyObserver(Observer<ObservableFuture<V>> observer) {
        observableHelper.notifyObserver(observer, this.sender, this);
    }

    private void notifyObservers() {
        observableHelper.notifyObservers(this.sender, this);
        observableHelper.destroy();
    }

}
