package org.limewire.hello.base.internet.socket;

import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class ConnectTask extends TaskClose {
	
	// Make

	/** Make a new outgoing TCP socket connection to ipPort. */
	public ConnectTask(Update update, IpPort ipPort) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.ipPort = ipPort;
		
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	/** The IP address and port number to connect to. */
	public final IpPort ipPort;

	// Result
	
	/** The socket we connected, its yours to use and then close, or throws the exception that made us give up. */
	public Socket result() throws Exception { return (Socket)check(socket); }
	private Socket socket;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Socket taskSocket; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Make and connect a new socket to the given IP address and port number
			taskSocket = new Socket(ipPort);
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
