package com.zipwhip.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: jdinsel
 * Date: 11/1/11
 * Time: 6:18 PM
 * <p/>
 * So you can put objects into a TreeSet
 */
public class HashCodeComparator<T> implements Comparator<T>, Serializable {

	private static HashCodeComparator INSTANCE;
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(T o1, T o2) {
		if ((o1 == null) && (o2 == null)) {
			return 0;
		} else if ((o1 == null) && (o2 != null)) {
			return -1;
		} else if ((o1 != null) && (o2 == null)) {
			return 1;
		}

		int left = o1.hashCode();
		int right = o2.hashCode();

		if (left < right) {
			return -1;
		} else if (left > right) {
			return 1;
		} else {
			return 0;
		}
	}

    public static <T> HashCodeComparator<T> getInstance() {
        if (INSTANCE == null) {
            // NOTE: This is not thread safe. Different threads might get different values. That's ok.
            INSTANCE = new HashCodeComparator<T>();
        }

        return INSTANCE;
    }

}
