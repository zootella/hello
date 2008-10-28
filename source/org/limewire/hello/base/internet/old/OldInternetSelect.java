package org.limewire.hello.base.internet.old;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import javax.swing.SwingWorker;

import org.limewire.hello.all.Main;
import org.limewire.hello.base.exception.PlatformException;
import org.limewire.hello.base.exception.ProgramException;
import org.limewire.hello.base.state.old.OldState;

public class OldInternetSelect {

	// -------- The program's InterentSelect object and its contents --------
	
	/**
	 * Make the program's InternetSelect object to hold the Selector, which tells us when a channel is ready for something.
	 * 
	 * @param internet A link back up to the program's Internet object
	 */
	public OldInternetSelect(OldInternet internet) {
		try {
			this.internet = internet;   // Save the link to the Internet object we're a part of
			selector = Selector.open(); // Make the Java Selector object
			on();                       // Start a SwingWorker thread that will wait on the Selector for a channel to become ready
		} catch (IOException e) { throw new PlatformException(); } // If we can't make the Selector, tell the user the program can't run
	}

	/** A link back up to the program's Internet object. */
	public OldInternet internet;
	
	/** The Java Selector object that will tell us which channels are ready for which operations. */
	private Selector selector;
	
	// -------- Listen for incoming TCP socket connections, and send and receive UDP packets --------

	/**
	 * Make our TCP server socket and UDP socket, and bind them to the given port number.
	 * This starts the program listening for incoming TCP socket connections and UDP packets.
	 * 
	 * @param port The port number to listen on
	 */
	public void listen(int port) throws IOException {

		// Make both new channels
		server = ServerSocketChannel.open();
		datagram = DatagramChannel.open();
		datagram.socket().setSendBufferSize(OldInternetPackets.size);    // Be able to send a 64 KB UDP packet, the default is just 8 KB
		datagram.socket().setReceiveBufferSize(OldInternetPackets.size); // Be able to receive a 64 KB UDP packet

		// Tell the channels to never block, and register them with the Selector
		server.configureBlocking(false);
		datagram.configureBlocking(false);
		register(server, SelectionKey.OP_ACCEPT, null); // We're only interested in accepting new connections
		register(datagram, SelectionKey.OP_READ, null); // We're always interested in downloading UDP packets

		// Bind both channels to the given port number
		server.socket().bind(new InetSocketAddress(port));
		datagram.socket().bind(new InetSocketAddress(port));
	}
	
	/** Our Java ServerSocketChannel which listens for incoming TCP socket connections. */
	private ServerSocketChannel server;
	/** Our Java DatagramChannel which sends and receives UDP packets. */
	public DatagramChannel datagram;

	// -------- Use the Selector --------
	
	/**
	 * Register a channel with the Selector.
	 * 
	 * @param channel    The channel
	 * @param operations The operations the Selector should tell the channel to do, when it can do them
	 * @param attachment A link back up to an Object, like a Tube, that keeps the channel
	 */
	public void register(SelectableChannel channel, int operations, Object attachment) throws IOException {
		
		// We have to bump the SwingWorker thread off the Selector before calling register()
		off();
		
		// Register the channel with the Selector
		channel.register(selector, operations, attachment);
	}

	/**
	 * Change which operations the Selector will tell a channel it can do.
	 * 
	 * @param channel   The channel that we're using with the Selector
	 * @param operation An int with 1 bit set for an operation, like SelectionKey.OP_ACCEPT
	 * @param set       true to have the Selector tell us when the channel can do the operation, false to not look for it
	 */
	public void interest(SelectableChannel channel, int operation, boolean set) {
		
		// Make sure the channel is open and registered with the Selector
		if (channel == null || !channel.isOpen() || !channel.isRegistered()) return;
		
		// Bump the SwingWorker thread off the Selector so a new one will see our new interest
		off();

		// Update our interested operations
		int before = channel.keyFor(selector).interestOps();              // Find out what operations we're already interested in
		int after = set ? (before | operation) : (before & ~operation);   // Set or clear the given operation bit
		if (after != before) channel.keyFor(selector).interestOps(after); // If that changed something, update the interest
	}

	// -------- Make a MySwingWorker object to watch the Selector --------

	/** Make a new MySwingWorker object to have a SwingWorker thread watch the Selector. */
	private void on() {
		if (closed) return;              // If the program is closing, making a new MySwingWorker would keep it open
		(new MySwingWorker()).execute(); // Make a new MySwingWorker object, and have a SwingWorker thread call its doInBackground() method
	}

	/** Bump the SwingWorker thread currently watching the Selector off of it. */
	private void off() {
		selector.wakeup(); // If there is a SwingWorker thread stuck on selector.select(), have that call return now
	}
	
	/** Have the InternetSelect object discard its SwingWorker thread and not make a new one so the program can close. */
	public void close() {
		off();         // Bump the current SwingWorker thread off selector.select()
		closed = true; // Mark the InternetSelect object as closed
	}
	/** True if the InternetSelect object has been closed in preparation to close the program. */
	private boolean closed;

	/** A MySwingWorker object has a SwingWorker thread that waits on the Selector until a channel becomes ready for something. */
	private class MySwingWorker extends SwingWorker<Void, Void> { // Returns nothing, and doesn't report progress

		// When on() calls (new MySwingWorker()).execute(), Java will have a separate SwingWorker thread call this method
		public Void doInBackground() throws Exception {
			
			// Have the SwingWorker thread wait here until a channel becomes ready for something, or off() calls selector.wakeup()
			selector.select();
			return null;
		}

		// Once doInBackground() returns, the normal Swing thread calls this done() method
		public void done() {

			// If no channels are ready, soon will remain false
			boolean soon = false;
			
			try {

				// Ask the Selector which channels are ready for which operations, and perform them
				Iterator<SelectionKey> i = selector.selectedKeys().iterator();
				while (i.hasNext()) {
					SelectionKey key = i.next();
					soon = true; // At least one channel is ready
					
					// It's our server socket channel that listens for new connections
					if (server != null && key.channel() == server) {
						
						// If it has a new connection for us, accept it and make a Tube for it
						if (key.isAcceptable()) new OldTube(null, server.accept()); // Adds the new Tube to the list
						
					// It's our channel that sends and receives UDP packets
					} else if (datagram != null && key.channel() == datagram) {
						
						// Tell the InternetPackets object to download and upload
						if (key.isReadable()) internet.packets.selectDownload();
						if (key.isWritable()) internet.packets.selectUpload();
						
					// It must be a TCP socket connection in a Tube object
					} else {
						OldTube tube = (OldTube)key.attachment(); // Get the Tube the channel is in
						if (!tube.state().isClosed()) {     // Don't touch a closed Tube
							
							try {
								
								// Tell the Tube to connect, download, and upload
								if (key.isConnectable()) tube.selectConnect();
								if (key.isReadable())    tube.download.selectDownload();
								if (key.isWritable())    tube.upload.selectUpload();
								
							// If an IOException happened, close the Tube
							} catch (IOException e) { tube.close(new OldState(OldState.socketException, e)); }
						}
					}
					
					// Remove the SelectionKey from the Set so the Selector can select it again later
					i.remove();
				}

			// If our server or datagram sockets throw an IOEexception, restart the program
			} catch (IOException e) { throw new ProgramException(); }

			// If we did something, have the next pulse happen soon so the rest of the program can see what's new
			if (soon) Main.soon();

			// This MySwingWorker object's work is done, make a new one for the next time
			on();
		}
	}
}
