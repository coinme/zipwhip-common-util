package com.zipwhip.util;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/1/11
 * Time: 11:00 PM
 * <p/>
 * Select the appropriate cache
 */
public interface SelectionStrategy<T> {

    void setOptions(List<T> list);

    List<T> getOptions();

    /**
     * Select the appropriate one.
     *
     * @return
     */
    T select();
}
