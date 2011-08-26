package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA. User: Michael Date: 8/19/11 Time: 4:22 PM
 * 
 * Serialize the implementing object to a string
 */
public interface Serializer {

    /**
     * Serialize this object to this output
     * 
     * @return the serialized string representation of this
     */
    String serialize();

}
