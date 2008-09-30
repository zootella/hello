package org.limewire.hello.base.move;

import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.time.Duration;
import org.limewire.hello.base.time.Now;

public class FileMove {
	
	// Look
	
	/** How long this Move took to complete, the time before and after the blocking call that did it. */
	public final Duration duration;
	
	/** The file Stripe this Move read, wrote, downloaded, or uploaded. */
	public final Stripe stripe;
	
	// Make

	/** Make a Move object to document the result of a successful blocking data transfer. */
	public FileMove(Now start, long i, long size) {
		this.duration = new Duration(start); // Record now as the stop time
		this.stripe = new Stripe(i, size);
	}
}
