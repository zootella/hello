package org.limewire.hello.base.state;

/** Have your object extend Close so the program will notice if you forget to later call its close() method. */
public abstract class Close {
	
	// Core

	/**
	 * Count that the program has made another new object that needs to be closed.
	 * This automatically runs before execution enters the constructor of an object that extends Close.
	 */
	public Close() {
		programOpen++; // Count the program has one more object open, this new one that extends Close
	}
	
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
		programOpen--;                 // Count the program has one fewer object it needs to close
		return false;                  // Return false to run the contents of the close() method this first and only time
	}

	// Program
	
	/** The total number of objects the program has made that still need to be closed. */
	private static int programOpen;

	/** Before the program closes, call Close.checkAll() to make sure every object with a close() method had it run. */
	public static void checkAll() {
		if (programOpen != 0)
			throw new IllegalStateException("program closed with " + programOpen + " open objects");
	}

	// Help

	/** Close the given object that extends Close, takes null and ignores exception. */
	public static void close(Close c) {
		if (c == null) return;
		try { c.close(); } catch (Exception e) {}
	}
}
