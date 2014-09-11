package com.zipwhip.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 8/29/11
 * Time: 12:19 PM
 */
public class CollectionUtilTest {

    @Test
    public void testCollectionGetSingleItem() throws Exception {
        Object object = new Object();
        Collection<Object> collection = Arrays.asList(object);

        Object object2 = CollectionUtil.get(collection);

        assertSame(object, object2);
    }

    @Test
    public void testCollectionGetMultipleItems() throws Exception {
        Object object = new Object();
        Collection<Object> collection = Arrays.asList(object, object);

        Object object2 = CollectionUtil.get(collection);

        assertNull(object2);
    }

    @Test
    public void testSizeOfNull() throws Exception {
        assertEquals(0, CollectionUtil.size((Object) null));
        assertEquals(0, CollectionUtil.size((Object[]) null));
        assertEquals(0, CollectionUtil.size((Map) null));
        assertEquals(0, CollectionUtil.size((Collection) null));
    }

    @Test
    public void testSizeOfObject() throws Exception {
        assertEquals(1, CollectionUtil.size((Object) new Object()));
        assertEquals(1, CollectionUtil.size((Object) Arrays.asList(new Object())));
        assertEquals(1, CollectionUtil.size((Object) new TreeSet<Object>(Arrays.asList((Object)"test"))));
        assertEquals(1, CollectionUtil.size((Object) Collections.singletonMap(new Object(), new Object())));
        assertEquals(0, CollectionUtil.size((Object) null));
    }

    @Test
    public void testSizeOfRawTypes() throws Exception {
        assertEquals(1, CollectionUtil.size(new Object()));
        assertEquals(1, CollectionUtil.size(Arrays.asList(new Object())));
        assertEquals(1, CollectionUtil.size(new TreeSet<Object>(Arrays.asList((Object)"test"))));
        assertEquals(1, CollectionUtil.size(Collections.singletonMap(new Object(), new Object())));
        assertEquals(0, CollectionUtil.size((Object) null));
    }

    @Test
    public void testGetStringServletParameterMap() throws Exception {
        {
            final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

            parameterMap.put("test1", new String[]{"test1value"});
            parameterMap.put("test2", new String[]{null});
            parameterMap.put("test3", new String[]{"test3value1", "test3value2"});
            parameterMap.put("test4", null);

            assertEquals("test1value", CollectionUtil.getString(parameterMap, "test1"));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test2"));
            assertEquals("test3value1", CollectionUtil.getString(parameterMap, "test3"));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test5"));

            assertEquals("test1value", CollectionUtil.getString(parameterMap, "test1", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test2", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test3", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test4", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test5", true));
        }

        {
            final Map<String, Collection> parameterMap = new HashMap<String, Collection>();

            parameterMap.put("test1", Arrays.asList("test1value"));
            parameterMap.put("test2", Arrays.asList((String) null));
            parameterMap.put("test3", Arrays.asList("test3value1", "test3value2"));
            parameterMap.put("test4", null);

            assertEquals("test1value", CollectionUtil.getString(parameterMap, "test1"));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test2"));
            assertEquals("test3value1", CollectionUtil.getString(parameterMap, "test3"));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test5"));

            assertEquals("test1value", CollectionUtil.getString(parameterMap, "test1", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test2", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test3", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test4", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test5", true));
        }

        {
            final Map<String, Map> parameterMap = new HashMap<String, Map>();

            parameterMap.put("test1", Collections.singletonMap("b", "test1value"));
            parameterMap.put("test2", Collections.singletonMap("c", null));

            {
                Map map = new HashMap();

                map.put("b", "test3value1");
                map.put("a", "test3value2");

                parameterMap.put("test3", map);
            }

            parameterMap.put("test4", null);

            assertEquals("test1value", CollectionUtil.getString(parameterMap, "test1"));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test2"));
            assertEquals("test3value1", CollectionUtil.getString(parameterMap, "test3"));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test5"));

            assertEquals("test1value", CollectionUtil.getString(parameterMap, "test1", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test2", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test3", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test4", true));
            assertEquals(null, CollectionUtil.getString(parameterMap, "test5", true));
        }
    }

    @Test
    public void testGetStringNullInput() throws Exception {
        assertEquals(null, CollectionUtil.getString(null, (String) null));
        assertEquals(null, CollectionUtil.getString(null, "asdfg"));
    }


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
