package com.zipwhip.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/26/11
 * Time: 10:50 AM
 */
public class SetDirectory<TKey, TValue> extends GenericLocalDirectory<TKey, TValue> {

    private static final long serialVersionUID = -7397620823732731308L;

    public SetDirectory() {
        super(SerializableFactory.<TValue>getInstance());
    }

    public SetDirectory(Collection<TValue> values, InputCallable<TValue, TKey> sorter) throws Exception {
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

    private static class SerializableFactory<T> implements Factory<Collection<T>>, Serializable {

        private static final long serialVersionUID = -6191224293092850629L;

        private static final SerializableFactory INSTANCE = new SerializableFactory();

        @Override
        public Collection<T> create() {
            return Collections.synchronizedSet(new TreeSet<T>(COMPARATOR));
        }

        @SuppressWarnings("unchecked")
        public static <T> Factory<Collection<T>> getInstance() {
            return INSTANCE;
        }
    }
}
