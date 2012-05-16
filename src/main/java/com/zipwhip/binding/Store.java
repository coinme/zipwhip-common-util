package com.zipwhip.binding;

import com.zipwhip.events.OrderedDataEventObject;
import com.zipwhip.events.Observable;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 4:13 PM
 *
 * A store contains records. It also loads them and manages them.
 */
public interface Store<T extends Record> extends Filterable<T> {

    /**
     * Remove the record from the store.
     * @param id The id of the record to remove.
     */
    void remove(Long id);

    /**
     * Add a record to the store.
     *
     * @param record The record to be added.
     */
    void add(T record);

    /**
     * Get a record by its recordId.
     *
     * @param id The id of the record to query.
     * @return The record corresponding to the id, or null if the record does not exist.
     */
    T get(Long id);

    /**
     * How many records do you have?
     *
     * @return The number of records in the store.
     */
    int size();

    /**
     * Give the record at the given index
     *
     * @param index The index of the record to query.
     * @return The record corresponding to the index, or null if the index does not exist.
     */
    T getAt(int index);

    /**
     * For a given record, where is it?
     *
     * @param id the id of the record to be checked
     * @return the index of the record or -1 if not found.
     */
    int indexOf(Long id);

    /**
     * More efficient way to determine if a record is present (rather than checking indexOf's result, for example)
     *
     * @param id the id of the record to be checked
     * @return true if there is a record with the id, otherwise false
     */
    boolean contains(Long id);

    // events

    Observable<OrderedDataEventObject<T>> onChange();

    Observable<OrderedDataEventObject<T>> onAdd();

    Observable<OrderedDataEventObject<T>> onRemove();

    Observable<EventObject> onLoad();

}
