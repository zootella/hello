package org.limewire.hello.base.file;

import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class OpenTask extends TaskClose {
	
	// Make

	/** Open a file on the disk. */
	public OpenTask(Update update, Open open) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.open = open;

		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	/** The path to the file we open and how to open it. */
	public final Open open;

	// Result
	
	/** The File we opened, or throws the exception that made us give up. */
	public File result() throws Exception { return (File)check(file); }
	private File file;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private File taskFile; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Open the file
			taskFile = new File(open);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				file = taskFile;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
