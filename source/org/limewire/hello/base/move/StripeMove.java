package org.limewire.hello.base.move;

import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.time.Duration;
import org.limewire.hello.base.time.Now;

public class StripeMove {
	
	// Make

	/** Document the result of a successful blocking data transfer of 1 or more bytes a distance i into a file. */
	public StripeMove(Now start, long i, long size) {
		this.duration = new Duration(start); // Record now as the stop time
		this.stripe = new Stripe(i, size);
	}
	
	// Look
	
	/** How long this Move took to complete, the time before and after the blocking call that did it. */
	public final Duration duration;
	
	/** The Stripe in the file we read, wrote, downloaded, or uploaded. */
	public final Stripe stripe;
}
