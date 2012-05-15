package com.zipwhip.events;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 11:29 PM
 *
 * For when your data has some order, and we need to know where it is.
 */
public class OrderedDataEventObject<T> extends DataEventObject<T> {

    int index;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public OrderedDataEventObject(Object source, T data, int index) {
        super(source, data);

        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
