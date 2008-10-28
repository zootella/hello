package org.limewire.hello.base.internet.socket;

import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class AcceptTask extends TaskClose {

	// Make

	/** Wait for a peer to make a TCP socket connection to listen. */
	public AcceptTask(Update update, ListenSocket listen) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.listen = listen;
		
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	/** Our bound server socket a peer will connect to. */
	private final ListenSocket listen;

	// Result
	
	/** The socket that connected to server, it's yours to use and then close, or throws the exception that made us give up. */
	public Socket result() throws Exception { return (Socket)check(socket); }
	private Socket socket;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Socket taskSocket; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {

			// Wait here until a peer connects to us
			taskSocket = new Socket(listen.channel.accept());
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				socket = taskSocket;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
