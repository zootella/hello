package org.limewire.hello.base.state.old;

//TODO what if this actually had private boolean closed in it?

public class OldClose {

	// -------- The core of an object that extends Close --------
	
	/** true once this object that extends Close has been closed, and we've counted it. */
	private boolean counted;
	
	/**
	 * Count that the program has made another new object that needs to be closed.
	 * This method automatically runs before execution enters the constructor of an object that extends Close.
	 */
	public OldClose() {
		open++; // Count one more object open, this new one that extends Close
	}

	/**
	 * Count that the program has closed an object.
	 * Call count() at the end of your object's close() method.
	 */
	protected void count() { // protected only lets your object that extends Close call count()
		
		// Only make it past here once for this object that extends Close
		if (counted)
			throw new IllegalStateException("object already closed");
		counted = true;
		
		// Count one fewer object open, this one that extends Close
		open--;
	}

	// -------- The static count the whole program shares --------
	
	/** The total number of objects the program has made that still need to be closed. */
	private static int open;

	/** Before the program closes, call Close.checkAll() to make sure every object with a close() method had it run. */
	public static void checkAll() {
		if (open != 0)
			throw new IllegalStateException("program closed with " + open + " open objects");
	}
}
