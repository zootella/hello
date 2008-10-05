package org.limewire.hello.base.internet.old;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.internet.name.IpPort;

// a Packet is a UDP packet
public class OldPacket {

	// -------- A Packet object, and its parts --------
	
	/** The IP address and port number we're going to send this packet to, or that we received it from. */
	public IpPort address;
	
	/** Look at the data of the payload of this UDP packet. */
	public Data data() { return bay.data(); }

	/**
	 * Make a new Packet object to represent a UDP packet we're going to send or have received.
	 * 
	 * @param address The IP address and port number of the packet.
	 *                If we're going to send this packet, the destination address.
	 *                If we received this packet, the source address.
	 * @param data    The data of the payload of the UDP packet.
	 */
	public OldPacket(IpPort address, Data d) {
		
		// Make sure we were given data
		if (d.isEmpty()) throw new IllegalArgumentException();
		
		// Save the given address
		this.address = address;

		// Copy the given data into this new Packet object
		bay = new Bay(d);
	}
	
	/** A Packet object keeps the data of the payload of the UDP packet in this Bay object. */
	private Bay bay;
}
