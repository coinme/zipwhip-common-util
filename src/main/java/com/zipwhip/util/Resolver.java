package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Russ
 * Date: 3/4/14
 * Time: 4:38 PM
 */
public interface Resolver<T, V> {

    V resolve(T source) throws Exception;

}
