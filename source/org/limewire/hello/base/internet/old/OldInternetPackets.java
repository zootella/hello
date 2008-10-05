package org.limewire.hello.base.internet.old;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.List;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.internet.name.IpPort;

public class OldInternetPackets {

	// -------- Factory settings --------
	
	/**
	 * 65536 bytes, set the socket buffer for UDP to 64 KB.
	 * By default it's much smaller, like 8 KB.
	 */
	public static final int size = 64 * 1024;

	// -------- Make the program's InternetPackets object --------
	
	/**
	 * Make the program's InternetPackets object.
	 * 
	 * @param select A link back up to the program's InternetSelect object, which has the Selector
	 */
	public OldInternetPackets(OldInternetSelect select) {
		
		// Save the given link up
		this.select = select;

		// Make lists to hold the Packet objects we will upload and download
		upload = new LinkedList<OldPacket>();
		download = new LinkedList<OldPacket>();

		// Make a 64 KB Bay to hold the data of a UDP packet we're downloading
		bay = new Bay(size);
	}
	
	/** A link up to the program's InternetSelect object that has the Selector. */
	private OldInternetSelect select;

	// -------- Upload packets --------
	
	/**
	 * Upload a UDP packet to a remote peer on the Internet.
	 * 
	 * @param address The IP address and port number to send the UDP packet to.
	 * @param data    The data to send as the UDP packet payload.
	 */
	public void upload(IpPort address, Data d) {

		// Make a new Packet object from the given IP address and data
		OldPacket packet = new OldPacket(address, d); // Copy the data into the new Packet object

		// Add it to our list of packets to send
		upload.add(packet); // Add it to the end of the list so it will be sent after packets already there

		// We have a packet to upload, tell the Selector we're interested in writing
		interest();
	}
	
	/** A List of Packet objects, the UDP packets we have to upload. */
	private List<OldPacket> upload;

	/**
	 * The Selector says the datagram channel wants some data.
	 * Move as many UDP packets as we can from our upload list into it.
	 */
	public void selectUpload() throws IOException {
		
		// Loop until we run out of packets to send, or our channel refuses our upload
		while (true) {

			// Get the first packet in our list of packets to send
			OldPacket packet = upload.get(0); // The first Packet has index number 0
			if (packet == null) break; // The upload list is empty

			// Give it to our channel to upload as a UDP packet
			int uploaded = select.datagram.send(packet.data().toByteBuffer(), packet.address.toInetSocketAddress());
			if (uploaded == 0) break; // The channel is out of room, try to send more packets later
			
			// We uploaded it
			upload.remove(0); // Remove the packet we sent, it's still first in the list
		}
		
		// If we're out of packets to upload, turn write interest off
		interest();
	}
	
	// -------- Download packets --------
	
	/**
	 * The UDP packets the program has downloaded.
	 * A List of Packet objects.
	 * 
	 * When the Internet package receives a UDP packet, it wraps it in a Packet object and adds it to this list.
	 * Code outside the Internet package must look at these packets and remove them from this list.
	 * Otherwise, the list will grow as more and more packets arrive.
	 */
	public List<OldPacket> download;
	
	/**
	 * The Selector says the datagram channel has data for us.
	 * Download as many UDP packets as we can from it, putting them in the download list.
	 */
	public void selectDownload() throws IOException {

		// Loop until the channel runs out of packets
		while (true) {
			
			// Get the data of one UDP packet that's arrived
			IpPort from = bay.oldReceive(select.datagram);
			if (from == null) break; // The channel is out of packets

			// Make it into a new Packet object, and add it to the download list
			OldPacket packet = new OldPacket(from, bay.data()); // Copy the data from bay into the new Packet object
			download.add(packet);
		}
	}
	
	/** A 64 KB Bay selectDownload() uses to keep the data of a UDP packet it's downloading. */
	private Bay bay;

	// -------- Tell the Selector when we have packets to upload, and when we don't --------

	/**
	 * Tell the Selector if we are interested in uploading packets right now or not.
	 * Call interest() when this object changes to keep the Selector informed.
	 */
	private void interest() {
		
		// If our list of packets to upload isn't empty, we are interested in writing to the datagram channel
		select.interest(select.datagram, SelectionKey.OP_WRITE, !upload.isEmpty());
	}
}
