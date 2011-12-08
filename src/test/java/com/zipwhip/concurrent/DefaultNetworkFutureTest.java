package com.zipwhip.concurrent;

import com.zipwhip.events.MockObserver;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 10/10/11
 * Time: 3:50 PM
 *
 * Test that our future works as expected
 */
public class DefaultNetworkFutureTest {

    MockObserver<ObservableFuture<Object>> observer = new MockObserver<ObservableFuture<Object>>();

    @Test
    public void testSuccess() throws Exception {
        ObservableFuture<Object> future = new DefaultObservableFuture<Object>(this);

        assertVirgin(future);

        Object result = new Object();

        future.addObserver(observer);

        future.setSuccess(result);

        assertTrue(future.isSuccess());

        assertSame("results are same", result, future.getResult());
        assertHitCount(observer, 1);
        assertNotVirgin(future);

    }

    @Test
    public void testCancel() throws Exception {
        ObservableFuture<Object> future = new DefaultObservableFuture<Object>(this);

        assertVirgin(future);

        future.addObserver(observer);

        future.cancel();

        assertTrue(future.isCancelled());

        assertHitCount(observer, 1);
        assertNotVirgin(future);

    }

    @Test
    public void testThrowable() throws Exception {
        ObservableFuture<Object> future = new DefaultObservableFuture<Object>(this);

        assertVirgin(future);

        future.addObserver(observer);

        Throwable t = new Throwable();

        future.setFailure(t);

        assertFalse(future.isCancelled());
        assertTrue(future.isDone());
        assertSame(future.getCause(), t);

        assertHitCount(observer, 1);
        assertNotVirgin(future);

    }

    private void assertHitCount(MockObserver<ObservableFuture<Object>> observer, int i) {
        assertTrue(observer.getHitCount() == i);
    }

    private void assertNotVirgin(ObservableFuture<?> future) {
        assertTrue(future.isDone());
    }

    private void assertVirgin(ObservableFuture<?> future) {
        assertFalse("future is not done", future.isDone());
        assertFalse("future is not cancelled", future.isCancelled());
        assertFalse("future is not success", future.isSuccess());
        assertNull("result is null", future.getResult());
        assertNull("cause is null", future.getCause());
    }
}
