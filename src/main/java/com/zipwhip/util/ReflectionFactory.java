package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/8/11
 * Time: 4:52 PM
 *
 * Uses refletion to create the object.
 */
public class ReflectionFactory<T> implements Factory<T> {

    Class<T> clazz;

    public ReflectionFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T create() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
