package com.zipwhip.binding;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 9:48 PM
 *
 * Something that is filterable
 */
public interface Filterable<T> {

    void setFilter(Filter<T> filter);

    boolean isFiltered();

    void clearFilter();

}
