package org.limewire.hello.base.download;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Move;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;

public class GetLater extends Later {
	
	// Make

	/** Download 1 to size bytes from get to bin, don't look at bin until this is closed. */
	public GetLater(Update above, Get get, Range range, Bin bin) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.get = get;
		this.range = range;
		this.bin = bin;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}

	/** The HTTP GET request we download from. */
	private final Get get;
	/** We download at least 1 and at most range.size bytes from get. */
	public final Range range;
	/** The Bin we put the data in. */
	private final Bin bin;

	// Result
	
	/** How much of size we downloaded and how long it took, or throws the exception that made us give up. */
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
				
				// Download 1 or more bytes from get to bin
				workMove = bin.in(get.stream, range);

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
