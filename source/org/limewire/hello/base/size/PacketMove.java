package org.limewire.hello.base.size;

import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.time.Duration;
import org.limewire.hello.base.time.Now;

public class PacketMove {
	
	// Make

	/** Document we successfully transferred a UDP packet with ipPort with a data payload of 0 or more bytes. */
	public PacketMove(Now start, int size, IpPort ipPort) {
		if (size < 0) throw new IndexOutOfBoundsException();
		this.duration = new Duration(start); // Record now as the stop time
		this.size = size;
		this.ipPort = ipPort;
	}
	
	// Look
	
	/** How long this Move took to complete, the time before and after the blocking call that did it. */
	public final Duration duration;
	
	/** The size of the UDP packet's data payload, 0 or more bytes. */
	public final int size;
	
	/** The IP address and port number we sent the UDP packet to or received it from. */
	public final IpPort ipPort;
}
