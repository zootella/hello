package org.limewire.hello.base.internet.packet;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;

public class SendLater extends Later {

	// Make

	/** Send bin's data to ipPort in a UDP packet, don't look at bin after this. */
	public SendLater(Update above, ListenPacket listen, Bin bin, IpPort ipPort) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.listen = listen;
		this.bin = bin;
		this.ipPort = ipPort;
		
		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}
	
	/** Our bound UDP socket we use to send the packet. */
	private final ListenPacket listen;
	/** The data we send. */
	private final Bin bin;
	/** The IP address and port number we send the UDP packet to. */
	private final IpPort ipPort;

	// Result
	
	/** A Packet that tells when it was sent and has an empty bin to reuse, or throws the exception that made us give up. */
	public Packet result() throws Exception { return (Packet)check(packet); }
	private Packet packet;

	// Inside
	
	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private Packet workPacket;

		// A worker thread will call this method
		public Void doInBackground() {
			try {
				
				// Use listen to send bin's data to ipPort in a UDP packet
				workPacket = new Packet(listen, bin, ipPort);
				
			} catch (Exception e) { exception = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception != null) { // No exception, save what worker did
				
				packet = workPacket;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
