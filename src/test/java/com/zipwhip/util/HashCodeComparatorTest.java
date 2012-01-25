/**
 * 
 */
package com.zipwhip.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author jdinsel
 *
 */
public class HashCodeComparatorTest {

	@Test
	public void test() {
		HashCodeComparator<Long> comparator = new HashCodeComparator<Long>();

		assertTrue(comparator.compare(null, null) == 0);
		assertTrue(comparator.compare(null, Long.valueOf(1)) < 0);
		assertTrue(comparator.compare(Long.valueOf(1), null) > 0);
		assertTrue(comparator.compare(Long.valueOf(1), Long.valueOf(1)) == 0);
		assertTrue(comparator.compare(Long.valueOf(1), Long.valueOf(2)) < 0);
		assertTrue(comparator.compare(Long.valueOf(2), Long.valueOf(1)) > 0);
	}

}
