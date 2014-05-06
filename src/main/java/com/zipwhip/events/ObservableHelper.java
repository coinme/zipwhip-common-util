package com.zipwhip.events;

import com.zipwhip.executors.SimpleExecutor;
import com.zipwhip.lifecycle.CascadingDestroyableBase;
import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 8/1/11
 * Time: 4:31 PM
 * <p/>
 * A base class that simplifies the act of being observed
 */
public class ObservableHelper<T> extends CascadingDestroyableBase implements Observable<T>, Observer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservableHelper.class);

    private final String name;
    private final Executor executor;
    private final Set<Observer<T>> observers = Collections.synchronizedSet(new CopyOnWriteArraySet<Observer<T>>());

    public ObservableHelper() {
        this(null, null);
    }

    public ObservableHelper(Executor executor) {
        this(null, executor);
    }

    public ObservableHelper(String name) {
        this(name, null);
    }

    public ObservableHelper(String name, Executor executor) {
        this.name = name;

        if (executor == null){
            this.executor = SimpleExecutor.getInstance();
        } else {
            this.executor = executor;
        }
    }

    @Override
    public void addObserver(Observer<T> observer) {
        synchronized (observers) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("Added observer [%s] to me [%s]", observer, this));
            }

            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        synchronized (observers) {
            if (observers == null) {
                return;
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("Removed observer [%s] to me [%s]", observer, this));
            }

            observers.remove(observer);
        }
    }

    /**
     * Notify all the observers that their thing occurred.
     *
     * @param sender who is notifying of the event!
     * @param result the result that the observers will hear about.
     */
    public void notifyObservers(final Object sender, final T result) {
        if (CollectionUtil.isNullOrEmpty(observers)) {
            return;
        }

        synchronized (observers) {
            for (Observer<T> observer : observers) {
                try {
                    notifyObserver(observer, sender, result);
                } catch (Throwable e) {
                    // We don't have a logger, oh well...
                    LOGGER.error(String.format("Got an exception trying to notifyObserver %s of [%s, %s]:", observer, sender, result), e);
                }
            }
        }
    }

    /**
     * Notify all the observer that its thing occurred.
     *
     * @param observer the observer to notify.
     * @param sender who is notifying of the event.
     * @param result the result that the observers will hear about.
     */
    public void notifyObserver(final Observer<T> observer, final Object sender, final T result) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(String.format("[%s] notifying [%s](%s)", name, observer, result));
                }

                observer.notify(sender, result);
            }

            @Override
            public String toString() {
                return String.format("[Observer: %s, result: %s]", observer.toString(), result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (observers != null){
            synchronized (observers) {
                observers.clear();
            }
        }
    }

    /**
     * This allows you to forward events from one ObservableHelper to another. You can just nest them.
     *
     * @param sender The sender might not be the same object every time, so we'll let it just be object, rather than generics.
     * @param item Rich object representing the notification.
     */
    @Override
    public void notify(Object sender, T item) {
        this.notifyObservers(sender, item);
    }

    @Override
    public String toString() {
        if (StringUtil.isNullOrEmpty(name)) {
            return super.toString();
        }
        return StringUtil.join("[name=", name, ", number=", observers.size(), "]");
    }

}
