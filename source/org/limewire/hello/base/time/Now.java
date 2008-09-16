package org.limewire.hello.base.time;

/** A Now remembers the moment when it was made. */
public class Now {
	
	public Now() { time = Time.now(); }

	public final long time;

	/** true if milliseconds have passed since this Now was made. */
	public boolean expired(long milliseconds) {
		if (milliseconds < 0) throw new IllegalArgumentException();
		return age() >= milliseconds; 
	}
	
	/** The number of milliseconds that have passed since this New was made. */
	public long age() { return Time.now() - time; }
	
	
	
	
	// Compare

	/** Return this Now or the given one, whichever is the oldest. */
	public Now old(Now now) {
		if (time < now.time) return this;
		else return now;
	}
	
	/** Return this Now or the given one, whichever is the youngest. */
	public Now young(Now now) {
		if (time > now.time) return this;
		else return now;
	}
	
	
}
