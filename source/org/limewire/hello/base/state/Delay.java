package org.limewire.hello.base.state;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class Delay extends Close {
	
	// Make
	
	/** Make a Delay that will call receive() once shortly after the first of a bunch of send() calls. */
	public Delay(Receive receive) { this(receive, time); }
	/** Make a Delay that will call receive() once delay milliseconds after the first of a bunch of send() calls. */
	public Delay(Receive receive, int delay) {
		this.receive = receive;
		if (delay < time) delay = time; // Make sure delay isn't too fast
		timer = new Timer(delay, new MyActionListener());
		timer.setRepeats(false);
	}

	/** A link to the receive() method we call. */
	private final Receive receive;
	/** Our Timer that doesn't repeat. */
	private Timer timer;

	/** Close this Delay so it never calls receive() again. */
	public void close() {
		if (already()) return;
		timer.stop(); // Stop and discard timer, keeping it might prevent the program from closing
		timer = null;
	}
	
	// Send and receive

	/**
	 * Have this Update call the receive() method you gave it in a separate event after a short delay.
	 * Call send() several times in the delay time, and receive() will only happen once.
	 */
	public void send() {
		if (closed()) return; // Do nothing once closed
		if (set) return;      // We're already set to go off
		timer.start();        // Set the timer to go off once after its delay
		set = true;
	}
	
	/** true when we've set timer to go off, and it hasn't yet. */
	private boolean set;

	// When timer goes off, Java calls this actionPerformed() method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (closed()) return; // Don't let a closed Delay call receive()
			set = false;          // Let the next call to send() go through
			receive.receive();    // Call our given receive() method
		}
	}

	// Preset

	/** 200 milliseconds, 1/5th of a second, the minimum and default delay time. */
	public static final int time = 200;
}
