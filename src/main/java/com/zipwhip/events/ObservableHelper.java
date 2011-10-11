package com.zipwhip.events;

import com.zipwhip.executors.SimpleExecutor;
import com.zipwhip.lifecycle.DestroyableBase;
import com.zipwhip.util.CollectionUtil;

import java.util.List;
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
public class ObservableHelper<T> extends DestroyableBase implements Observable<T> {

    private Executor executor;
    private final List<Observer<T>> observers = new CopyOnWriteArrayList<Observer<T>>();

    public ObservableHelper() {
        this(null);
    }

    public ObservableHelper(Executor executor) {
        if (executor == null){
            this.executor = new SimpleExecutor();
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

        for (Observer<T> observer : observers) {
            notifyObserver(observer, sender, result);
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

}
