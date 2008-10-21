package org.limewire.hello.base.download;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.web.Url;

public class OpenGetLater extends Later {
	
	// Make

	/** Make a HTTP GET request for url, and get the web server's response. */
	public OpenGetLater(Update above, Url url, Range range) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.url = url;
		this.range = range;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}
	
	/** The Url we get. */
	public final Url url;
	/** The Range we request. */
	public final Range range;

	// Result
	
	/** The Get request we opened, or throws the exception that made us give up. */
	public Get result() throws Exception { return (Get)check(get); }
	private Get get;
	
	// Inside

	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private Get workGet;

		// A worker thread will call this method
		public Void doInBackground() {
			try {
				
				// Make the request
				workGet = new Get(url, range);

			} catch (Exception e) { workException = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception == null) { // No exception, save what worker did
				
				get = workGet;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
