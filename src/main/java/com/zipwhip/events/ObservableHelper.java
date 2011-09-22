package com.zipwhip.events;

import com.zipwhip.executors.SimpleExecutor;
import com.zipwhip.lifecycle.DestroyableBase;
import com.zipwhip.util.CollectionUtil;

import java.util.LinkedList;
import java.util.List;
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
    private final List<Observer<T>> observers = new LinkedList<Observer<T>>();

    public ObservableHelper() {
        this(new SimpleExecutor());
    }

    public ObservableHelper(Executor executor) {
        this.executor = executor;
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
     * @param item   the item that the observers will hear about.
     */
    public void notifyObservers(final Object sender, final T item) {

        if (CollectionUtil.isNullOrEmpty(observers)) {
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (observers) {
                    for (Observer<T> observer : observers) {
                        observer.notify(sender, item);
                    }
                }
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
