package com.zipwhip.events;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 11:14 PM
 *
 * Event object with some specific data.
 */
public class DataEventObject<T> extends EventObject {

    private static final long serialVersionUID = -6216814771966359748L;

    private T data;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DataEventObject(Object source, T data) {
        super(source);

        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
