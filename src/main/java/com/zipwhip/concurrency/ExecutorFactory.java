package com.zipwhip.concurrency;

import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/2/12
 * Time: 7:04 PM
 * <p/>
 * Creates executors where the underlying thread has a specific name prefix.
 */
public interface ExecutorFactory {

    Executor create(String name);

}
