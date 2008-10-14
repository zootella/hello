package org.limewire.hello.base.file;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.move.StripeMove;
import org.limewire.hello.base.pattern.Range;
import org.limewire.hello.base.pattern.Trip;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;

public class WriteLater extends Later {
	
	// Make

	/** Write 1 or more bytes from bin to trip in file, don't look at bin until this is closed. */
	public WriteLater(Update above, File file, Range range, Bin bin) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.file = file;
		this.range = range;
		this.bin = bin;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}

	/** The file we write to. */
	private final File file;
	/** We write at least 1 byte in file at trip.at. */
	public final Range range;
	/** The Bin we take the data from. */
	private final Bin bin;

	// Result
	
	/** How much of stripe we wrote and how long it took, or throws the exception that made us give up. */
	public StripeMove result() throws Exception { return (StripeMove)check(move); }
	private StripeMove move;
	
	// Inside

	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private StripeMove workMove;

		// A worker thread will call this method
		public Void doInBackground() {
			try {
				
				// Read 1 or more bytes from stripe in file to bin
				workMove = bin.write(file, range);

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
