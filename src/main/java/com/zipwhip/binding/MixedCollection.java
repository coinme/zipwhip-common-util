package com.zipwhip.binding;

import com.zipwhip.events.ObservableHelper;
import com.zipwhip.events.OrderedDataEventObject;
import com.zipwhip.util.KeyValuePair;
import com.zipwhip.util.OrderedMapHelper;

import java.util.Comparator;
import java.util.EventObject;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 9:19 PM
 */
public class MixedCollection<K, V> implements Filterable<KeyValuePair<K, V>> {

    /**
     * An item has been added
     */
    private ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>> onAdd = new ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>>();
    /**
     * An item has been removed
     */
    private ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>> onRemove = new ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>>();
    /**
     * The collection has been loaded with a new set of data. Update your UI
     */
    private ObservableHelper<EventObject> onLoad = new ObservableHelper<EventObject>();

    /**
     * This is where we store our actual data. This is not filtered.
     */
    protected OrderedMapHelper<K, V> data;

    /**
     * If we have an active filter on the data, this is where it will appear.
     */
    protected OrderedMapHelper<K, V> query;

    /**
     * This is the active filter.
     */
    protected Filter<KeyValuePair<K, V>> filter;
    protected Comparator<KeyValuePair<K, V>> sorter;
    // caching
    private EventObject eventObject;

    public MixedCollection(Map<K, V> data) {
        this.data = new OrderedMapHelper<K, V>(data);
    }

    protected MixedCollection(OrderedMapHelper<K, V> data) {
        this.data = new OrderedMapHelper<K, V>(data);
    }

    public MixedCollection() {
        this((Map<K, V>) null);
    }

    public int add(K key, V value) {
        KeyValuePair<K, V> pair = silentPut(key, value);

        int index = getData().indexOf(key);

        if (this.onAdd != null) {
            this.onAdd.notifyObservers(this, new OrderedDataEventObject<KeyValuePair<K, V>>(this, pair, index));
        }

        return index;
    }

    public V getAt(int index) {
        return getData().getAt(index);
    }

    public V get(K key) {
        return getData().get(key);
    }

    /**
     * Store the data but dont throw an event. Also tell me if it went into the query map.
     *
     * @param key
     * @param value
     * @return if the value went into the query map or not.
     */
    protected KeyValuePair<K, V> silentPut(K key, V value) {
        return silentPut(new KeyValuePair<K, V>(key, value));
    }

    protected KeyValuePair<K, V> silentPut(KeyValuePair<K, V> pair) {
        data.put(pair);

        if (query != null) {
            // we have an active query
            try {
                if (filter.call(pair)) {
                    // it's been filtered out.
                    // do nothing
                } else {
                    // it's been included in the data.
                    query.put(pair);
                }
            } catch (Exception e) {
                // we weren't sure what to do.
                // let's just not do anything?
            }
        }

        return pair;
    }


    /**
     * Query this data structure for a subset of the data. If it's filter.filtered() then
     *
     * @param filter
     * @return
     */
    public MixedCollection<K, V> query(Filter<KeyValuePair<K, V>> filter) {
        OrderedMapHelper<K, V> data = this.data;
        if (query != null) {
            data = query;
        }

        return new MixedCollection<K, V>(applyFilter(data, filter));
    }

    public synchronized void sort(Comparator<KeyValuePair<K, V>> sorter) {
        this.sorter = sorter;

        // we need to sort the current data structure.
        if (isFiltered()) {
            // there's an active query!
            this.query.sort(sorter);
        } else {
            this.data.sort(sorter);
        }

        if (this.onLoad != null) {
            this.onLoad.notifyObservers(this, null);
        }
    }

