package org.limewire.hello.base.time;

import org.limewire.hello.base.internet.IpPort;
import org.limewire.hello.base.pattern.Stripe;

public class Move {
	
	// Look
	
	/** How long this Move took to complete, the time before and after the blocking call that did it. */
	public final Duration duration;

	/**
	 * The file Stripe this Move tried to read, write, download, or upload.
	 * null if this Move is about an Internet transfer with just sockets and buffers involved.
	 */
	public final Stripe ask;
	
	/**
	 * The file Stripe this Move actually read, wrote, downloaded, or uploaded.
	 * 1 or more bytes at the start of ask.
	 * If this Move is about an Internet transfer with just sockets and buffers involved, stripe starts at 0 and just tells the data size moved.
	 */
	public final Stripe did;
	
	/**
	 * The file Stripe this Move didn't do, ask - did.
	 * null if this Move is about an Internet transfer with just sockets and buffers involved.
	 */
	public Stripe remain() {
		if (ask == null) return null; // It only makes sense to call this if the Move is about a file
		return ask.minus(did);
	}
	
	/**
	 * The IP address and port number we received a UDP packet from.
	 * null if this Move isn't about receiving a UDP packet.
	 */
	public final IpPort ipPort;
	
	// Make

	/**
	 * Make a Move object to document the result of a successful blocking data transfer.
	 * @param start  The time when the transfer started, make your Move right when it's over
	 * @param ask    The file Stripe we were asked to move, null if no file
	 * @param did    The number of bytes we moved, 1 or more
	 * @param ipPort The IP address and port number we received a UDP packet from, null if no packet
	 */
	public Move(Now start, Stripe ask, long did, IpPort ipPort) {
		this.duration = new Duration(start); // Record now as the stop time
		this.ask = ask;
		if (ask == null) this.did = new Stripe(0, did);     // No file involved, ask null and did at 0
		else             this.did = new Stripe(ask.i, did); // File involved, ask a Stripe and did starts at ask
		this.ipPort = ipPort;
	}

	// Calculate
	
	/** The speed of this Move per unit like Time.second or Time.minute. */
	public long speed(long unit) {
		return did.size * unit / duration.time();
	}
	
	/** Total up the given Move objects to make one with the earliest start and stop times and the total size. */
	public Move(Move... moves) {
		if (moves.length == 0) throw new IllegalArgumentException();

		// Loop through the given Move objects
		long size = 0;
		Now start = null;
		Now stop = null;
		for (Move move : moves) {
			size += move.did.size; // Total the stripe size
			start = start.old(move.duration.start); // Find the oldest start and youngest stop times
			stop = stop.young(move.duration.stop);
		}

		// Save those values in this new object
		this.duration = new Duration(start, stop);
		this.ask = null;
		this.did = new Stripe(0, size);
		this.ipPort = null;
	}
}
