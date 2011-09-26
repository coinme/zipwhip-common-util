package com.zipwhip.util;

import com.zipwhip.lifecycle.DestroyableBase;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/26/11
 * Time: 10:50 AM
 */
public class SetDirectory<TKey, TValue> extends DestroyableBase implements Directory<TKey, TValue> {

    final Map<TKey, Set<TValue>> listeners = new HashMap<TKey, Set<TValue>>();

    public SetDirectory() {

    }

    public SetDirectory(List<TValue> values, InputCallable<TKey,TValue> sorter) {
        if (CollectionUtil.isNullOrEmpty(values)){
            return;
        }
        if (sorter == null){
            throw new NullPointerException("Need to have a sorter");
        }

        for(TValue value : values){
            TKey key = sorter.call(value);

            add(key, value);
        }
    }

    /**
     * This method is deprecated. Calling it will result in it
     * throwing a RuntimeException.
     */
    @Override
    @Deprecated
    public List<TValue> list(TKey key) {
        throw new RuntimeException("This method is deprecated, use get() instead.");
    }

    /**
     * The return value for this method can be safely cast to a {@code Set<TValue>}
     * for client who need to access methods of the {@code Set} interface.
     *
     * @param key The key to search on.
     * @return A {@code Set<TValue>} of items based on the key or null.
     */
    @Override
    public Set<TValue> get(TKey key) {
        if (listeners == null) {
            return null;
        }
        return listeners.get(key);
    }

    @Override
    public void add(TKey key, TValue value) {
        Set<TValue> list = listeners.get(key);
        if (CollectionUtil.isNullOrEmpty(list)) {
            list = Collections.synchronizedSet(new HashSet<TValue>());
            listeners.put(key, list);
        }
        list.add(value);
    }

    @Override
    public void remove(TKey key, TValue value) {
        Set<TValue> list = listeners.get(key);
        if (CollectionUtil.isNullOrEmpty(list)) {
            return;
        }
        list.remove(value);

        if (list.isEmpty()) {
            listeners.remove(key);
        }

    }

    @Override
    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    @Override
    public void clear() {
        listeners.clear();
    }

    @Override
    public Set<TKey> keySet() {
        return listeners.keySet();
    }

    @Override
    protected void onDestroy() {
        clear();
    }

}
