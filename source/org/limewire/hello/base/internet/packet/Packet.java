package org.limewire.hello.base.internet.packet;

import java.io.IOException;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.size.PacketMove;

public class Packet {
	
	// Send
	
	/**
	 * Use listen to send bin's data to ipPort in a UDP packet.
	 * @return A Packet that tells when it was sent, and has an empty bin you can reuse
	 */
	public Packet(ListenPacket listen, Bin bin, IpPort ipPort) throws IOException {
		if (listen.closed()) throw new IOException("listen closed");
		this.outgoing = true; // Send
		this.move = bin.send(listen, ipPort);
		this.ipPort = ipPort;
		this.bin = bin;
	}
	
	// Receive
	
	/** Given the empty bin, wait on listen until a new Packet arrives. */
	public Packet(ListenPacket listen, Bin bin) throws IOException {
		if (listen.closed()) throw new IOException("listen closed");
		this.outgoing = false; // Receive
		this.move = bin.receive(listen);
		this.ipPort = move.ipPort;
		this.bin = bin;
	}
	
	// Look

	/** true if we sent this Packet, false if we received it. */
	public final boolean outgoing;
	/** How big this Packet is and how long it took to send, or how long we waited for it. */
	public final PacketMove move;
	/** The IP address and port number we sent this Packet to or received it from. */
	public final IpPort ipPort;
	/** An empty bin after sending this packet, or the data of the packet we received. */
	public final Bin bin;
}
