package org.limewire.hello.base.time;

public class Time {

	// Now
	
	/** The time right now, the number of milliseconds since midnight January 1, 1970 UTC. */
	public static long now() { return System.currentTimeMillis(); }

	// Units

	/** 1, number of milliseconds in a millisecond. */
	public static final long millisecond = 1;
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
