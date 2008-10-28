package org.limewire.hello.base.download;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Move;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class GetTask extends TaskClose {
	
	// Make

	/** Download 1 to size bytes from get to bin, don't look at bin until this is closed. */
	public GetTask(Update update, Get get, Range range, Bin bin) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.get = get;
		this.range = range;
		this.bin = bin;

		task = new Task(new MyTask()); // Make a separate thread call thread() below now
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
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Move taskMove; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Download 1 or more bytes from get to bin
			taskMove = bin.in(get.stream, range);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				move = taskMove;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
