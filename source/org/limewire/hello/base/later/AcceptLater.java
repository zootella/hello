package org.limewire.hello.base.later;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.state.Update;

public class AcceptLater extends Later {

	// Make

	/** Wait for a peer to make a TCP socket connection to server. */
	public AcceptLater(Update above, ServerSocketChannel server) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.server = server;
		
		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}
	
	/** Our bound server socket a peer will connect to. */
	private final ServerSocketChannel server;

	// Result
	
	/** The socket that connected to server, it's yours to use and then close, or throws the exception that made us give up. */
	public SocketChannel result() throws Exception { return (SocketChannel)check(socket); }
	private SocketChannel socket;

	// Inside
	
	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private SocketChannel workSocket;

		// A worker thread will call this method
		public Void doInBackground() {
			try {

				// Wait here until a peer connects to us
				workSocket = server.accept();
				
			} catch (Exception e) { exception = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception != null) { // No exception, save what worker did
				
				socket = workSocket;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
