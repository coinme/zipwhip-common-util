package com.zipwhip.util;

import com.zipwhip.lifecycle.DestroyableBase;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/27/11
 * Time: 10:59 AM
 *
 * An in memory implementation backed by a {@code List} implementation.
 */
public class ListDirectory <TKey, TValue> extends DestroyableBase implements Directory<TKey, TValue> {

    final Map<TKey, List<TValue>> listeners = new HashMap<TKey, List<TValue>>();

    public ListDirectory() {

    }

    public ListDirectory(List<TValue> values, InputCallable<TKey,TValue> sorter) {
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

    @Override
    public List<TValue> list(TKey uri) {
        return get(uri);
    }

    /**
     * The return value for this method can be safely cast to a {@code List<TValue>}
     * for client who need to access methods of the {@code List} interface.
     *
     * @param uri The key to search on.
     * @return A {@code List<TValue>} of items based on the key or null.
     */
    @Override
    public List<TValue> get(TKey uri) {
        if (listeners == null) {
            return null;
        }
        return listeners.get(uri);
    }

    @Override
    public void add(TKey uri, TValue signalObserver) {
        List<TValue> list = listeners.get(uri);
        if (CollectionUtil.isNullOrEmpty(list)) {
            list = Collections.synchronizedList(new ArrayList<TValue>());
            listeners.put(uri, list);
        }
        list.add(signalObserver);
    }

    @Override
    public void remove(TKey uri, TValue signalObserver) {
        List<TValue> list = listeners.get(uri);
        if (CollectionUtil.isNullOrEmpty(list)) {
            return;
        }
        list.remove(signalObserver);

        if (list.isEmpty()) {
            listeners.remove(uri);
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
