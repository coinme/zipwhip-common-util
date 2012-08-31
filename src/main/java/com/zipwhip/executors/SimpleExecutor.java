package com.zipwhip.executors;

import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 8/2/11
 * Time: 2:42 PM
 * <p/>
 * Executes something synchronously
 */
public class SimpleExecutor implements Executor {

    private static SimpleExecutor instance;

    public static SimpleExecutor getInstance() {
        if (instance == null) {
            instance = new SimpleExecutor();
        }
        return instance;
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
