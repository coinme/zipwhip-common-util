package com.zipwhip.binding;

import com.zipwhip.concurrent.ObservableFuture;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:06 AM
 *
 * Gets the data from somewhere
 */
public interface DataProxy<T> {

    /**
     * Load the data from some external place (disk, network, email, SMPP, SIP, memory, etc)
     *
     * @return A ObservableFuture that returns the type T once it completes.
     * @throws Exception
     */
    ObservableFuture<T> load() throws Exception;

}
