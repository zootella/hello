package org.limewire.hello.base.internet.packet;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class SendTask extends TaskClose {

	// Make

	/** Send bin's data to ipPort in a UDP packet, don't look at bin after this. */
	public SendTask(Update update, ListenPacket listen, Bin bin, IpPort ipPort) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.listen = listen;
		this.bin = bin;
		this.ipPort = ipPort;
		
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
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

	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Packet taskPacket; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Use listen to send bin's data to ipPort in a UDP packet
			taskPacket = new Packet(listen, bin, ipPort);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				packet = taskPacket;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
