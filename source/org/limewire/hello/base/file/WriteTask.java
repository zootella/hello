package org.limewire.hello.base.file;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Move;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class WriteTask extends TaskClose {
	
	// Make

	/** Write 1 or more bytes from bin to range in file, don't look at bin until this is closed. */
	public WriteTask(Update update, File file, Range range, Bin bin) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.file = file;
		this.range = range;
		this.bin = bin;

		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}

	/** The file we write to. */
	private final File file;
	/** We write at least 1 byte in file at trip.at. */
	public final Range range;
	/** The Bin we take the data from. */
	private final Bin bin;

	// Result
	
	/** How much of stripe we wrote and how long it took, or throws the exception that made us give up. */
	public Move result() throws Exception { return (Move)check(move); }
	private Move move;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Move taskMove; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Read 1 or more bytes from stripe in file to bin
			taskMove = bin.write(file, range);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				move = taskMove;
				file.add(move.stripe); // Record the Stripe of data we wrote in file's StripePattern
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
