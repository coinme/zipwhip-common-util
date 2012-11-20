package com.zipwhip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Russ
 * Date: 10/19/12
 * Time: 11:42 AM
 *
 * This basically wraps the default UncaughtExceptionHandler, so we can log the exception before the app crashes
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    public UncaughtExceptionHandler() {
        this.defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable ex) {
        LOGGER.error("Uncaught exception in thread: " + t.getName(), ex);
        this.defaultUncaughtExceptionHandler.uncaughtException(t, ex);
    }

}
