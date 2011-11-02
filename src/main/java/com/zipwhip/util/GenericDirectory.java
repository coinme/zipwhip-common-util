package com.zipwhip.util;

import com.zipwhip.lifecycle.DestroyableBase;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/11
 * Time: 6:46 PM
 *
 * A base template for all directories
 */
public abstract class GenericDirectory<TKey, TValue> extends DestroyableBase implements Directory<TKey, TValue> {

    @Override
    public void add(TKey key, TValue value) {
        Collection<TValue> collection = getOrCreateCollection(key);

        collection.add(value);
    }

    @Override
    public void remove(TKey key, TValue value) {
        Collection<TValue> collection = get(key);
        if (collection == null){
            return;
        }

        collection.remove(value);
    }

    protected abstract Collection<TValue> getOrCreateCollection(TKey key);

}
