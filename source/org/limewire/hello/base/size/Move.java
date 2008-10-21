package org.limewire.hello.base.size;

import org.limewire.hello.base.time.Duration;
import org.limewire.hello.base.time.Now;

public class Move {
	
	// Make
	
	/** Document the result of a successful blocking data transfer of 1 or more bytes. */
	public Move(Now start, long size) { this(start, 0, size); }
	/** Document the result of a successful blocking data transfer of 1 or more bytes at index i. */
	public Move(Now start, long i, long size) {
		this.duration = new Duration(start); // Record now as the stop time
		this.stripe = new Stripe(i, size);
	}
	
	// Look
	
	/** How long this Move took to complete, the time before and after the blocking call that did it. */
	public final Duration duration;
	/** The Stripe we uploaded, downloaded, read, or wrote, index 0 start of file or no file, size 1 or more. */
	public final Stripe stripe;
}
