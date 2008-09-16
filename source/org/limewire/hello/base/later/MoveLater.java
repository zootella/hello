package org.limewire.hello.base.later;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Move;
import org.limewire.hello.base.time.Now;

public class MoveLater extends Later {
	
	// Make

	/** Move data from source to destination, don't look at either until this is closed. */
	public MoveLater(Update above, Bin source, Bin destination) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.source = source;
		this.destination = destination;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}

	/** The source Bin. */
	private final Bin source;
	/** The destination Bin. */
	private final Bin destination;

	// Result
	
	/** How much data we moved and how long it took, or throws the exception that made us give up. */
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
				
				// Move data from source to destination
				Now start = new Now();
				int did = source.size();
				destination.add(source);
				did -= source.size();
				workMove = new Move(start, null, did, null);

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
