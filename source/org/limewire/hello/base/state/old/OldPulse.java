package org.limewire.hello.base.state.old;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import org.limewire.hello.all.Program;

public class OldPulse {

	// -------- The pulse delay --------
	
	/** A pulse happens every delay milliseconds. */
	private static int delay = 200; // With a delay of 200 milliseconds, 5 pulses happen each second

	// -------- Make the program's Pulse object, and close it --------

	/** Make the program's Pulse object, which keeps a timer that makes the pulses happen. */
	public OldPulse(Program program) {

		// Save a link back up to the Program object
		this.program = program;

		// Make a Swing Timer object, and set it up
		timer = new Timer(delay, new MyActionListener()); // Every delay milliseconds, it will call MyActionListener.actionPerformed()
		timer.setInitialDelay(0); // Have it go off right at the start, not after the first delay
		timer.start(); // Start it now
	}
	
	/** A link up to the Program object that the Pulse object is a part of. */
	private Program program;
	/** The Swing Timer object we use to pulse the program. */
	private Timer timer;

	/** Stop pulsing the program, and let the program close. */
	public void close() { timer.stop(); } // Otherwise, the Timer's thread can keep the program from closing

	// -------- The method Java calls --------
	
	// When the Timer goes off, Java calls this actionPerformed() method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

			// Pulse the program
			program.pulse(); // Ends up calling the pulse() method of every object in the program that has one
		}
	}

	// -------- Pulse next soon, not after the normal delay --------
	
	/**
	 * Have the next pulse happen soon, instead of after the normal delay.
	 * You can call soon() repeatedly, and the next pulse will happen soon, and only once.
	 */
	public void soon() { timer.restart(); } // Restart the timer with its initial delay of 0 milliseconds
}
