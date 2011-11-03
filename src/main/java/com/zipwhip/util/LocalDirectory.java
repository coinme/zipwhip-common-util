package com.zipwhip.util;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/11
 * Time: 6:20 PM
 * <p/>
 * For local in JVM directories
 */
public interface LocalDirectory<TKey, TValue> extends Directory<TKey, TValue>, Serializable {

	/**
	 * Returns a set of the keys that have been used in the past. There is no guarantee that each key has values.
	 *
	 * For example, clearing a collection does not remove the key from the set. If it does, that's an implementation
	 * detail and not a guarantee across implementations.
	 *
	 * @return
	 */
	Set<TKey> keySet();

	/**
	 * Returns if the directory has no data.
	 *
	 * @return
	 */
	boolean isEmpty();

	void clear();

}
