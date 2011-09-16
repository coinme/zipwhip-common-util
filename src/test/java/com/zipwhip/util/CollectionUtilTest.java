package com.zipwhip.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void testIsNullOrEmpty() throws Exception {

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
