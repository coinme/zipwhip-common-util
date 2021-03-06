package com.zipwhip.pools;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.ObjectPoolFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.slf4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 10/27/11
 * Time: 10:35 PM
 * <p/>
 * This class is helpful for using pools
 */
public class PoolUtil {

    public static final GenericObjectPool.Config CONFIG = new GenericObjectPool.Config();

    static {
        CONFIG.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        CONFIG.maxActive = -1;
    }

    protected static final ObjectPoolFactory FACTORY = new GenericObjectPoolFactory(null, CONFIG);

    public static <T> T borrow(ObjectPool pool) {
        try {
            return (T) pool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Borrow an object from a pool. If it fails, it will reflectively create a new instance.
     *
     * Will log any errors.
     *
     * If reflection fails, will throw a runtime exception.
     *
     * @param LOGGER
     * @param clazz
     * @param pool
     * @param <T>
     * @throws RuntimeException if the reflection fails
     * @return
     */
    public static <T> T borrowSafely(Logger LOGGER, Class<T> clazz, ObjectPool pool) {
        try {
            return (T) pool.borrowObject();
        } catch (Exception e) {
            LOGGER.error("Failed to borrowObject!", e);

            try {
                // If this call fails, we'll have to throw.
                return clazz.newInstance();
            } catch (InstantiationException e1) {
                throw new RuntimeException(e1);
            } catch (IllegalAccessException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    public static void release(ObjectPool pool, Object item) {
        try {
            pool.returnObject(item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void releaseSafely(Logger LOGGER, ObjectPool pool, Object item) {
        try {
            pool.returnObject(item);
        } catch (Exception e) {
            LOGGER.error("Failed to returnObject", e);
        }
    }

    public static <T> ObjectPool getPool(PoolableObjectFactory factory) {
        ObjectPool pool = FACTORY.createPool();

        pool.setFactory(factory);

        return pool;
    }

    public static <T> ObjectPool getPool(final Class<T> clazz) {
        ObjectPool pool = FACTORY.createPool();

        pool.setFactory(new PoolableObjectFactory() {
            @Override
            public Object makeObject() throws Exception {
                return clazz.newInstance();
            }

            @Override
            public void destroyObject(Object obj) throws Exception {

            }

            @Override
            public boolean validateObject(Object obj) {
                return true;
            }

            @Override
            public void activateObject(Object obj) throws Exception {

            }

            @Override
            public void passivateObject(Object obj) throws Exception {

            }
        });

        return pool;
    }

//    public static void release(Class<?> clazz, Object o) {
//        try {
//            getPool(clazz, null).returnObject(o);
//        } catch (Exception e) {
//            // mask this exception because it dont matter
//            e.printStackTrace();
//        }
//    }
}
