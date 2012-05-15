package com.zipwhip.binding;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:05 AM
 *
 * Reads the data and produces records.
 */
public interface DataReader<T> {

    Set<Record> read(T data) throws Exception;

}
