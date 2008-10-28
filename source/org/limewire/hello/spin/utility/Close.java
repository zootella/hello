package org.limewire.hello.spin.utility;

/** Have your object extend Close so the program will notice if you forget to later call its close() method. */
public abstract class Close {
	
	// Core
	
	/** true once this object that extends Close has been closed, and promises to not change again. */
	public boolean closed() { return objectClosed; }
	private boolean objectClosed; // Private so objects that extend Close can't get to this

	/** Call close() on an object to have it close objects inside, put away resources, and never change again. */
	public abstract void close(); // Your object that extends Close must have this method

	/**
	 * Mark this object that extends Close as closed, and only do this once.
	 * Start your close() method with the code if (already()) return;.
	 * The first time already() runs, it marks this object as closed and returns false.
	 * Try calling it again, and it will just return true.
	 */
	public boolean already() {
		if (objectClosed) return true; // We're already closed, return true to return from the close() method
		objectClosed = true;           // Mark this object that extends Close as now permanently closed
		return false;                  // Return false to run the contents of the close() method this first and only time
	}
}
