package com.zipwhip.util;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: Dec 3, 2010
 * Time: 4:00:29 AM
 * <p/>
 * Just a simple memory implementation
 */
public class MapDirectory<TKey, TValue> extends GenericLocalDirectory<TKey, TValue> {

	public MapDirectory() {
		super(new Factory<Collection<TValue>>() {
			@Override
			public Collection<TValue> create() throws Exception
			{
				return Collections.synchronizedSet(new TreeSet<TValue>(COMPARATOR));
			}
		});
	}

}
