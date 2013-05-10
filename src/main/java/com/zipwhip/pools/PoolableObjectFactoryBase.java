package com.zipwhip.pools;

import com.zipwhip.lifecycle.Destroyable;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 10/28/11
 * Time: 12:00 AM
 *
 */
public abstract class PoolableObjectFactoryBase implements PoolableObjectFactory {

    @Override
    public void destroyObject(Object obj) throws Exception {
        if (obj instanceof Destroyable){
            ((Destroyable) obj).destroy();
        }
    }

    @Override
    public boolean validateObject(Object obj) {
        if (obj instanceof Destroyable){
            if (((Destroyable) obj).isDestroyed()){
                return false;
            }
        }
        return true;
    }

    @Override
    public void activateObject(Object obj) throws Exception {

    }

    @Override
    public void passivateObject(Object obj) throws Exception {

    }

}
