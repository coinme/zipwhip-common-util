package com.zipwhip.reliable.retry;

import org.junit.Test;

import static org.junit.Assert.*;

/**
* Created by IntelliJ IDEA.
* User: Erickson
* Date: 8/6/12
* Time: 12:26 PM
* To change this template use File | Settings | File Templates.
*/
public class RetryStrategyTests {

    @Test
    public void testConstantRetryIntervals(){
        RetryStrategy retry = new ConstantIntervalRetryStrategy(1000);
//        assertTrue(retry.continueReattempts(0));
//        assertTrue(retry.continueReattempts(-4)); //Yes, we allow negative numbers.
//        assertTrue(retry.continueReattempts(9));
//        assertFalse(retry.continueReattempts(10));

        assertEquals(1000l, retry.getNextRetryInterval(0));
        assertEquals(1000l, retry.getNextRetryInterval((-4))); //Yes, we allow negative numbers.
        assertEquals(1000l, retry.getNextRetryInterval((9)));
        assertEquals(1000l, retry.getNextRetryInterval((10)));

    }

    @Test
    public void testExponentialRetryIntervals(){
        RetryStrategy retry = new ExponentialBackoffRetryStrategy(1000, 1.5d);
//        assertTrue(retry.continueReattempts(0));
//        assertTrue(retry.continueReattempts(-4)); //Yes, we allow negative numbers.
//        assertTrue(retry.continueReattempts(9));
//        assertFalse(retry.continueReattempts(10));

        assertEquals(1000l, retry.getNextRetryInterval(0));
        assertEquals(1000l, retry.getNextRetryInterval((-4))); //Yes, we allow negative numbers.
        assertEquals(25628l, retry.getNextRetryInterval((9)));
        assertEquals(38443l, retry.getNextRetryInterval((10)));

    }
}
