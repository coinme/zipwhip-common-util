package com.zipwhip.concurrent;

import com.zipwhip.events.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When the future finishes, if it's the current "connectFuture" clean up the references.
 */
public class CopyFutureStatusToNestedFutureWithCustomResultObserver<T1, T2> implements Observer<ObservableFuture<T1>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CopyFutureStatusToNestedFutureWithCustomResultObserver.class);

    final ObservableFuture<T2> nestedFuture;
    final T2 result;

    public CopyFutureStatusToNestedFutureWithCustomResultObserver(ObservableFuture<T2> nestedFuture, T2 result) {
        this.nestedFuture = nestedFuture;
        this.result = result;
    }

    @Override
    public void notify(Object sender, ObservableFuture<T1> future) {
        LOGGER.trace(String.format("Cloning the state from %s to %s", future, nestedFuture));
        // notify people that care.
        NestedObservableFuture.syncState(future, nestedFuture, result);
    }
}
