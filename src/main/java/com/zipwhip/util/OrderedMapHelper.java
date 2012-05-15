package com.zipwhip.util;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 11:48 PM
 *
 * I need to provide ordering to keys
 */
public class OrderedMapHelper<K,V> {

    private static final int NOT_FOUND = -1;

    private Map<K, V> keyToData;
    private Map<V, K> dataToKey;
    private Map<K, Integer> keyToIndex = new TreeMap<K, Integer>();
    private List<KeyValuePair<K,V>> keys = new LinkedList<KeyValuePair<K,V>>();

    public OrderedMapHelper() {
        this((Map<K,V>)null);
    }

    public OrderedMapHelper(OrderedMapHelper<K, V> data) {
        this(data.getData());
    }

    public OrderedMapHelper(Map<K, V> data) {
        if (data == null){
            data = new TreeMap<K, V>();
        }

        this.keyToData = data;
        this.dataToKey = new HashMap<V, K>();

        for (K key : data.keySet()) {
            put(key, data.get(key));
        }
    }

    public void put(KeyValuePair<K,V> pair) {
        K key = pair.getKey();
        V value = pair.getValue();

        if (keyToData.containsKey(key)){
            // already exists!
            // find the index.
            removeKey(key);
        }

        // maybe overwrite value?
        keyToData.put(key, value);
        dataToKey.put(value, key);

        addKey(pair);
    }

    public V get(K key){
        return keyToData.get(key);
    }

    public V getAt(int index){
        if (keys == null || index < 0 || index >= keys.size()){
            return null; // null counts as not found right?
        }

        KeyValuePair<K,V> pair = keys.get(index);

        return keyToData.get(pair.getKey());
    }

    public int indexOf(K key) {
        Integer index = keyToIndex.get(key);

        if (index == null){
            return NOT_FOUND;
        }

        return index;
    }


    public KeyValuePair<K,V> put(K key, V value){
        if (keyToData.containsKey(key)){
            // already exists!
            // find the index.
            removeKey(key);
        }

        // maybe overwrite value?
        keyToData.put(key, value);
        dataToKey.put(value, key);

        return addKey(key, value);
    }

    private void removeKey(K key) {
        // NOTE: sorting on remove isn't important. things just fill in below.
        int idx = keyToIndex.get(key);

        keys.remove(idx);
    }

    private KeyValuePair<K,V> addKey(KeyValuePair<K,V> pair) {
        // todo: what do we do about sorting?
        keys.add(pair);

        // find the index of this addition.
        int index = keys.indexOf(pair);
        // cached autoboxing
        this.keyToIndex.put(pair.getKey(), Integer.valueOf(index));

        return pair;
    }

    private KeyValuePair<K,V> addKey(K key, V value) {
        KeyValuePair<K, V> pair = new KeyValuePair<K, V>(key, value);

        return addKey(pair);
    }

    public void sort(Comparator<KeyValuePair<K,V>> sorter){
        // sort the keys
        Collections.sort(this.keys, sorter);

        // but now they are out of order. let's remember where they are.
        this.keyToIndex.clear();
        int i = 0;
        for (KeyValuePair<K,V> key : keys) {
            this.keyToIndex.put(key.getKey(), i);
            i++;
        }

    }

    public Map<K, V> getData() {
        return keyToData;
    }

    public List<KeyValuePair<K,V>> getKeys() {
        return keys;
    }


    public int findValue(V record) {
        K key = this.dataToKey.get(record);

        return this.keyToIndex.get(key);
    }

    public KeyValuePair<K, V> getPair(int index) {
        return this.keys.get(index);
    }

    public void remove(K key) {
        int index = indexOf(key);
        V value = get(key);

        this.dataToKey.remove(value);
        this.keyToIndex.remove(key);
        this.keyToData.remove(key);
        this.keys.remove(index);
    }

    public boolean contains(K key) {
        return this.keyToData.containsKey(key);
    }

    public int size() {
        return this.keyToData.size();
    }

    public void clear() {
        this.dataToKey.clear();
        this.keyToIndex.clear();
        this.keyToData.clear();
        this.keys.clear();
    }
}
