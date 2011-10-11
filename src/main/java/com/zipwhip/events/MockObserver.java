package com.zipwhip.events;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 10/10/11
 * Time: 3:54 PM
 *
 * For testing.
 */
public class MockObserver<T> implements Observer<T> {

    private Object lastSender;
    private T lastItem;
    private boolean called;
    private int hitCount;

    @Override
    public void notify(Object sender, T item) {
        this.lastSender = sender;
        this.lastItem = item;
        this.called = true;
        this.hitCount++;
    }

    public Object getLastSender() {
        return lastSender;
    }

    public void setLastSender(Object lastSender) {
        this.lastSender = lastSender;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

}
