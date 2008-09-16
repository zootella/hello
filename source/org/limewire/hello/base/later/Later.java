package org.limewire.hello.base.later;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Now;

/** The core of an object that extends Later. */
public abstract class Later extends Close {

	// Inside

	/** The Update object above we'll tell when we're done. */
	protected Update above;
	/** null if things worked, or the exception that prevented success. */
	protected Exception exception;
	/** Our SwingWorker that has a worker thread run some code. */
	protected SwingWorker<Void, Void> work;
	
	// Close

	/**
	 * Close this object's resources and make sure it won't change again.
	 * If your object that extends Later has more to close than just worker, override this method.
	 */
	public void close() {
		if (already()) return;
		work.cancel(true); // true to interrupt its thread
	}

	/** Make sure we closed without exception and o isn't null before returning it as a result. */
	protected Object check(Object o) throws Exception {
		if (!closed()) throw new IllegalStateException(); // We're not closed yet
		if (exception != null) throw exception;           // We had an exception, throw it instead of returning o
		if (o == null) throw new NullPointerException();  // Never return null
		return o;
	}
	
	
	
	// Time
	
	/** The time when this Later object started trying to do something that takes awhile. */
	public final Now birth;
	
	public Later() {
		birth = new Now();
	}
}
