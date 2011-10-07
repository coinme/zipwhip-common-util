package com.zipwhip.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 8/29/11
 * Time: 12:19 PM
 */
public class CollectionUtilTest {

    @Test
    public void testDeepJoinArrays() throws Exception {

    }

    @Test
    public void testGetString() throws Exception {

    }

    @Test
    public void testGetNumber() throws Exception {

    }

    @Test
    public void testGetBoolean() throws Exception {

    }

    @Test
    public void testGetInteger() throws Exception {

    }

    @Test
    public void testGetLong() throws Exception {

    }

    @Test
    public void testGetDate() throws Exception {

    }

    @Test
    public void testGetListFromStringArray() throws Exception {
        Map<String, String[]> m = new HashMap<String, String[]>();
        m.put("key", new String[]{"asdf", "fdsa"});
        List<String> lst = CollectionUtil.getList(m, "key");
        Assert.assertNotNull(lst);
        Assert.assertTrue(lst.size() == 2);
        Assert.assertEquals(lst.get(0), "asdf");
        Assert.assertEquals(lst.get(1), "fdsa");
    }

    @Test
    public void testGetParam() throws Exception {

    }

    @Test
    public void testGetMap() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

    }

    @Test
    public void testAddAllEfficient() throws Exception {

    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testDiff() throws Exception {

    }

    @Test
    public void testContainsAny() throws Exception {

    }

    @Test
    public void testContainsAll() throws Exception {

    }

    @Test
    public void testAsList() throws Exception {

    }

    @Test
    public void testSet() throws Exception {

        Set<String> set;

        set = CollectionUtil.asSet();
        Assert.assertNotNull(set);
        Assert.assertTrue(set.isEmpty());

        set = CollectionUtil.asSet("1", "2", "3", "3");
        Assert.assertNotNull(set);
        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.size() == 3);
    }

    @Test
    public void testIsNullOrEmpty() throws Exception {

        List<Object> list = new ArrayList<Object>();
        Map<Object, Object> map = new HashMap<Object, Object>();

        Assert.assertTrue(CollectionUtil.isNullOrEmpty(list));
        Assert.assertTrue(CollectionUtil.isNullOrEmpty(map));

        list.add("");
        map.put("" ,"");

        Assert.assertFalse(CollectionUtil.isNullOrEmpty(list));
        Assert.assertFalse(CollectionUtil.isNullOrEmpty(map));

        list = null;
        map = null;

        Assert.assertTrue(CollectionUtil.isNullOrEmpty(list));
        Assert.assertTrue(CollectionUtil.isNullOrEmpty(map));
    }

    @Test
    public void testExists() throws Exception {

        List<Object> list = new ArrayList<Object>();
        Map<Object, Object> map = new HashMap<Object, Object>();

        Assert.assertFalse(CollectionUtil.exists(list));
        Assert.assertFalse(CollectionUtil.exists(map));

        list.add("");
        map.put("" ,"");

        Assert.assertTrue(CollectionUtil.exists(list));
        Assert.assertTrue(CollectionUtil.exists(map));

        list = null;
        map = null;

        Assert.assertFalse(CollectionUtil.exists(list));
        Assert.assertFalse(CollectionUtil.exists(map));
    }

    @Test
    public void testFirst() throws Exception {

    }

    @Test
    public void testLast() throws Exception {

    }

    @Test
    public void testFind() throws Exception {

    }

    @Test
    public void testAsMap() throws Exception {

    }

    @Test
    public void testSubList() throws Exception {

    }

}
