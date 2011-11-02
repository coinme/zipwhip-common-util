package com.zipwhip.util;

import com.zipwhip.lifecycle.DestroyableBase;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/26/11
 * Time: 10:50 AM
 */
public class SetDirectory<TKey, TValue> extends GenericLocalDirectory<TKey, TValue> {

    public SetDirectory() {
        super(new Factory<Collection<TValue>>() {
            @Override
            public Collection<TValue> create() throws Exception {
                return Collections.synchronizedSet(new TreeSet<TValue>(COMPARATOR));
            }
        });
    }

    public SetDirectory(Collection<TValue> values, InputCallable<TKey,TValue> sorter) {
        this();

        if (CollectionUtil.isNullOrEmpty(values)){
            return;
        }
        if (sorter == null){
            throw new NullPointerException("Need to have a sorter");
        }

        synchronized (values) {
            for(TValue value : values){
                TKey key = sorter.call(value);

                add(key, value);
            }
        }

    }

}
