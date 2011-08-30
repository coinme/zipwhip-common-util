package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA. User: Michael Date: 8/19/11 Time: 4:22 PM
 * 
 * Represents a parser that can parse from 1 type to another
 */
public interface Serializer<TSource, TDestination> {

    /**
     * Serialize this object to this output
     *
     * @param source this
     * @return the serialized representation of this
     */
    TDestination serialize(TSource source);

}
