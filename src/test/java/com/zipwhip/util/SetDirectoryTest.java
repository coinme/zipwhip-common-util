package com.zipwhip.util;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 9/26/11
 * Time: 10:58 AM
 */
public class SetDirectoryTest {

    LocalDirectory<String, String> directory;

    @Before
    public void setUp() throws Exception {
        directory = new SetDirectory<String, String>();
    }

    @Test
    public void testAddGet() throws Exception {

        Collection<String> results = directory.get("nothing");
        Assert.assertNull(results);

        directory.add("one", "a");
        directory.add("one", "b");
        directory.add("one", "c");
        directory.add("one", "c");

        directory.add("two", "1");
        directory.add("two", "2");
        directory.add("two", "3");
        directory.add("two", "3");

        results = directory.get("one");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 3);

        results = directory.get("two");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 3);
    }

    @Test
    public void testRemove() throws Exception {

        directory.add("one", "a");
        directory.add("one", "b");

        Collection<String> results = directory.get("one");
        Assert.assertNotNull(results);

        directory.remove("one", "b");

        results = directory.get("one");
        Assert.assertNotNull(results);

        directory.remove("one", "a");
        results = directory.get("one");

        Assert.assertTrue(CollectionUtil.isNullOrEmpty(results));
    }

    @Test
    public void testIsEmpty() throws Exception {
        Assert.assertTrue(directory.isEmpty());
        directory.add("key", "value");
        Assert.assertFalse(directory.isEmpty());
    }

    @Test
    public void testClear() throws Exception {
        Assert.assertTrue(directory.isEmpty());
        directory.add("key", "value");
        Assert.assertFalse(directory.isEmpty());
        directory.clear();
        Assert.assertTrue(directory.isEmpty());
    }

    @Test
    public void testKeySet() throws Exception {

        directory.add("one", "a");
        directory.add("one", "b");
        directory.add("one", "c");

        directory.add("two", "1");
        directory.add("two", "2");
        directory.add("two", "3");

        Set<String> keys = directory.keySet();
        Assert.assertNotNull(keys);

        Assert.assertTrue(keys.contains("one"));
        Assert.assertTrue(keys.contains("two"));
        Assert.assertTrue(keys.size() == 2);

        directory.remove("two", "1");
        directory.remove("two", "2");
        directory.remove("two", "3");

        keys = directory.keySet();
        Assert.assertNotNull(keys);

        Assert.assertTrue(keys.contains("one"));
        Assert.assertFalse(keys.contains("two"));
        Assert.assertTrue(keys.size() == 1);
    }

}
