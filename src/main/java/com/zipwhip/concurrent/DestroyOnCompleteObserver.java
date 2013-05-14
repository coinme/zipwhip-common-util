package com.zipwhip.concurrent;

import com.zipwhip.events.Observer;
import com.zipwhip.lifecycle.Destroyable;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 9/10/12
 * Time: 3:35 PM
 *
 * Destroys an object on complete
 */
public class DestroyOnCompleteObserver<T> implements Observer<T> {

    private Destroyable destroyable;

    public DestroyOnCompleteObserver(Destroyable destroyable) {
        this.destroyable = destroyable;
    }

    @Override
    public void notify(Object sender, T item) {
        destroyable.destroy();
        destroyable = null;
    }
}
