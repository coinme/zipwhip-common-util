package com.zipwhip.lifecycle;

import com.zipwhip.util.HashCodeComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/11
 * Time: 5:38 PM
 * <p/>
 * A base implementation
 */
public abstract class CascadingDestroyableBase extends DestroyableBase implements CascadingDestroyable {

    private static final Comparator<Destroyable> COMPARATOR = HashCodeComparator.getInstance();

    protected volatile Collection<Destroyable> destroyables = null;

    public void link(Destroyable destroyable) {
        if (destroyable == null) {
            return;
        }

        if (destroyables == null) {
            synchronized (this) {
                if (destroyables == null) {
                    // need a set because add needs to be idempotent.
                    // need a treeset because remove operations need to be fast.
                    destroyables = Collections.synchronizedSet(new TreeSet<Destroyable>(COMPARATOR));
                }
            }
        }

        destroyables.add(destroyable);
    }

    public void unlink(Destroyable destroyable) {
        if (destroyable == null || destroyables == null) {
            return;
        }

        destroyables.remove(destroyable);
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }

        if (destroyables != null) {
            synchronized (this) {
                synchronized (destroyables) {
                    for (Destroyable destroyable : destroyables) {
                        destroyable.destroy();
                    }
                    destroyables.clear();
                    destroyables = null;
                }
            }
        }

        super.destroy();
    }
}
