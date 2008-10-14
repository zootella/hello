package org.limewire.hello.base.file;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;

public class OpenLater extends Later {
	
	// Make

	/** Open a file on the disk. */
	public OpenLater(Update above, Open open) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.open = open;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}
	
	/** The path to the file we open and how to open it. */
	public final Open open;

	// Result
	
	/** The File we opened, or throws the exception that made us give up. */
	public File result() throws Exception { return (File)check(file); }
	private File file;
	
	// Inside

	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private File workFile;

		// A worker thread will call this method
		public Void doInBackground() {
			try {
				
				// Open the file
				workFile = new File(open);

			} catch (Exception e) { workException = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception == null) { // No exception, save what worker did
				
				file = workFile;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
