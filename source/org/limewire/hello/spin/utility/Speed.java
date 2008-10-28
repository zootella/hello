package org.limewire.hello.spin.utility;

/** Keep track of how fast something is moving. */
public class Speed {

	// Make

	/** Make a new Speed object that can keep track of how fast you're transferring data. */
	public Speed() {
		array = new int[length]; // Allocate the array of total distances in recent time intervals
	}
	
	/** Record that we just traveled a distance, right now, in bytes. */
	public void add(int distance) {
		expire();                               // Set now and zero expired spots in our array
		array[(int)(now % length)] += distance; // Add the given distance to the total in the array spot we're in now
		last = now;                             // Record the interval number we most recently added data to
	}

	/** Find out how fast we're going right now, 0 or more bytes/second. */
	public int speed() {

		// Set now and zero expired spots in our array
		expire();
		
		// Step through the array from the oldest spot through the one we're in now
		boolean data = false; // True when we find data past 0s at the start
		int bytes = 0;        // The total of distances we find in array spots
		int spots = 0;        // The number of array spots we got distances from
		for (long interval = now - length + 1; interval <= now; interval++) {
			
			// Find the array index that the interval index wraps to
			int index = (int)(interval % length);

			// Don't include starting 0s in the average
			if (array[index] != 0) data = true;
			if (data) {                // Get information for the average
				bytes += array[index]; // Add the distance we traveled in this time interval
				spots++;               // Count one more time interval
			}
		}

		// Calculate the average speed in bytes/second
		if (spots == 0) return 0;                                        // No distance, report no speed
		else return (int)(((long)bytes * 1000) / ((long)spots * width)); // Multiply by 1000 because width is in milliseconds
	}

	// Inside
	
	/**
	 * An array that tells the total distances we've traveled during time intervals that have happened recently.
	 * For instance, the array might look like this:
	 * 
	 * {1024, 0, 2048, 0, 0, 0, 0, 0}
	 * 
	 * It has length elements.
	 * Each element represents a time interval of width milliseconds.
	 * In the first interval, we traveled 1024 bytes.
	 * In the second interval, no data was transferred.
	 * During the interval after that, we traveled a total of 2048 bytes.
	 */
	private int[] array;
	
	/** The number of spots in the array. */
	private static final int length = 50;
	/** The time, in milliseconds, we will spend in each spot. */
	private static final int width = 20;

	/*
	 * The last and now variables are measured in units of interval numbers.
	 * For the first width number of milliseconds of January 1970, we were in interval number 0.
	 * The interval number we're in now is (System.currentTimeMillis() / width).
	 * To find the array index dedicated to an interval number, use (intervalNumber % length).
	 */

	/** The interval number we most recently recorded a distance into. */
	private long last;
	/** The interval number we're in now. */
	private long now;

	/**
	 * Set the now time, and zero spots in our array that contain data from a previous scan across it.
	 * 
	 * As time goes on, we scan across our array over and over again.
	 * Before adding a new distance or averaging the speed, we must clear data from a previous scan.
	 * 
	 * First, suppose we last recorded a distance in array index 2, and now is still in that spot.
	 * 
	 * 2
	 * last
	 * now
	 * 
	 * In this case, expire() doesn't need to clear anything.
	 * Next, suppose it's a little later, and we're going to add a distance to 3.
	 * 
	 * 2      3
	 * last   now
	 *        clear
	 * 
	 * A previous scan across the array may have put data in 3.
	 * This is the first time we're entering 3 on this scan, so we have to clear the old data.
	 * To do that, expire() sets array[3] to 0.
	 * 
	 * It's also possible that nothing will happen during 3's time interval.
	 * Suppose data is added in 2, and then later in 4.
	 * 
	 * 2      3      4
	 * last          now
	 *        clear  clear
	 * 
	 * Both 3 and 4 are from the previous scan, so expire() clears them.
	 */
	private void expire() {

		// Set the interval number we're in now
		now = System.currentTimeMillis() / width;
		
		// Step through the array from one beyond last through now, stopping when we've covered the whole array
		for (long interval = last + 1; interval <= now && interval <= last + length; interval++) {

			// This spot in the array contains data from a previous scan, clear it
			array[(int)(interval % length)] = 0; // Start the new total distance in bytes at 0
		}
	}
}
