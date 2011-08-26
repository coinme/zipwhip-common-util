package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA. User: Michael Date: 8/19/11 Time: 4:22 PM
 * 
 * Serialize something to a string
 */
public interface Serializer<T> {

    /**
     * Serialize your object to this output
     * 
     * @param item
     *        the thing you want to serialize
     * @return the serialized string representation of the item
     */
    String serialize(T item);

}
