package com.zipwhip.concurrency;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 1/29/11
 * Time: 12:18 PM
 */
public interface Callback<TSender, TValue> {

    void onComplete(TSender sender, TValue value);

}
