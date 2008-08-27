package org.limewire.hello.base.time;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Update {

	// -------- Make and close --------

	/**
	 * Make an Update to find out when things below have changed.
	 * 
	 * @param receive Your UpdateReceive object with the receive() method this new Update will call
	 */
	public Update(UpdateReceive receive) {
		this.receive = receive; // Save the given link back up to our owner's receive() method
		spin = new Spin();      // Make our Spin that will detect if we start going too fast
	}

	/** Close this Update so it never calls receive() again. */
	public void close() {
		if (closed) return; // Only close once
		closed = true;
		timer(0, false); // Stop and discard our Timer, keeping it might prevent the program from closing
	}
	
	// -------- Behavior --------

	/**
	 * Set this Update to normal default behavior.
	 * Call send() several times, and it will call receive() once soon and separately afterwards.
	 */
	public void behaveNormal() {
		if (closed) return; // Do nothing once closed
		timer(0, false);    // Stop and discard our Timer
	}

	/**
	 * Set this Update to grouping behavior.
	 * Call send() several times in delay milliseconds, and it will call receive() once after the delay.
	 */
	public void behaveGroup(int delay) {
		if (closed) return;  // Do nothing once closed
		timer(delay, false); // Make a Timer that will go off once delay after we set it
	}

	/**
	 * Set this Update to pulsing behavior.
	 * It will call receive() after every delay milliseconds, calling send() won't do anything.
	 */
	public void behavePulse(int delay) {
		if (closed) return; // Do nothing once closed
		timer(delay, true); // Make a Timer that will go off every delay milliseconds
	}

	// -------- Internal parts and state --------
	
	/** A link back up to our owner's UpdateReceive object that has the receive() method we call. */
	private UpdateReceive receive;
	
	/** Our Spin object that detects if we go too fast. */
	private Spin spin;
	
	/** true if we expect our Runnable to get called. */
	private boolean expectRun;
	
	/** true if we expect our Timer to go off. */
	private boolean expectTimer;

	/** true if we're pulsing, sending a notification every delay milliseconds. */
	private boolean pulse;
	
	/** true when this Update is closed, and will never call our owner's receive() method again. */
	private boolean closed;
	
	// -------- Timer and delay --------

	/**
	 * Set, reset, or discard our Timer.
	 * 
	 * @param delay The delay in milliseconds, or 0 to not have a Timer
	 * @param pulse true to set our Timer to pulse repeatedly
	 */
	private void timer(int delay, boolean pulse) {

		// If we have a Timer, get rid of it
		if (timer != null) {
			timer.stop();        // Stop it, have it not go off if it was set
			timer = null;        // Discard it and record that we don't have a Timer anymore
			expectTimer = false; // We don't expect the Timer to go off
			this.pulse = false;  // We're not pulsing
		}

		// If requested, make and setup a Timer
		if (delay != 0) {
			if (delay < this.delay) delay = this.delay;       // Make sure the delay isn't too fast
			timer = new Timer(delay, new MyActionListener()); // Sets initial and repeating delay
			timer.setRepeats(pulse);  // Set whether the Timer will go off once or repeatedly
			if (pulse) timer.start(); // Start the pulsing Timer
			expectTimer = pulse;      // If we're pulsing, we expect the Timer to go off
			this.pulse = pulse;       // Record if we're pulsing
		}
	}
	
	/** 200 milliseconds, 1/5th of a second, the minimum and default delay time for group and pulse. */
	public final int delay = 200;
	
	/** The Timer we use to group events and pulse, null when we don't need one. */
	private Timer timer;
	
	// -------- Send and receive --------

	/**
	 * Have this Update call its owner's receive() method soon in a separate event.
	 * You can call send() several times in a row, but receive() will only happen once.
	 * If this Update is set to group, receive() will happen once after a delay.
	 * If this Update is set to pulse, calling this send() method doesn't do anything.
	 */
	public void send() { send(false); }
	
	/**
	 * Have this Update call its owner's receive() method soon in a separate event.
	 * You can call sendNow() several times in a row, but receive() will only happen once.
	 * If this Update is set to group or pulse, receive() will happen once soon.
	 */
	public void sendNow() { send(true); }
	
	private void send(boolean now) {
		if (closed) return;    // Do nothing once closed
		if (expectRun) return; // If we're already set to go off, don't do anything
		
		if (timer == null || now) {                       // Normal behavior, or group or pulse with sendNow()
			SwingUtilities.invokeLater(new MyRunnable()); // Have Java call run() below separately and soon
			expectRun = true;
		} else if (!expectTimer) { // Group behavior with nothing set to go off
			timer.start();         // Set the timer to go off once after its delay
			expectTimer = true;
		}
	}

	// Soon after send() above calls SwingUtilities.invokeLater(), Java calls this run() method
	private class MyRunnable implements Runnable {
		public void run() {
			if (closed) return; // Don't let a closed Update call receive()
			expectRun = false;  // Let the next call to send() go through
			spin.count();       // If we've been doing this too frequently, throw a SpinException
			receive.receive();  // Call our owner's UpdateReceive receive() method
		}
	}

	// When our Timer goes off, Java calls this actionPerformed() method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (closed) return;              // Don't let a closed Update call receive()
			if (!pulse) expectTimer = false; // If we're not pulsing, record we're not set anymore
			spin.count();                    // If we've been doing this too frequently, throw a SpinException
			receive.receive();               // Call our owner's UpdateReceive receive() method
		}
	}
}
