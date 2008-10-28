package org.limewire.hello.base.file;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Move;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.size.StripePattern;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class ReadTask extends TaskClose {
	
	// Make

	/** Read 1 or more bytes from stripe in file to bin, don't look at bin until this is closed. */
	public ReadTask(Update update, File file, Range range, Bin bin) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.file = file;
		this.pattern = file.pattern(); // Get the StripePattern that shows where file has data
		this.range = range;
		this.bin = bin;
		
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}

	/** The File we read from. */
	private final File file;
	/** The StripePattern that shows where file has data. */
	private final StripePattern pattern;
	/** We read at least 1 byte from the start of stripe. */
	public final Range range;
	/** The Bin we put the data in. */
	private final Bin bin;

	// Result
	
	/** How much of stripe we read and how long it took, or throws the exception that made us give up. */
	public Move result() throws Exception { return (Move)check(move); }
	private Move move;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Move taskMove; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Read 1 or more bytes from stripe in file to bin
			taskMove = bin.read(file, pattern, range);
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
