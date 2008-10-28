package org.limewire.hello.spin;

import javax.swing.SwingUtilities;

import org.limewire.hello.spin.utility.Speed;
import org.limewire.hello.spin.utility.Spin;

/** A Spin object that calls SwingUtilities.invokeLater() each time. */
public class InvokeSpin extends Spin {
	
	/** Say what kind of Spin this is. */
	public String title() { return "Invoke"; }

	/** Make a new object to spin quickly and show how fast. */
	public InvokeSpin() {
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
			
			// Send an event to spin again
			SwingUtilities.invokeLater(new Event());
		}
	}
}
