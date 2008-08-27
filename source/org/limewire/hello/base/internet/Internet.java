package org.limewire.hello.base.internet;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.limewire.hello.base.data.Data;

public class Internet {

	// -------- Make and pulse the Internet object --------
	
	/** Make the program's Internet object to transfer data with remote peers. */
	public Internet() {
		select  = new InternetSelect(this);    // Make the InternetSelect object that has the Selector
		packets = new InternetPackets(select); // Make the InternetPackets object to send and receive UDP packets
		tubes   = new LinkedList<Tube>();      // Make a list for the Tube objects that hold TCP socket connections
	}
	
	/** The InternetSelect object has the Selector. */
	public InternetSelect select;

	/** The InternetPackets object sends and receives UDP packets. */
	public InternetPackets packets;

	/** Close threads so the program can close. */
	public void close() {
		select.close(); // Tell the InternetSelect object to close its thread and not make a new one
	}

	// -------- Become a server --------
	
	/**
	 * Start listening for incoming TCP socket connections and UDP packets.
	 * A peer-to-peer program is both a client and a server.
	 * Call listen() to become a server.
	 * 
	 * Prepare firewalls to allow the program to become a server before you call listen().
	 * Binding a socket to an address will cause firewalls to display warnings.
	 * 
	 * @param port The port number to listen on
	 * @return     true if it worked, false if there was an exception binding the sockets
	 */
	public boolean listen(int port) {

		// Bind our TCP server and UDP sockets to the given port number
		try {
			select.listen(port);
		} catch (IOException e) { return false; } // Return false if there was an exception
		return true; // It worked
	}

	// -------- Transfer data on the Internet --------

	/**
	 * Make a TCP socket connection to a remote peer on the Internet.
	 * 
	 * @param IpPort The IP address and port number to try to connect to.
	 * @return       A new Tube object that represents the connection.
	 *               Use it to see when the connection goes through, and to transfer data.
	 */
	public Tube connect(IpPort address) {

		// Make a return a new Tube object
		return new Tube(select, address);
	}

	/** Loop through all of the program's TCP socket connections. */
	public List<Tube> tubes; // The tubes of the Internet
	
	/**
	 * Send a UDP packet.
	 * 
	 * @param address The IP address and port number to send it to.
	 * @param data    The data to put in the UDP packet payload.
	 */
	public void uploadPacket(IpPort address, Data d) {

		// Have our InternetPackets object do it
		packets.upload(address, d);
	}
	
	/**
	 * Get UDP packets that have arrived.
	 * Clear this list on each pulse so it doesn't get bigger and bigger.
	 * 
	 * @return A list of Packet objects.
	 */
	public List<Packet> packetsDownloaded() {

		// Get the list from our InternetPackets object
		return packets.download;
	}
}
