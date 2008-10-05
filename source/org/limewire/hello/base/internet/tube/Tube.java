package org.limewire.hello.base.internet.tube;

import java.net.InetSocketAddress;

import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.internet.socket.ConnectLater;
import org.limewire.hello.base.internet.socket.Socket;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Now;

// a Tube is a TCP socket connection
public class Tube extends Close {
	
	// Connect

	/** Make a new Tube when a peer has connected to us. */
	public Tube(Update above, Socket socket) {
		this.above = above;
		this.update = new Update(new MyReceive());

		this.outgoing = false; // The peer connected to us
		this.socket = socket;
		this.ipPort = new IpPort((InetSocketAddress)socket.channel.socket().getRemoteSocketAddress());
		
		timeAttempt = timeConnect = timeResponse = new Now();
		
		up = new TubeUp(this);
		down = new TubeDown(this);
	}

	/** Make a new Tube object to try to initiate a connection to a peer. */
	public Tube(Update above, IpPort ipPort) {
		this.above = above;
		this.update = new Update(new MyReceive());

		this.outgoing = true; // We will connect to the peer
		this.ipPort = ipPort;
		
		connect = new ConnectLater(update, ipPort);
		timeAttempt = new Now();
		
		up = new TubeUp(this);
		down = new TubeDown(this);
	}
	
	// Look

	/** Upload tools, options, and statistics. */
	public final TubeUp up;
	/** Download tools, options, and statistics. */
	public final TubeDown down;
	
	/** true if we connected to the peer, false if it connected to us. */
	public final boolean outgoing;
	/** The Internet IP address and port number of the peer on the far side of this Tube. */
	public final IpPort ipPort;
	
	/** When we started trying to connect, or accepted this connection. */
	public Now timeAttempt;
	/** When this connection started. */
	public Now timeConnect;
	/** When we last heard from the peer. */
	public Now timeResponse;
	
	
	/** The TCP socket that connects us to the peer. */
	public Socket socket;

	/** A ConnectUpdate we can use to make a new outgoing socket connection. */
	private ConnectLater connect;

	
	
	public void close() {
		if (already()) return;

		if (connect != null) connect.close();
		Close.close(socket); // This object actually owns the socket, and closes it
		
		up.close();
		down.close();
		
		above.send();
	}

	
	

	// Result

	/** true if we closed and won't change again. */
	public boolean closed() { return closed; }
	private boolean closed;
	
	/** The exception that closed us. */
	public Exception exception() { return exception; }
	Exception exception;
	
	
	
	// Update

	/** The Update above we'll notify when we change. */
	Update above;
	/** Our Update we'll give to objects below. */
	Update update;

	// When a worker object we gave our Update has progressed or completed, it calls this receive() method
	private class MyReceive implements Receive {
		public void receive() {
			if (closed) return;
			try {
				
				// We connected
				if (connect != null && connect.closed()) {
					
					socket = connect.result();
					
					timeConnect = timeResponse = new Now(); // Record the time
					
					connect = null; // Don't come in here again
					above.send();
				}

			} catch (Exception e) { exception = e; close(); }
		}
	}
	
	
	


	




}
