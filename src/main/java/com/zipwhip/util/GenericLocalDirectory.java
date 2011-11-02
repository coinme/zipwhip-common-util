package com.zipwhip.util;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/11
 * Time: 6:39 PM
 * <p/>
 * Something basic/generic
 */
public class GenericLocalDirectory<TKey, TValue> extends GenericDirectory<TKey, TValue> implements LocalDirectory<TKey, TValue> {

    protected static final Comparator COMPARATOR = new HashCodeComparator();

    protected Factory<Collection<TValue>> factory = null;
    protected Map<TKey, Collection<TValue>> data = null;

    public GenericLocalDirectory(Factory<Collection<TValue>> factory) {
        this.factory = factory;
    }

    @Override
    public Set<TKey> keySet() {
        if (data == null) {
            return null;
        }

        return data.keySet();
    }

    @Override
    public boolean isEmpty() {
        if (data == null) {
            return true;
        }

        return data.isEmpty();
    }

    @Override
    public void clear() {
        if (data == null) {
            return;
        }

        data.clear();
    }

    @Override
    public Collection<TValue> get(TKey key) {
        if (data == null){
            return null;
        }

        return data.get(key);
    }

    @Override
    protected Collection<TValue> getOrCreateCollection(TKey key) {
        if (data == null) {
            synchronized (this) {
                if (data == null) {
                    data = createStore();
                }
            }
        }

        Collection<TValue> collection = data.get(key);

        if (collection == null) {
            synchronized (data) {
                collection = data.get(key);
                if (collection == null) {
                    try {
                        collection = factory.create();
                    } catch (Exception e) {
                        // TODO: figure out if we should add an exception to the .add() method
                        throw new RuntimeException("Cannot create collection via factory", e);
                    }

                    data.put(key, collection);
                }
            }
        }

        return collection;
    }

    protected Map<TKey, Collection<TValue>> createStore() {
        return Collections.synchronizedMap(new TreeMap<TKey, Collection<TValue>>(COMPARATOR));
    }

    @Override
    protected void onDestroy() {
        data.clear();
    }

}
