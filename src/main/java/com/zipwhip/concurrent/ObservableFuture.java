package com.zipwhip.concurrent;

import com.zipwhip.events.Observer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An interface that represents a unit of work that will
 */
public interface ObservableFuture<V> extends Future<V> {

    /**
     * @return {@code true} if and only if this future is
     *         complete, regardless of whether the operation was successful, failed,
     *         or cancelled.
     */
    boolean isDone();

    /**
     * @return {@code true} if and only if this future was
     *         cancelled by a {@link #cancel()} method.
     */
    boolean isCancelled();

    /**
     * @return {@code true} if and only if the operation was completed
     *         successfully.
     */
    boolean isSuccess();

    /**
     * @return {@code true} if and only if the operation failed.
     */
    boolean isFailed();

    /**
     * Returns the cause of the failed I/O operation if the I/O operation has
     * failed.
     *
     * @return the cause of the failure.
     *         {@code null} if succeeded or this future is not
     *         completed yet.
     */
    Throwable getCause();

    /**
     * Cancels the I/O operation associated with this future
     * and notifies all listeners if canceled successfully.
     *
     * @return {@code true} if and only if the operation has been canceled.
     *         {@code false} if the operation can't be canceled or is already
     *         completed.
     */
    boolean cancel();

    /**
     * Marks this future as a success and notifies all
     * listeners.
     *
     * @param result The result of the successful computation.
     * @return {@code true} if and only if successfully marked this future as
     *         a success. Otherwise {@code false} because this future is
     *         already marked as either a success or a failure.
     */
    boolean setSuccess(V result);

    /**
     * Marks this future as a failure and notifies all
     * listeners.
     *
     * @param cause The {@code Throwable} that caused the failure.
     * @return {@code true} if and only if successfully marked this future as
     *         a failure. Otherwise {@code false} because this future is
     *         already marked as either a success or a failure.
     */
    boolean setFailure(Throwable cause);

    /**
     * Adds the specified listener to this future.  The
     * specified listener is notified when this future is
     * {@linkplain #isDone() done}.  If this future is already
     * completed, the specified listener is notified immediately.
     *
     * @param observer The observer to add.
     */
    void addObserver(Observer<ObservableFuture<V>> observer);

    /**
     * Removes the specified listener from this future.
     * The specified listener is no longer notified when this
     * future is {@linkplain #isDone() done}.  If the specified
     * listener is not associated with this future, this method
     * does nothing and returns silently.
     *
     * @param observer The observer to remove.
     */
    void removeObserver(Observer<ObservableFuture<V>> observer);

    /**
     * Waits for this future to be completed.
     *
     * @throws InterruptedException if the current thread was interrupted
     */
    void await() throws InterruptedException;

    /**
     * Waits for this future to be completed without
     * interruption.  This method catches an {@link InterruptedException} and
     * discards it silently.
     */
    void awaitUninterruptibly();

    /**
     * Waits for this future to be completed within the
     * specified time limit.
     *
     * @param timeout The time to wait before an interruption is possible.
     * @param unit The unit of time to be used for {@code timeout}
     * @return {@code true} if and only if the future was completed within
     *         the specified time limit
     * @throws InterruptedException if the current thread was interrupted
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Waits for this future to be completed within the
     * specified time limit without interruption. This method catches an
     * {@link InterruptedException} and discards it silently.
     *
     * @param timeout The time to wait before an interruption is possible.
     * @param unit The unit of time to be used for {@code timeout}
     * @return {@code true} if and only if the future was completed within
     *         the specified time limit.
     */
    boolean awaitUninterruptibly(long timeout, TimeUnit unit);

    /**
     * Gets the result or null if it hasn't completed (you  {@code isSuccess} to check if this is done).
     *
     * @return V
     */
    V getResult();

}
