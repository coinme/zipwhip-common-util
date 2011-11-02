package com.zipwhip.lifecycle;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/16/10
 * Time: 8:11 PM
 * <p/>
 * Indicates that an object can be actively destroyed.
 */
public interface Destroyable {

    /**
     * Destroy this item (and anything that it is linked to).
     */
    void destroy();

    /**
     * See if it's already been destroyed (since we can't control garbage collection).
     *
     * @return true if this has been destroyed, otherwise false.
     */
    boolean isDestroyed();

}
