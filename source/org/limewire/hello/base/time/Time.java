package org.limewire.hello.base.time;

public class Time {

	// -------- Record the time right now --------

	/** Have this Time object record the time right now. */
	public void set() {
		time = now();
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
		return now() - time; // Compare the time now to what it was then
	}

	// -------- Copy --------
	
	/** Make a copy of this Time object. */
	public Time copy() {
		Time t = new Time();
		t.time = time;
		return t;
	}
	
	// -------- Recent --------
	
	/** Given a list of Time objects, return a new one with the most recent time among all of them. */
	public static Time recent(Time... times) {
		Time recent = new Time();                                        // Make a new Time object to return
		for (Time t : times)                                             // Loop through each Time object we were given
			if (t != null && recent.time < t.time) recent.time = t.time; // If t is later than recent, use it
		return recent;
	}
	
	// -------- Now --------
	
	/** The time right now, the number of milliseconds since midnight January 1, 1970 UTC. */
	public static long now() {
		return System.currentTimeMillis();
	}
	
	// -------- Units --------
	
	/** 1000, number of milliseconds in a second. */
	public static final long second = 1000;
	/** Number of milliseconds in a minute. */
	public static final long minute = 60 * second;
	/** Number of milliseconds in an hour. */
	public static final long hour = 60 * minute;
	/** Number of milliseconds in a day. */
	public static final long day = 24 * hour;
	/** Number of milliseconds in a week. */
	public static final long week = 7 * day;
	/** Number of milliseconds in a year, 365 days. */
	public static final long year = 365 * day;
}
