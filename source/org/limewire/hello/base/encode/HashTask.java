package org.limewire.hello.base.encode;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Move;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Now;

public class HashTask extends TaskClose {
	
	// Make

	/** SHA1 hash and clear bin's data with the given Hash object, don't look at hash or bin until this is closed. */
	public HashTask(Update update, Hash hash, Bin bin, Range range) {
		this.update = update; // We'll tell update when we're done
		
		// Save the input
		this.hash = hash;
		this.bin = bin;
		this.range = range;

		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}

	/** The Hash object we use. */
	private final Hash hash;
	/** The Bin we put the data in. */
	private final Bin bin;
	/** The limit of how much data we'll hash. */
	private final Range range;

	// Result
	
	/** How much we hashed when we're done, or throws the exception that made us give up. */
	public Move result() throws Exception { return (Move)check(move); }
	private Move move;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Move taskMove; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
			
			// Hash data from bin and remove it
			Now start = new Now();
			int ask = range.ask(bin.size());
			hash.add(bin.data().begin(ask));
			bin.remove(ask);
			taskMove = new Move(start, ask);
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
