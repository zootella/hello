package org.limewire.hello.base.internet.old;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.state.old.OldState;
import org.limewire.hello.base.time.OldTime;

// a Tube is a TCP socket connection
public class OldTube {

	// -------- Make a new Tube object to hold our TCP socket connection to a peer --------

	/**
	 * Make a new Tube object when a peer has connected to us.
	 * 
	 * @param select  A link back up to the program's InternetSelect object that has the Selector
	 * @param channel The SocketChannel that connects us to them
	 */
	public OldTube(OldInternetSelect select, SocketChannel channel) {

		// Link this new Tube object into the program
		this.select = select;            // Save the given link back up to the program's InternetSelect object
		select.internet.tubes.add(this); // Add this new Tube to the program's list of all of them
		
		// Make the objects inside this new Tube that will make it work
		upload = new OldTubeUpload(this);
		download = new OldTubeDownload(this);
		attempt = new OldTime();
		connect = new OldTime();

		// Save direction and address information
		outgoing = false; // The peer connected to us
		address = new IpPort((InetSocketAddress)channel.socket().getRemoteSocketAddress());

		try {
			
			// Save the given SocketChannel, and register it with our Selector
			this.channel = channel;
			channel.configureBlocking(false);
			select.register(channel, SelectionKey.OP_READ, this);
			
		// If that caused an IOException, this new Tube object will start out closed and not in the list
		} catch (IOException e) { close(new OldState(OldState.socketException, e)); return; }
		
		// We're connected right from the start
		connect.set(); // Set the time we connected as right now
	}

	/**
	 * Make a new Tube object to try to initiate a connection to a peer.
	 * 
	 * @param select  A link back up to the program's InternetSelect object that has the Selector
	 * @param address The IP address and port number to try to connect to
	 */
	public OldTube(OldInternetSelect select, IpPort address) {

		// Link this new Tube object into the program
		this.select = select;            // Save the given link back up to the program's InternetSelect object
		select.internet.tubes.add(this); // Add this new Tube to the program's list of all of them
		
		// Make the objects inside this new Tube that will make it work
		upload = new OldTubeUpload(this);
		download = new OldTubeDownload(this);
		attempt = new OldTime();
		connect = new OldTime();
		
		// Save direction and address information
		outgoing = true;        // We will connect to the peer
		this.address = address; // Save the IP address and port number we're going to try to connect to

		try {
			
			// Make a new SocketChannel, and register it with our Selector
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			select.register(channel, SelectionKey.OP_CONNECT | SelectionKey.OP_READ, this); // We are interested in connecting and downloading
			
			// Tell our SocketChannel to try to connect to the IP address and port number
			boolean connected = channel.connect(address.toInetSocketAddress());
			attempt.set(); // Now starts the time we'll wait for our outgoing connection to go through
			if (connected) {
				
				// It connected immediately
				connect.set(); // Set the time we connected as right now
				select.interest(channel, SelectionKey.OP_CONNECT, false); // We're not interested in connecting
			}
			
		// If that caused an IOException, this new Tube object will start out closed and not in the list
		} catch (IOException e) { close(new OldState(OldState.socketException, e)); return; }
	}
	
	// -------- Get this Tube's current state, and close it --------

	/**
	 * Find out what this Tube's current state is.
	 * 
	 * Active operations:
	 * 
	 * opening         This Tube has initiated a TCP socket connection to a remote IP address, and is waiting for it to complete or fail.
	 * doing           This Tube has an open TCP socket connection, and can upload and download data through it.
	 * 
	 * Closed outcomes:
	 * 
	 * cancelled       The program closed the TCP socket connection because it doesn't need it anymore.
	 * socketException Java threw this Tube a socket exception because it lost our TCP socket connection.
	 * 
	 * @return A State object that describes our state right now
	 */
	public OldState state() {

		// If we're closed, that's our final state, return it
		if (closed != null) return closed;
		
		// Ask our SocketChannel if it's open to determine our state
		if (channel.isConnected()) return OldState.doing();
		else                       return OldState.opening();
	}

	/**
	 * Close this TCP socket connection and remove this Tube from the program's list of them.
	 * 
	 * @param closed A State object that tells how and why we closed
	 */
	public void close(OldState closed) {
		
		// Only let us close once, and save the given final closed state
		if (state().isClosed()) return;
		this.closed = closed;
		
		// Close our TCP socket connection
		try {
			channel.close(); // This also cancels the key, removing our channel's registration with the Selector
		} catch (IOException ignore) {} // An exception doesn't matter because we're closing anyway
		
		// Remove us from the program's list of Tube objects
		select.internet.tubes.remove(this);
	}

	/** Our final state that tells how and why we closed, or null if we're not closed yet. */
	private OldState closed;

	// -------- Information about this connection --------

	/**
	 * The IP address and port number of the peer this Tube connects us to.
	 * This is the real Internet IP address and port number of the distant peer, as we see it from our side of the Internet.
	 * 
	 * If we connected to the peer, it's the IP address and port number we tried to connect to.
	 * If the peer connected to us, it's the IP address and port number we got from the SocketChannel we accepted.
	 */
	public IpPort address;
	
	/**
	 * true if we connected to the peer, starting with its IP address and port number and then trying to open a new connection to it.
	 * false if the peer connected to us, and our listening socket accepted a new incoming connection.
	 */
	public boolean outgoing;

	/**
	 * The Time when we attempted to make our outgoing TCP socket connection.
	 * How long we've been waiting for our connection to go through.
	 * If the remote peer connected to us, this Time object won't be set.
	 */
	public OldTime attempt;
	
	/**
	 * The Time when our TCP socket connection was made.
	 * How long we've been connected.
	 * If we're still trying to connect, this Time object won't be set yet.
	 */
	public OldTime connect;
	
	/**
	 * The Time when we last heard from the remote peer at the other end of this Tube.
	 * The Time when we connected, or when we most recently downloaded data after that.
	 */
	public OldTime response() {

		// Pick whichever happend most recently, the Time we connected or the Time we last downloaded data
		return OldTime.recent(connect, download.transfer);
	}

	// -------- Do something when the Selector says we can --------
	
	/**
	 * Our SocketChannel, which we told to connect to an IP address and port number, made that connection, or gave up.
	 */
	public void selectConnect() throws IOException {

		// Find out what happened
		boolean connected = channel.finishConnect(); // Throws an IOException if our connection attempt failed
		if (!connected) throw new IOException("finishConnect() didn't throw an IOException, but returned false");

		// Set the time we connected as right now
		connect.set();

		// Tell the Selector we're not interested in connecting any longer
		select.interest(channel, SelectionKey.OP_CONNECT, false);
	}

	// -------- Internal parts --------

	/** A link back up to the program's InternetSelect object that has the Selector. */
	public OldInternetSelect select;

	/** The Java SocketChannel object that holds our TCP socket connection to the peer. */
	public SocketChannel channel;

	/** A Tube has a TubeUpload object that uploads data to the peer, with features like statistics and compression. */
	public OldTubeUpload upload;
	
	/** A Tube has a TubeDownload object that downloads data from the peer, with features like statistics and compression. */
	public OldTubeDownload download;
}
