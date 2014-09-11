package com.zipwhip.util;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: Nov 10, 2010
 * Time: 10:59:49 PM
 * <p/>
 * Some helpful things with collections
 */
public class CollectionUtil {

    public static void deepJoinArrays(Map<String, Object> result, Map<String, Object> addition) {

        for (String key : addition.keySet()) {
            Object secondItem = addition.get(key);

            if (result.containsKey(key)) {
                Object item = result.get(key);
                if (item instanceof List) {
                    // we have a collision
                    List<Object> existingList = (List<Object>) item;

                    if (secondItem instanceof List) {
                        for (Object value : (List) secondItem) {
                            existingList.add(value);
                        }
                    } else {
                        throw new RuntimeException("I cant deal with this scenario");
                    }
                } else {
                    throw new RuntimeException("I cant deal with this scenario");
                }

            } else {
                // we dont have a collision
                result.put(key, secondItem);
            }

        }
    }

    /**
     * If the passed in object is an array, returns length.
     * If the passed in object is a collection, returns size.
     * If the passed in object is null, returns 0.
     * If the passed in object is not an array or collection, returns 1.
     * If the passed in object is a map, returns size.
     *
     * @param object
     * @return
     */
    public static int size(final Object object) {
        if (null == object) {
            return 0;
        } else if (isArray(object)) {
            return size(object);
        } else if (isCollection(object)) {
            return size((Collection) object);
        } else if (isMap(object)) {
            return size((Map) object);
        } else {
            return 1;
        }
    }

    public static <T> int size(T[] object) {
        if (null == object) {
            return 0;
        }

        return Array.getLength(object);
    }

