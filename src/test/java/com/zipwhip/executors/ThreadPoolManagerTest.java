/**
 * 
 */
package com.zipwhip.executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Test;

/**
 * @author jdinsel
 *
 */
public class ThreadPoolManagerTest {

	@Test
	public void testShutdown()
	{
		ScheduledExecutorService scheduledThreadPool = ThreadPoolManager.getInstance().getScheduledThreadPool();
		ExecutorService fixedThreadPool = ThreadPoolManager.getInstance().getFixedThreadPool();

		scheduledThreadPool.shutdown();
		fixedThreadPool.shutdown();

		assertTrue(scheduledThreadPool.isShutdown());
		assertTrue(fixedThreadPool.isShutdown());

		scheduledThreadPool = ThreadPoolManager.getInstance().getScheduledThreadPool();
		fixedThreadPool = ThreadPoolManager.getInstance().getFixedThreadPool();

		assertFalse(scheduledThreadPool.isShutdown());
		assertFalse(fixedThreadPool.isShutdown());
	}
	/**
	 * Test method for {@link com.zipwhip.executors.ThreadPoolManager#getInstance()}.
	 */
	@Test
	public void testGetInstance()
	{
		assertNotNull(ThreadPoolManager.getInstance());
	}

	/**
	 * Test method for {@link com.zipwhip.executors.ThreadPoolManager#getScheduledThreadPool()}.
	 */
	@Test
	public void testGetScheduledThreadPool()
	{
		assertNotNull(ThreadPoolManager.getInstance().getScheduledThreadPool());
	}

	/**
	 * Test method for {@link com.zipwhip.executors.ThreadPoolManager#getFixedThreadPool()}.
	 */
	@Test
	public void testGetFixedThreadPool()
	{
		assertNotNull(ThreadPoolManager.getInstance().getFixedThreadPool());
	}

	@Test
	public void testOperation() throws InterruptedException
	{

		GarbageThread[] threads = new GarbageThread[500];
		Executor executor = ThreadPoolManager.getInstance().getFixedThreadPool();

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new GarbageThread();
		}
		for (int i = 0; i < threads.length; i++) {
			executor.execute(threads[i]);
		}

		Thread.sleep(1010);

		for (int i = 0; i < threads.length; i++) {
			assertTrue("Thread " + i + " has not run!", threads[i].hasRan());
		}

		executor = null;
	}

	private class GarbageThread implements Runnable {
		boolean ran = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ran = true;
		}

		public final boolean hasRan()
		{
			return ran;
		}
	}
}
