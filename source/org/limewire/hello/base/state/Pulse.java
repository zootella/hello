package org.limewire.hello.base.state;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class Pulse extends Close {
	
	// Make

	/** Make a Pulse that will call the given receive() method every delay milliseconds. */
	public Pulse(Receive receive, int delay) {
		this.receive = receive;
		if (delay < Delay.time) delay = Delay.time; // Make sure delay isn't too fast
		timer = new Timer(delay, new MyActionListener());
		timer.setRepeats(true);
		timer.start();
	}
	
	/** A link to the receive() method we call. */
	private final Receive receive;
	/** Our Timer set to repeat. */
	private Timer timer;

	/** Close this Pulse so it never calls receive() again. */
	public void close() {
		if (already()) return;
		timer.stop(); // Stop and discard timer, keeping it might prevent the program from closing
		timer = null;
	}
	
	// Receive

	// When timer goes off, Java calls this actionPerformed() method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (closed()) return; // Don't let a closed Pulse call receive()
			receive.receive();    // Call our given receive() method
		}
	}
}
