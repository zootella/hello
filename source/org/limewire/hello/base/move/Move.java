package org.limewire.hello.base.move;

import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.time.Duration;
import org.limewire.hello.base.time.Now;

public class Move {
	
	// Look
	
	/** How long this Move took to complete, the time before and after the blocking call that did it. */
	public final Duration duration;
	
	/** The number of bytes this Move read, wrote, downloaded, or uploaded, 0 or more. */
	public final int size;
	
	/**
	 * The IP address and port number we received a UDP packet from.
	 * null if this Move isn't about receiving a UDP packet.
	 */
	public final IpPort ipPort;
	
	// Make

	/**
	 * Make a Move object to document the result of a successful blocking data transfer.
	 * @param start  The time when the transfer started, make your Move right when it's over
	 * @param stripe The file Stripe we moved
	 * @param ipPort The IP address and port number we received a UDP packet from, null if no packet
	 */
	public Move(Now start, int size, IpPort ipPort) {
		if (size < 0) throw new IllegalArgumentException();
		this.duration = new Duration(start); // Record now as the stop time
		this.size = size;
		this.ipPort = ipPort;
	}
}