    /**
     * If the collection is null, returns 0
     * Returns the size of the collection.
     *
     * @param collection The collection to test the size of.
     * @return The size of the collection or 0 if c is null
     */
    public static <T> int size(Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <K, V> int size(Map<K, V> map) {
        return map == null ? 0 : map.size();
    }

    public static <T> boolean isArray(Object source) {
        if (source instanceof Object[]) {
            return true;
        } else if (source == null) {
            return false;
        }

        return source.getClass().isArray();
    }

    public static boolean isMap(final Object object) {
        return object instanceof Map;
    }

    public static boolean isCollection(final Object object) {
        return object instanceof Collection;
    }


    /**
     * If the map contains values that are of type List, Collection, Map, or Object[], will attempt to dive one level
     * deeper.
     * <p/>
     * If strict: if those underlying types have more than 1 result, returns null!
     * If non-strict: if those underlying types have more than 1 result, return the first result.
     *
     * @param params
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> String getString(final Map<K, V> params, final K key, boolean strict) {
        final V object = getParam(params, key);

        if (null == object) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else if (isArray(object)) {
            final Object[] array = (Object[]) object;
            final Object result = strict ? get(array) : first(array);

            return StringUtil.toString(result);
        } else if (isCollection(object)) {
            final Collection collection = (Collection) object;
            final Object result = strict ? get(collection) : first(collection);

            return StringUtil.toString(result);
        } else if (isMap(object)) {
            final Map map = (Map) object;
            final Object result = strict ? get(map) : first(map);

            return StringUtil.toString(result);
        }

        return StringUtil.toString(object);
    }

    /**
     * If the map contains values that are of type List, Collection, Map, or Object[], will attempt to dive one level
     * deeper.
     * <p/>
     * This is the non-strict version, if those underlying types have more than 1 result, returns the first one!
     *
     * @param params
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> String getString(final Map<K, V> params, final K key) {
        return getString(params, key, false);
    }

    public static <K, V> String getString(Map<K, V> params, K... keys) {
        if (isNullOrEmpty(keys) || isNullOrEmpty(params)) {
            return null;
        }

        for (final K key : keys) {
            final String result = getString(params, key);

            if (!StringUtil.isNullOrEmpty(result)) {
                return result;
            }
        }

        return null;
    }

    public static <K, V> String getString(final Map<K, V> params, final Collection<K> keys) {
        if (isNullOrEmpty(keys) || isNullOrEmpty(params)) {
            return null;
        }

        for (final K key : keys) {
            final String result = getString(params, key);

            if (!StringUtil.isNullOrEmpty(result)) {
                return result;
            }
        }

        return null;
    }

    public static Number getNumber(Map params, String key) {
        Object param = getParam(params, key);

        if (param instanceof Number) {
            return ((Number) param);
        }
        if (param instanceof String) {
            NumberFormat defForm = NumberFormat.getInstance();
            try {
                if (!StringUtil.isNullOrEmpty(param.toString())) { //empty string causes known exception
                    return defForm.parse((String) param);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        return null;
    }

    public static boolean getBoolean(Map params, String key, boolean defaultValue) {
        Boolean param = getBoolean(params, key);

        if (param == null) {
            return defaultValue;
        }

        return param;
    }

    public static Boolean getBoolean(Map params, String key) {
        Object param = getParam(params, key);

        if (param == null) {
            return null;
        }
        if (param instanceof Boolean) {
            return (Boolean) param;
        } else if (param instanceof String) {
            return (Boolean.parseBoolean((String) param));
        }

        return null;
    }

    public static int getInteger(Map params, String key, int defaultValue) {
        Integer result = getInteger(params, key);

        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    public static Integer getInteger(Map params, String key) {
        Number param = getNumber(params, key);

        if (param == null) {
            return null;
        }

        int result = param.intValue();

        if (result == -1) {
            return null;
        }

        return result;
    }

    public static long getLong(Map params, String key, long defaultValue) {
        Long result = getLong(params, key);

        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    public static Long getLong(Map params, String key) {
        Number param = getNumber(params, key);

        if (param == null) {
            return null;
        }

        long result = param.longValue();

        if (result == -1L) {
            return null;
        }

        return result;
    }

    public static Date getDate(Map params, String key) {
        Object object = getParam(params, key);

        if (object instanceof Number) {
            Long param = ((Number) object).longValue();

            if (param == -1L) {
                return null;
            }

            return new Date(param);
        } else if (object instanceof Date) {
            return (Date) object;
        }

        return null;
    }

    public static Date getDate(Map params, String key, Date defaultDate) {
        Date date = getDate(params, key);

        if (date == null) {
            return defaultDate;
        }

        return date;
    }
//
//    public static String getString(Map params, String key) {
//        Object param = getParam(params, key);
//
//        if (param == null) {
//            return null;
//        }
//
//        if (param instanceof String) {
//            return (String) param;
//        }
//
//        //        if (param instanceof org.codehaus.groovy.grails.web.json.JSONObject){
//        //        }
//
////        if (param instanceof Collection) {
////            Collection collection = ((Collection) param);
////
////            return delimitedParser.deliminate(collection);
////        }
//
//        return String.valueOf(param);
//        //        return null;
//    }

    public static List<String> getList(Map params, String key, List<String> defaultValue) {
        List<String> result = getList(params, key);
        if ((result == null) || result.isEmpty()) {
            return defaultValue;
        }
        return result;
    }

    public static List<String> getList(Map params, String... keys) {
        for (String key : keys) {
            List<String> result = getList(params, key);
            if (!isNullOrEmpty(result)) {
                return result;
            }
        }
        return null;
    }

    public static List<String> getList(Map params, String key) {
        Object param = getParam(params, key);

        if (param == null) {
            return null;
        }

        if (param instanceof Collection) {
            Collection collection = (Collection) param;
            List<String> result = new Vector<String>();
            for (Object part : collection) {
                result.add(String.valueOf(part));
            }
            return result;
        }

        List<String> result = new ArrayList<String>();
        if (param.getClass().isArray()) {
            List items = Arrays.asList(toObjectArray(param));

            for (Object item : items) {
                result.add(String.valueOf(item));
            }
        } else {
            result.add(String.valueOf(param));
        }

        return result;
    }

    public static <K, V> V getParam(Map<K, V> params, K key, V defaultValue) {
        V result = getParam(params, key);

        if (null == result) {
            return defaultValue;
        }

        return result;
    }

    public static <K, V> V getParam(Map<K, V> params, K key) {
        return get(params, key);
    }

    public static Map getMap(Map params, String key) {
        Object result = getParam(params, key);
        if (result instanceof Map) {
            return (Map) result;
        }
        return null;
    }

    public static class DiffResult<T> {
        public List<T> current;
        public Collection<T> previous;

        public List<T> additions;
        public List<T> subtractions;

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            sb.append("current: ").append(current).append("\n");
            sb.append("previous: ").append(previous).append("\n");
            sb.append("additions: ").append(additions).append("\n");
            sb.append("subtractions: ").append(subtractions).append("\n");


            return sb.toString();
        }

    }

    public static <TColl extends Collection<TItem>, TItem> TColl add(Class<TColl> clazz, TColl list, TItem item) {
        if (list == null) {
            try {
                list = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        list.add(item);

        return list;
    }

    public static <T> Collection<T> add(Collection<T> list, T item) {
        if (list == null) {
            list = new ArrayList<T>();
        }

        list.add(item);

        return list;
    }

    /**
     * Will not create a new list.
     *
     * @param <T>
     * @param result
     * @param other
     * @return
     */
    public static <T, TV extends Collection<T>> TV addAllEfficient(TV result, TV other) {
        if (isNullOrEmpty(other)) {
            return result;
        }
        if (isNullOrEmpty(result)) {
            return other;
        }

        result.addAll(other);

        return result;
    }

    public static <T> List<T> remove(List<T> callbackList, T item) {
        if (CollectionUtil.isNullOrEmpty(callbackList)) {
            return callbackList;
        }
        callbackList.remove(item);
        return callbackList;
    }

    /**
     * Unwrap an item from the collection only if it has 1 and only 1 item in it.
     * <p/>
     * If null or empty, returns null
     * If it has more than 1 item in it, returns null
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> T get(Collection<T> collection) {
        if (size(collection) > 1) {
            return null;
        }

        return get(collection, 0);
    }

    /**
     * If this map is null, returns null.
     * If this map has 1 and only 1 item in it, returns that item.
     * If this map has more than 1 item in it, returns null.
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V get(Map<K, V> map) {
        if (isNullOrEmpty(map)) {
            return null;
        }

        final int size = size(map);

        // This method refuses to work on maps that have more than 1 item in it.
        if (size > 1) {
            return null;
        }

        return get(map, 0);
    }

    /**
     * If this map is null, returns null.
     * If this map has more than index item in it, returns null.
     * <p/>
     * While iterating through the map, will NOT synchronize on the map. The caller should be aware of this.
     *
     * @param map
     * @param <K>
     * @param <V>
     * @param index
     * @return
     */
    public static <K, V> V get(final Map<K, V> map, final int index) {
        if (isNullOrEmpty(map)) {
            return null;
        } else if (index < 0) {
            return null;
        }

        final int size = map.size();

        if (size <= index) {
            return null;
        }

        int i = 0;
        for (K key : map.keySet()) {
            if (index == i) {
                return map.get(key);
            }

            i++;
        }

        throw new IllegalStateException(String.format("This can never happen. size:%s, index:%s", size, index));
    }

    /**
     * If the collection is null, returns null
     * If the collection is a list, returns the result within the O(n) notation of that list implementation.
     * If the index is less than 0, returns null in constant time
     * If the index is larger than the size, returns null in constant time
     * <p/>
     * If the index is within bounds, will return the right item in slow O(index) time by iterating through the collection.
     *
     * @param collection
     * @param index
     * @param <T>
     * @return
     */
    public static <T> T get(final Collection<T> collection, final int index) {
        if (collection instanceof List) {
            return get((List<T>) collection, index);
        } else if (index < 0) {
            // Negative positions do not exist.
            return null;
        }

        final int size = size(collection);

        if (size <= index) {
            return null;
        }

        int i = 0;
        for (T t : collection) {
            if (index == i) {
                return t;
            }

            i++;
        }

        throw new IllegalStateException(String.format("This can never happen. size:%s, index:%s", size, index));
    }

    public static <T> T get(T[] objects) {
        final int size = size(objects);

        if (size <= 0 || size > 1) {
            return null;
        }

        return get(objects, 0);
    }

    public static <T> T get(T[] objects, int index) {
        if (index >= size(objects)) {
            return null;
        }

        return (T) Array.get(objects, index);
    }

    /**
     * This method does not safely synchronize around the internal iterations. The caller must be aware of threading.
     *
     * @param original
     * @param current
     * @param <T>
     * @return
     */
    public static <T> DiffResult<T> diff(Collection<T> original, List<T> current) {
        DiffResult<T> result = new DiffResult<T>();
        result.previous = original;
        result.current = current;

        if (isNullOrEmpty(original)) {
            result.additions = current;
            result.subtractions = null;
            return result;
        }
        if (isNullOrEmpty(current)) {
            result.additions = null;
            result.subtractions = current;
            return result;
        }

        Map<T, Boolean> map = new HashMap<T, Boolean>();

        for (T t : original) {
            map.put(t, false);
        }

        for (T t : current) {
            if (!map.containsKey(t)) {
                // addition
                result.additions = (List) add(result.additions, t);
            } else {
                // it already exists
                map.put(t, true);
            }
        }

        for (Map.Entry<T, Boolean> entry : map.entrySet()) {
            if (entry.getValue().equals(false)) {
                // it was never updated
                result.subtractions = (List) add(result.subtractions, entry.getKey());
            }
        }

        return result;
    }


    public static boolean containsAny(Map params, String... param) {
        return containsAny(params, Arrays.asList(param));
    }

    public static boolean containsAll(Map params, String... param) {
        return containsAll(params, Arrays.asList(param));
    }

    public static boolean containsAny(Map params, Collection<String> param) {
        if (!CollectionUtil.isNullOrEmpty(params)) {
            for (String string : param) {
                Object object = getParam(params, string);
                if (object != null) { // because params.containsKey() on a Json map is completely broken.
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsAll(Map params, Collection<String> param) {
        if (!CollectionUtil.isNullOrEmpty(params)) {
            for (String string : param) {
                Object object = getParam(params, string);
                if (object != null) { // because params.containsKey() on a Json map is completely broken.
                    return true;
                }
            }
        }
        return true;
    }

    public static boolean containsAny(Map params, String param) {
        if (!CollectionUtil.isNullOrEmpty(params)) {
            Object object = getParam(params, param);
            if (object != null) { // because params.containsKey() on a Json map is completely broken.
                return true;
            }
        }
        return false;
    }

    /**
     * Null safe wrapper around Arrays.asList...
     *
     * @param items
     * @param <T>
     * @return
     */
    public static <T> List<T> asList(T... items) {
        if (isNullOrEmpty(items)) {
            return null;
        }
        return Arrays.asList(items);
    }

    public static <T> boolean isNullOrEmpty(Collection<T> collection) {
        return ((collection == null) || collection.isEmpty());
    }

    public static boolean isNullOrEmpty(Map map) {
        return ((map == null) || map.isEmpty());
    }

    public static <T> boolean exists(Collection<T> collection) {
        return !isNullOrEmpty(collection);
    }

    public static boolean exists(Map map) {
        return !isNullOrEmpty(map);
    }

    public static <K, V> V first(Map<K, V> map) {
        return get(map, 0);
    }

    public static <T> T first(T[] array) {
        return get(array, 0);
    }

    public static <T> T first(Collection<T> collection) {
        return get(collection, 0);
    }

    public static <T> T first(List<T> collection) {
        return get(collection, 0);
    }

    public static <T> T last(List<T> collection) {
        if (isNullOrEmpty(collection)) {
            return null;
        }
        return get(collection, collection.size() - 1);
    }

    public static <K, V> V get(final Map<K, V> map, final K key) {
        if (map == null) {
            return null;
        }

        try {
            return map.get(key);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public static <T> T find(List<T> collection, T needle) {
        if (isNullOrEmpty(collection)) {
            return null;
        }

        if (needle == null) {
            return null;
        }

        if (collection.size() == 0) {
            return null;
        }

        for (T item : collection) {
            if (item.equals(needle)) {
                return item;
            }
        }

        return null;
    }

    public static <T> T get(final List<T> collection, final int index) {
        if (isNullOrEmpty(collection)) {
            return null;
        } else if (index < 0) {
            return null;
        } else if (collection.size() <= index) {
            return null;
        }

        return collection.get(index);
    }

    public static <T> boolean isNullOrEmpty(T[] parts) {
        if ((parts == null) || (parts.length <= 0)) {
            return true;
        }
        return false;
    }

    public static <K, V> Map<K, V> add(Map<K, V> result, K key, V value) {
        if (result == null) {
            result = new HashMap<K, V>();
        }

        result.put(key, value);

        return result;
    }

    public static <T> List<T> addAll(List<T> result, List<T> toAdd) {
        if (result == null) {
            if (toAdd == null) {
                return null;
            }

            return toAdd;
        }

        if (toAdd == null) {
            return result;
        }

        result.addAll(toAdd);

        return result;

    }

    public static boolean isNullOrEmpty(byte[] sourceImageData) {
        return ((sourceImageData == null) || (sourceImageData.length == 0));
    }

    public static <T> void remove(Collection<T> items, T item) {
        if (isNullOrEmpty(items)) {
            return;
        }
        items.remove(item);
    }

    public static <TKey, TValue> Map<TKey, TValue> asMap(TKey key, TValue value) {
        Map<TKey, TValue> result = new HashMap<TKey, TValue>();

        result.put(key, value);

        return result;
    }

    /**
     * Create a Set from a lst of items of the same type, T.
     * The implementation is {@code HashSet} so iterating will become slow as you add to it.
     *
     * @param items Zero or more items of type T from which to create the Set
     * @param <T>   The type of Set to produce.
     * @return A Set of type T containing the arguments.
     */
    public static <T> Set<T> asSet(T... items) {

        Set<T> set = new HashSet<T>();
        set.addAll(Arrays.asList(items));

        return set;
    }

    public static List<String> subList(List<String> arguments, int index) {
        if (index == 0) {
            return arguments;
        }
        if (isNullOrEmpty(arguments)) {
            return null;
        }
        int max = arguments.size() - 1;
        if (index >= max) {
            return null;
        }
        return arguments.subList(index, max);
    }

    public static Set<String> getSet(Map params, String key) {
        Object param = getParam(params, key);

        if (param == null) {
            return null;
        }

        if (param instanceof Collection) {
            Collection collection = (Collection) param;
            Set<String> result = new HashSet<String>();
            for (Object part : collection) {
                result.add(String.valueOf(part));
            }
            return result;
        }

        Set<String> result = new HashSet<String>();
        if (param.getClass().isArray()) {
            List items = Arrays.asList(toObjectArray(param));
            for (Object item : items) {
                result.add(String.valueOf(item));
            }
        } else {
            result.add(String.valueOf(param));
        }

        return result;
    }

    /**
     * Convert the given array (which may be a primitive array) to an
     * object array (if necessary of primitive wrapper objects).
     * <p>A <code>null</code> source value will be converted to an
     * empty Object array.
     *
     * @param source the (potentially primitive) array
     * @return the corresponding object array (never <code>null</code>)
     * @throws IllegalArgumentException if the parameter is not an array
     */
    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }
}