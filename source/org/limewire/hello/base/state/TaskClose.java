package org.limewire.hello.base.state;

/** The core of a Task object that extends Close. */
public abstract class TaskClose extends Close {

	// Inside

	/** The Update object above we'll tell when we're done. */
	protected Update update;
	/** null if everything worked, or the exception that prevented success. */
	protected Exception exception;
	/** Our Task that has a separate thread run some code. */
	protected Task task;
	
	// Close

	/**
	 * Close this object's resources and make sure it won't change again.
	 * If your object that extends TaskClose has more to close than just task, override this method.
	 */
	public void close() {
		if (already()) return;
		task.close();
	}

	/** Make sure we closed without exception and o isn't null before returning it as a result. */
	protected Object check(Object o) throws Exception {
		if (!closed()) throw new IllegalStateException();         // Don't call this until closed
		if (exception != null) throw exception;                   // An exception made us give up
		if (o == null) throw new IllegalStateException("cancel"); // Closed without exception or result, must have been cancelled
		return o;
	}
}
