package com.zipwhip.executors;

import com.zipwhip.lifecycle.CascadingDestroyable;
import com.zipwhip.lifecycle.CascadingDestroyableBase;
import com.zipwhip.lifecycle.Destroyable;
import com.zipwhip.lifecycle.DestroyableBase;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 9/12/12
 * Time: 11:14 AM
 *
 * Subclass this to work with executors.
 */
public abstract class ExecutorAdapterBase extends AbstractExecutorService implements CascadingDestroyable {

    private CascadingDestroyable destroyableHelper = new CascadingDestroyableBase() {
        @Override
        protected void onDestroy() {

        }
    };

    private final CountDownLatch latch = new CountDownLatch(1);
    private final Executor executor;

    public ExecutorAdapterBase(Executor executor) {
        if (executor == null){
            executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(this.toString()));
            this.link(new DestroyableBase() {
                @Override
                protected void onDestroy() {
                    ((ExecutorService)ExecutorAdapterBase.this.executor).shutdownNow();
                }
            });
        }
        this.executor = executor;
    }

    @Override
    public void shutdown() {
        this.destroy();
        latch.countDown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();

        return null;
    }


    @Override
    public boolean isShutdown() {
        return isDestroyed();
    }

    @Override
    public boolean isTerminated() {
        return isDestroyed();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override
    public void link(Destroyable destroyable) {
        destroyableHelper.link(destroyable);
    }

    @Override
    public void unlink(Destroyable destroyable) {
        destroyableHelper.unlink(destroyable);
    }

    @Override
    public void destroy() {
        destroyableHelper.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyableHelper.isDestroyed();
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        if (executor == null) {
            return String.format("[%s: null]", this.getClass().getCanonicalName());
        }

        return String.format("[%s: %s]", this.getClass().getCanonicalName(), executor.toString());
    }
}
