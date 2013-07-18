package com.zipwhip.pools;

import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * Date: 6/26/13
 * Time: 11:25 AM
 *
 * @author Michael
 * @version 1
 */
public class DefaultPoolableObjectFactory extends BasePoolableObjectFactory {

    private final Class clazz;

    public DefaultPoolableObjectFactory(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object makeObject() throws Exception {
        return clazz.newInstance();
    }
}
