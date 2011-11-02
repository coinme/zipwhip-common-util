package com.zipwhip.lifecycle;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/11
 * Time: 5:38 PM
 *
 * When this object is destroyed, cascade the destruction to the children objects.
 *
 */
public interface CascadingDestroyable extends Destroyable {

    /**
     * When you destroy this object, cascade the destruction it to the "destroyable" passed in.
     *
     * @param destroyable
     */
    void link(Destroyable destroyable);

    /**
     * Prevent the cascading destruction.
     *
     * @param destroyable
     */
    void unlink(Destroyable destroyable);

}
