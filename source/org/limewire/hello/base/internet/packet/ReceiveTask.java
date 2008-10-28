package org.limewire.hello.base.internet.packet;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class ReceiveTask extends TaskClose {

	// Make

	/** Given the empty bin, wait on listen until a new Packet arrives. */
	public ReceiveTask(Update update, ListenPacket listen, Bin bin) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.listen = listen;
		this.bin = bin;
		
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	/** Our bound UDP socket that receives the packet. */
	private final ListenPacket listen;
	/** The empty Bin we put the data in, and then put inside the result Packet. */
	private final Bin bin;
	
	// Result
	
	/** The Packet we received, or throws the exception that made us give up. */
	public Packet result() throws Exception { return (Packet)check(packet); }
	private Packet packet;

	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Packet taskPacket; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Wait on listen until a new Packet arrives
			taskPacket = new Packet(listen, bin);				
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
