package org.limewire.hello.base.time;

import org.limewire.hello.base.user.Describe;

public class Progress {
	
	// Make

	/** Make a Progress that will track how fast done reaches size. */
	public Progress(String verb, String verbing, String verbed) {
		this.verb = verb;
		this.verbing = verbing;
		this.verbed = verbed;
		size = -1; // Size unknown at the start
	}
	/** Shows up in "... to verb". */
	private final String verb;
	/** Shows up in "Verbing at ...". */
	private final String verbing;
	/** Shows up in "Verbed 1 KB". */
	private final String verbed;
	
	// Set and look

	/** Tell this Progress we've done m more. */
	public void add(long m) {
		if (m < 0) throw new IndexOutOfBoundsException();
		done(done + m);
	}

	/** How much of this Progress is done, 0 or more. */
	public long done() { return done; }
	private long done;
	/** Set how much of this Progress is done, 0 or more. */
	public void done(long d) {
		if (d < 0)                    throw new IndexOutOfBoundsException();
		if (hasSize() && done > size) throw new IndexOutOfBoundsException();
		long gain = d - done;                       // See if we got closer to our goal
		if (gain > 0) {                             // We did
			if (speed == null) speed = new Speed(); // Make our Speed if we don't already have one
			speed.add((int)gain);                   // Tell it the distance we jumped forward
		}
		done = d;
	}
	
	/** true once this Progress size has been set to 1 or more, false if never set, false if set to 0. */
	public boolean hasSize() { return size > 0; }
	/** The total size goal of this Progress, 0 or more, -1 unknown. */
	public long size() { return size; }
	private long size;
	/** Set the total size goal of this Progress once before setting done, or never, 0 or more. */
	public void size(long s) {
		if (s < 0) throw new IndexOutOfBoundsException();
		if (hasSize() || done != 0) throw new IllegalStateException();
		size = s;
	}

	/** How much of this Progress remains, size() - done(), -1 unknown. */
	public long remain() {
		if (!hasSize()) return -1;
		long r = size - done;
		if (r < 0) return -1;
		return r;
	}

	/** Our Speed object, null if paused or haven't moved yet. */
	private Speed speed;

	/** Pause this Progress so it forgets its speed, you don't have to unpause it. */
	public void pause() {
		speed = null; // Throw out our Speed and arrival prediction
		arrive = 0;
	}

	// Calculate

	/** The Percent done this Progress is, 0 through 100, rounds down to only register a percent when complete, -1 unknown. */
	public int percent() {
		if (!hasSize() || done > size) return -1;
		return (int)((done * 100) / size);
	}
	
	/** How fast we're progressing, 0 or more units/second. */
	public int speed() {
		if (speed == null) return 0;
		else return speed.speed();
	}

	/** The number of seconds we predict it will take for this Progress to complete, 1 or more, -1 can't predict. */
	public int arrive() {

		// Get our current speed and distance remaining
		int s = speed();
		long r = remain();
		if (s == 0 || r == -1) return -1;
		
		// Calculate seconds remaining
		int a = (int)(r / s) + 1; // Add 1 to round up, and to never report 0 seconds
		
		// Keep the arrival time from flickering in the user interface
		if (arrive == 0 || // If we've never reported an arrival before, or
			a < arrive  || // Our new prediction is sooner, a step in the right direction, or
			(a > arrive * (fraction + 1) / fraction) && // Our new prediction is more than 1/10th later and
			(a > arrive + wait))                        // Our new prediction is more than 5 seconds later
			arrive = a; // Set our new prediction

		// Report our prediction
		return arrive;
	}
	/** The arrive time we previously predicted, 1 or more seconds, 0 before we make a prediction. */
	private int arrive;
	/** 10, only extend a previous prediction if it's more than 1/10th beyond our previous prediction. */
	private static final int fraction = 10;
	/** 5 seconds, only extend a previous prediction if it's more than 5 seconds beyond our previous prediction. */
	private static final int wait = 5;

	// Describe

	/** Describe our status like "12 sec at 2.34 KB/s to verb", "Verbing at 2.34 KB/s", or just "". */
	public String describeStatus() {
		int arrive = arrive(); // -1 can't predict
		int speed  = speed();  //  0 no speed
		if (arrive != -1 && speed > 0) return Describe.time(arrive * Time.second) + " at " + Describe.speed(speed) + " to " + verb; //  "12 sec at 2.34 KB/s to verb"
		else if            (speed > 0) return                             verbing + " at " + Describe.speed(speed);                 // "Verbing at 2.34 KB/s"
		else                           return "";                                                                                   // ""
	}

	/** Describe our size like "12% 145 KB/1,154 KB", "Verbed 145 KB", or just "". */
	public String describeSize() {
		int  percent = percent(); // -1 unknown, 0 not the first percent yet, 100 done
		long done    = done();    //  0 nothing yet
		long size    = size();    // -1 unknown
		if (size != -1 && percent != -1) {                                                                    // Size and percent known
			if      (done    ==   0) return                                              Describe.size(size); //            "1,154 KB" Nothing done yet
			else if (percent ==   0) return                  Describe.size(done) + "/" + Describe.size(size); //     "145 KB/1,154 KB" Not 1 percent yet
			else if (percent == 100) return                                              Describe.size(size); //            "1,154 KB" All done
			else                     return percent + "% " + Describe.size(done) + "/" + Describe.size(size); // "12% 145 KB/1,154 KB" 1 through 99 percent
		} else if (size != -1) {                                                                              // Just size
			                         return                                              Describe.size(size); //            "1,154 KB" Just size
		} else {                                                                                              // Just done
			if      (done     >   0) return   verbed + " " + Describe.size(done);                             //         "Verbed 1 KB" Counting up done
			else                     return "";                                                               //                    "" No size, done, or percent
		}
	}
}
