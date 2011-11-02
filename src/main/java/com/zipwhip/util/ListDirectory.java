package com.zipwhip.util;

import com.zipwhip.lifecycle.DestroyableBase;
import org.apache.commons.collections.list.TreeList;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/27/11
 * Time: 10:59 AM
 *
 * An in memory implementation backed by a {@code List} implementation.
 */
public class ListDirectory <TKey, TValue> extends GenericLocalDirectory<TKey, TValue> {

    public ListDirectory() {
        super(new Factory<Collection<TValue>>() {
            @Override
            public Collection<TValue> create() throws Exception {
                return Collections.synchronizedList(new TreeList());
            }
        });
    }

    public ListDirectory(Collection<TValue> values, InputCallable<TKey,TValue> sorter) {
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
