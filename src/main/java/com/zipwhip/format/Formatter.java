package com.zipwhip.format;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 10/4/12
 * Time: 3:29 PM
 *
 * Takes your input string and creates a format
 */
public interface Formatter<T> {

    T format(T input);

}
