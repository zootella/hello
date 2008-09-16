package org.limewire.hello.base.time;

//TODO use New instead, which is immutable
public class OldTime {

	// -------- Record the time right now --------

	/** Have this Time object record the time right now. */
	public void set() {
		time = Time.now();
	}

	/**
	 * true if this Time object is recording how long it's been since it was set.
	 * false if this Time object has never been set.
	 */
	public boolean isSet() {
		return time != 0; // If we haven't been set yet, time will still be 0
	}
	
	/**
	 * The number of milliseconds between January 1970 and when this Time object was most recently set.
	 * 0 if this Time object hasn't been set yet.
	 */
	private long time;
	
	// -------- See how much time has passed since then --------
	
	/**
	 * Determine if a given amount of time has passed since this Time object was set.
	 * expired(0) will return true because no time has expired.
	 * 
	 * @param milliseconds The time that needs to have passed, as a number of milliseconds
	 * @param never        If this Time has never been set, return this boolean value
	 * @return             true if milliseconds have passed, false if they haven't yet, or never if we were never set
	 */
	public boolean expired(long milliseconds, boolean never) {
		if (!isSet()) return never; // If we were never set, return never instead of throwing an IllegalStateException
		if (milliseconds < 0) throw new IllegalArgumentException(); // Make sure milliseconds is 0 or more
		return expired() >= milliseconds; // Return true if the requested number of milliseconds, or more, have passed 
	}
	
	/**
	 * Find out how many milliseconds have passed since this Time object was last set.
	 * If you call this right after set() it will return 0 because no milliseconds have passed yet.
	 * 
	 * @throws IllegalStateException if this Time object has never been set
	 */
	public long expired() {
		if (time == 0) throw new IllegalStateException(); // Make sure we were set
		return Time.now() - time; // Compare the time now to what it was then
	}

	// -------- Copy --------
	
	/** Make a copy of this Time object. */
	public OldTime copy() {
		OldTime t = new OldTime();
		t.time = time;
		return t;
	}
	
	// -------- Recent --------
	
	/** Given a list of Time objects, return a new one with the most recent time among all of them. */
	public static OldTime recent(OldTime... times) {
		OldTime recent = new OldTime();                                        // Make a new Time object to return
		for (OldTime t : times)                                             // Loop through each Time object we were given
			if (t != null && recent.time < t.time) recent.time = t.time; // If t is later than recent, use it
		return recent;
	}
}
