package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 5/5/11
 * Time: 5:19 PM
 */
public interface ObjectCallback {

    void handleMessage();

    void handleMessage(Object object);

    void handleMessage(Object[] object);

}
