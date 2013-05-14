package com.zipwhip.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
			public Collection<TValue> create() {
				return Collections.synchronizedList(new ArrayList<TValue>());
			}
		});
	}

	public ListDirectory(Collection<TValue> values, InputCallable<TValue, TKey> sorter) {
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
