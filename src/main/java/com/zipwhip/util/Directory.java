package com.zipwhip.util;

import com.zipwhip.lifecycle.Destroyable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: Dec 3, 2010
 * Time: 3:59:46 AM
 * <p/>
 * A Directory is an easy to use add/remove class that can return a list of things given a specific key.
 */
public interface Directory<TKey, TValue> extends Destroyable {

    void add(TKey key, TValue value);

    void remove(TKey key, TValue value);

    Set<TKey> keySet();

    Collection<TValue> get(TKey key);

    /**
     * This method is deprecated. Use {@code get} instead.
     *
     * @param key The key.
     * @return A list based on the key.
     */
    @Deprecated
    List<TValue> list(TKey key);

    boolean isEmpty();

    void clear();
}
