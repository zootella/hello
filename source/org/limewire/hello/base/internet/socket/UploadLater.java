package org.limewire.hello.base.internet.socket;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Move;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;

public class UploadLater extends Later {
	
	// Make

	/** Upload 1 or more bytes from bin to socket, don't look at bin until this is closed. */
	public UploadLater(Update above, Socket socket, Range range, Bin bin) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.socket = socket;
		this.range = range;
		this.bin = bin;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}

	/** The socket we upload to. */
	private final Socket socket;
	/** Limit how much we upload. */
	private final Range range;
	/** The Bin we take the data from. */
	private final Bin bin;

	// Result
	
	/** How much data we uploaded and how long it took, or throws the exception that made us give up. */
	public Move result() throws Exception { return (Move)check(move); }
	private Move move;
	
	// Inside

	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private Move workMove;

		// A worker thread will call this method
		public Void doInBackground() {
			try {
				
				// Upload 1 or more bytes from bin to socket
				workMove = bin.upload(socket, range);

			} catch (Exception e) { workException = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception == null) { // No exception, save what worker did
				
				move = workMove;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
