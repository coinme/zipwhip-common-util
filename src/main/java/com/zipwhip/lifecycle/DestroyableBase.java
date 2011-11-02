package com.zipwhip.lifecycle;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import com.zipwhip.util.HashCodeComparator;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/16/10
 * Time: 8:22 PM
 * <p/>
 * An easy to use class that is Destroyable, that you can extend.
 */
public abstract class DestroyableBase implements Destroyable {

	private boolean destroyed;

	private static final Comparator<Destroyable> COMPARATOR = new HashCodeComparator<Destroyable>();
	protected Collection<Destroyable> destroyables = null;

	@Override
	public void destroy() {
		if (this.destroyed) {
			return;
		}
		this.destroyed = true;

		if (destroyables != null) {
			synchronized (destroyables) {

				for (Destroyable destroyable : destroyables) {
					destroyable.destroy();
				}

				destroyables.clear();
			}
			destroyables = null;
		}

		this.onDestroy();
	}

	protected abstract void onDestroy();

	@Override
	public boolean isDestroyed() {
		return this.destroyed;
	}

	protected synchronized void createDestroyableList()
	{
		if (destroyables == null) {
			destroyables = Collections.synchronizedCollection(new TreeSet<Destroyable>(COMPARATOR));
		}
	}
}
