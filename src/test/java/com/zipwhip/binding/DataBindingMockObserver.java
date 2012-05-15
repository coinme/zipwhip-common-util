package com.zipwhip.binding;

import com.zipwhip.events.Observable;
import com.zipwhip.events.Observer;

import java.util.EventObject;

public class DataBindingMockObserver<T extends EventObject> implements Observer<T> {

    T eventObject;
    int hitCount;

    @Override
    public void notify(Object sender, T e) {
        this.eventObject = e;
        this.hitCount ++;
    }

    public void update(Observable<T> observable, T e) {
        this.eventObject = e;
        this.hitCount ++;
    }

    public boolean hit(int count) {
        return this.hitCount == count;
    }
    public boolean hit() {
        return this.hitCount > 0;
    }

    public void reset() {
        this.hitCount = 0;
        this.eventObject = null;
    }

    public T getEventObject() {
        return eventObject;
    }

    public void setEventObject(T eventObject) {
        this.eventObject = eventObject;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }
}
