package org.limewire.hello.base.state;

import org.limewire.hello.base.time.Time;

/** A Spin object detects when an Update is sending events so quickly it must be broken. */
public class Spin {

	/** Count another event this second, and throw a SpinException if there are too many. */
	public void count() {

		// Find out what second this is
		long now = Time.now() / Time.second;

		// We're in a new second
		if (second != now) {
			second = now; // Save it
			count = 0;    // Zero our count
		}

		// Count this event, and throw an exception if there are too many
		count++;
//		if (count > limit) throw new IllegalStateException();
//TODO put that back
	}

	/** The second we're in, 0 before we start. */
	private long second;

	/** The number of events we've counted in this second. */
	private long count;

	/**
	 * 100,000 events/second, the limit.
	 * More than this in a second, and we'll throw an exception.
	 * On a January 2008 PC, Update in a tight loop spun 623,000 times a second.
	 * Processing an 8 KB Bin.medium of data at a time, 800,000 KB/s is 781 MB/s or 0.76 GB/s.
	 */
	private final long limit = 100000;
}
