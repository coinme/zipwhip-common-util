package com.zipwhip.util;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/11
 * Time: 6:20 PM
 * <p/>
 * For local in JVM directories
 */
public interface LocalDirectory<TKey, TValue> extends Directory<TKey, TValue> {

    Set<TKey> keySet();

    boolean isEmpty();

    void clear();

}
