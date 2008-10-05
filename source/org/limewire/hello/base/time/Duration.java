package org.limewire.hello.base.time;

public class Duration {
	
	//TODO rename Life, with birth and death

	/** Make a new Duration that will record the given start time to right now. */
	public Duration(Now start) {
		this(start, new Now()); // Call the next constructor
	}

	/** Make a new Duration to record the given start and stop times. */
	public Duration(Now start, Now stop) {
		if (stop.time < start.time) throw new IllegalArgumentException(); // Make sure stop is at or after start
		this.start = start;
		this.stop = stop;
	}

	/** The time when this Duration started. */
	public final Now start;
	/** The time when this Duration stopped, the same as start or afterwards. */
	public final Now stop;

	/** The length of this Duration in milliseconds, 1 or more. */
	public long time() {
		long t = stop.time - start.time;
		if (t == 0) return 1; // A 0 might end up on the bottom of a speed fraction
		else return t;
	}
}
