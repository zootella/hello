package org.limewire.hello.base.state;

import javax.swing.SwingUtilities;

/** Make a Task to run some code in a separate thread. */
public class Task extends Close {
	
	// Make

	/** Make a Task to have a separate thread run the code in body now. */
	public Task(TaskBody body) {
		this.body = body;
		thread = new Thread(new ThreadRun(), "Task"); // Name thread "Task"
		thread.setDaemon(true); // Let the program close even if thread is still running
		thread.start(); // Have thread call ThreadRun.run() below now
	}

	/** A link to the code this Task will run. */
	private final TaskBody body;
	/** Our Thread we make, run, and let exit. */
	private Thread thread;
	
	/** Interrupt this Task's Thread. */
	public void close() {
		if (already()) return;
		if (thread != null) // If thread is running, have it throw an Exception
			thread.interrupt();
	}
	
	// Run
	
	// When the constructor makes thread above, thread calls the run() method here
	private class ThreadRun implements Runnable {
		public void run() {
			try {
				body.thread();                          // Call the code we were given
			} catch (Exception e) { exception = e; }    // Catch and save any Exception it throws
			SwingUtilities.invokeLater(new EventRun()); // We're done, send an event
		}                                               // When thread exits run(), it closes
	}	
	
	/** The Exception body's code threw when thread ran it, null if none. */
	private Exception exception;

	// Soon after thread calls invokeLater() above, the normal event thread calls run() here
	private class EventRun implements Runnable {
		public void run() {
			if (closed()) return; // Do nothing once closed
			thread = null;        // thread is done and exited, null our reference to it
			close();              // Mark this Task closed
			body.done(exception); // Call the given done() method with the Exception we got
		}
	}
}
