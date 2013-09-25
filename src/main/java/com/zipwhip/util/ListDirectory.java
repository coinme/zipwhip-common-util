package com.zipwhip.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/27/11
 * Time: 10:59 AM
 *
 * An in memory implementation backed by a {@code List} implementation.
 */
public class ListDirectory<TKey, TValue> extends GenericLocalDirectory<TKey, TValue> {

    private static final long serialVersionUID = 1217950736713706717L;

    public ListDirectory() {
		super(SerializableFactory.<TValue>getInstance());
	}

	public ListDirectory(Collection<TValue> values, InputCallable<TValue, TKey> sorter) throws Exception {
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

        private static final long serialVersionUID = -8463768013903366748L;

        private static final SerializableFactory INSTANCE = new SerializableFactory();

        @Override
        public Collection<T> create() {
            return Collections.synchronizedList(new LinkedList<T>());
        }

        @SuppressWarnings("unchecked")
        public static <T> Factory<Collection<T>> getInstance() {
            return INSTANCE;
        }
    }
}
