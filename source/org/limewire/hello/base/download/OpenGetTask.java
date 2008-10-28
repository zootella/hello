package org.limewire.hello.base.download;

import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.web.Url;

public class OpenGetTask extends TaskClose {
	
	// Make

	/** Make a HTTP GET request for url, and get the web server's response. */
	public OpenGetTask(Update update, Url url, Range range) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.url = url;
		this.range = range;

		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	/** The Url we get. */
	public final Url url;
	/** The Range we request. */
	public final Range range;

	// Result
	
	/** The Get request we opened, or throws the exception that made us give up. */
	public Get result() throws Exception { return (Get)check(get); }
	private Get get;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Get taskGet; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Make the request
			taskGet = new Get(url, range);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				get = taskGet;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