    public synchronized void setFilter(Filter<KeyValuePair<K, V>> filter) {
        if (query != null) {
            // we already have an active query, we need to nuke it.
            // NOTE: is this ok? should we instead instantiate a new one due to someone else using it?
            // I think it's better to just drop the instance of the map entirely.
            // let the garbage collector handle it because "clearing" the map has no real value.
            // we're just abandoning those internal nodes anyway.
        }

        // we need to keep track of it for adds later.
        this.filter = filter;
        this.query = this.applyFilter(this.data, filter);

        // indicate that we've loaded from scratch and the full UI must be redrawn.
        if (this.onLoad != null) {
            this.onLoad.notifyObservers(this, null);
        }
    }

    protected OrderedMapHelper<K, V> applyFilter(OrderedMapHelper<K, V> mapToReadFrom, Filter<KeyValuePair<K, V>> filter) {
        OrderedMapHelper<K, V> result = new OrderedMapHelper<K, V>();

        for (KeyValuePair<K, V> kvp : mapToReadFrom.getKeys()) {
            try {
                if (filter.call(kvp)) {
                    result.put(kvp);
                }
            } catch (Exception e) {
                // we don't know what to do?
                // just ignore?
            }
        }

        return result;
    }

    public boolean isFiltered() {
        return query != null;
    }

    public synchronized void clearFilter() {
        // wipe references to the query/filter, let it be garbage collected
        this.query = null;
        this.filter = null;
    }

    /**
     * This could be precalculated to be more efficient, but we're on a desktop environment.
     *
     * @return
     */
    private OrderedMapHelper<K, V> getData() {
        if (isFiltered()) {
            return query;
        }

        return data;
    }

    /**
     * Same as indexOf, but for the value this time.
     *
     * @param record
     * @return
     */
    public int indexOfValue(V record) {
        return getData().findValue(record);
    }

    public void remove(K key) {

        if (query != null) {
            if (onRemove != null) {
                int index = query.indexOf(key);
                KeyValuePair<K, V> pair = query.getPair(index);

                // kill it
                query.remove(key);
                data.remove(key);

                // announce
                onRemove.notifyObservers(this, new OrderedDataEventObject<KeyValuePair<K, V>>(this, pair, index));
            } else {
                // no one cares about the event, just do it quietly.
                query.remove(key);
                data.remove(key);
            }
        } else {
            if (onRemove != null) {
                int index = data.indexOf(key);
                KeyValuePair<K, V> pair = data.getPair(index);

                // kill it.
                data.remove(key);

                // announce
                onRemove.notifyObservers(this, new OrderedDataEventObject<KeyValuePair<K, V>>(this, pair, index));
            } else {
                // no one cares
                data.remove(key);
            }
        }

    }

    public boolean containsKey(K key) {
        return getData().contains(key);
    }

    public void putAll(MixedCollection<K, V> changes) {
        OrderedMapHelper<K, V> data = changes.getData();

        for (KeyValuePair<K, V> pair : data.getKeys()) {
            silentPut(pair);
        }

        // a big huge change (reload your entire UI)
        if (this.onLoad != null) {
            this.onLoad.notifyObservers(this, null);
        }
    }

    /**
     * for a given item, where does it stand in the list.
     *
     * @param key
     * @return
     */
    public int indexOfKey(K key) {
        return getData().indexOf(key);
    }

    public ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>> onAdd() {
        return onAdd;
    }

    public ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>> onRemove() {
        return onRemove;
    }


    public int size() {
        return getData().size();
    }

    public ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>> getOnAdd() {
        return onAdd;
    }

    public ObservableHelper<OrderedDataEventObject<KeyValuePair<K, V>>> getOnRemove() {
        return onRemove;
    }

    public ObservableHelper<EventObject> getOnLoad() {
        return onLoad;
    }

    public void clear() {
        this.data.clear();
        if (this.query != null){
            this.query.clear();
        }

        if (this.onLoad != null){
            this.onLoad.notifyObservers(this, getEventObject());
        }
    }

    private EventObject getEventObject() {
        if (eventObject == null){
            eventObject = new EventObject(this);
        }

        return eventObject;
    }
}
