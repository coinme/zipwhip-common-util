package com.zipwhip.events;

import com.zipwhip.executors.SimpleExecutor;
import com.zipwhip.lifecycle.CascadingDestroyableBase;
import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.HashCodeComparator;
import com.zipwhip.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private String name;
    private Executor executor;
    private final Set<Observer<T>> observers = Collections.synchronizedSet(new TreeSet<Observer<T>>(HashCodeComparator.getInstance()));

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
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        synchronized (observers) {
            if (observers == null) {
                return;
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
                    e.printStackTrace();
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
                observer.notify(sender, result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        synchronized (observers) {
            if (observers != null){
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
