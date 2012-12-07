package com.zipwhip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 2/8/11
 * Time: 4:52 PM
 *
 * Uses reflection to create the object.
 */
public class ReflectionFactory<T> implements Factory<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionFactory.class);

    Class<T> clazz;

    public ReflectionFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T create() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("Exception instantiating class: " + e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException instantiating class: " + e);
        }
        return null;
    }
}
