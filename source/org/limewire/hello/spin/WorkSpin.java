package org.limewire.hello.spin;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.limewire.hello.spin.utility.Speed;
import org.limewire.hello.spin.utility.Spin;

/** A Spin object that runs a SwingWorker each time. */
public class WorkSpin extends Spin {
	
	/** Say what kind of Spin this is. */
	public String title() { return "Work"; }

	/** Make a new object to spin quickly and show how fast. */
	public WorkSpin() {
		speed = new Speed();
		
		// Send the first event that starts us spinning
		SwingUtilities.invokeLater(new Event());
	}
	
	/** Our Speed object that keeps track of how fast we're spinning. */
	public Speed speed() { return speed; }
	private final Speed speed;
	
	/** Mark this closed to stop it spinning. */
	public void close() { if (already()) return; }

	/** Receive an event. */
	private class Event implements Runnable {
		public void run() {
			if (closed()) return;
			
			// Count we've spun another time
			speed.add(1);
			
			// Have a worker thread call doInBackground() now
			(new MySwingWorker()).execute();
		}
	}
	
	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		
		// A worker thread will call this method
		public Void doInBackground() {
			return null;
		}
		
		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			
			// Send an event to spin again
			SwingUtilities.invokeLater(new Event());
		}
	}
}
