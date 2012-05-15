package com.zipwhip.binding;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 9:20 PM
 *
 * For filtering a MixedCollection
 */
public interface Filter<T> {

    /**
     * Is a particular thing caught in the filter or not. Returns true if it's something this filter cares about.
     * Returns false if the filter ignores it.
     *
     * @param item
     * @return true to be filtered. false to be not filtered.
     * @throws Exception Throw an exception if you cannot make the determination.
     */
    boolean call(T item) throws Exception;

}
