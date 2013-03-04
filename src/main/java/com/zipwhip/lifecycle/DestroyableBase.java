package com.zipwhip.lifecycle;

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

    @Override
	public synchronized void destroy() {
		if (this.destroyed) {
			return;
		}

		this.destroyed = true;

		this.onDestroy();
	}

	protected abstract void onDestroy();

	@Override
	public boolean isDestroyed() {
		return this.destroyed;
	}

}
