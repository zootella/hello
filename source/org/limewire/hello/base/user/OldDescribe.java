package org.limewire.hello.base.user;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Text;

// compose text for the user
public class OldDescribe {
	
	// -------- Factory settings --------

	/**
	 * 250 milliseconds, wait a quarter second before updating progress on the screen.
	 * Any faster, and the updates flicker faster than the user can read them.
	 */
	public static final int delay = 250;
	
	/**
	 * 5 seconds, update a growing or shrinking time every 5 seconds in the user interface.
	 * Any faster, and the updates become a distracting countdown.
	 */
	public static final int wait = 5;

	// -------- Number --------
	
	/** Given a number and a name like number(5, "object"), compose a String like "5 objects". */
	public static String number(int number, String name) {
		if      (number == 0) return "0 " + name + "s";                 // "0 names"
		else if (number == 1) return "1 " + name;                       // "1 name"
		else                  return commas(number) + " " + name + "s"; // "2 names" and up
	}

	/** Describe the given number like 1234 as a String like "1,234". */
	public static String commas(long l) { return commas(Number.toString(l)); }
	/** Insert commas in the given String, turn "1234" into "1,234". */
	public static String commas(String s) {
		String done = "";
		while (s.length() > 3) { // Loop, chopping groups of 3 characters off the end of s 
			done = "," + Text.end(s, 3) + done;
			s = Text.chop(s, 3);
		}
		return s + done;
	}

	// -------- Size --------
	
	/** Given a number of bytes, describe the size in kilobytes like "1,234 KB". */
	public static String size(long bytes) {
		if      (bytes == -1) return "";
		else if (bytes ==  0) return "0 KB";
		else                  return commas(((int)((bytes - 1) / 1024)) + 1) + " KB"; // Remove and add 1 to match Windows Explorer's Size column exactly
	}
	
	/**
	 * Compose text for the user that describes the size and progress of a download, like "12% 145 KB/1,154 KB".
	 * 
	 * @param saved The number of bytes saved, 0 if none saved yet
	 * @param size  The total number of bytes in the file, -1 if unknown
	 * @return      Descriptive text for the user like "12% 145 KB/1,154 KB"
	 */
	public static String sizePercent(long saved, long size) {

		// Size unknown
		if (size == -1) {
			
			// If we've saved at least 1 byte, compose text like "Saved 145 KB"
			if (saved == 0) return "";
			else            return "Saved " + OldDescribe.size(saved);

		// We know the size
		} else {

			// Nothing saved yet
			if (saved == 0) {
				
				// Compose text like "1,154 KB", showing the total size
				return OldDescribe.size(size);

			// Both saved and size must be 1 or more
			} else {

				// Compose text like "12% 145 KB/1,154 KB"
				int percent = (int)((saved * 100) / size); // 0 if less than a percent, 100 if all done
				if (percent == 0) return OldDescribe.size(saved) + "/" + OldDescribe.size(size); // Omit 0%
				else if (percent == 100) return OldDescribe.size(size); // Omit 100% and the saved size
				else return percent + "% " + OldDescribe.size(saved) + "/" + OldDescribe.size(size);
			}
		}
	}

	// -------- Time --------
	//TODO have these all take milliseconds, use Time.seconds and those
	
	/** Describe the given number of seconds with a String like "1 min 24 sec". */
	public static String timeSeconds(long seconds) { return time(seconds * 1000, false); }
	/** Describe the given number of seconds with a String like "5 sec" or "10 sec", counting up in big steps. */
	public static String timeSecondsCoarse(long seconds) { return time(seconds * 1000, true); }
	/** Describe the given number of milliseconds with a String like "1 min 24 sec". */
	public static String timeMilliseconds(long milliseconds) { return time(milliseconds, false); }
	/** Describe the given number of milliseconds with a String like "5 sec" or "10 sec", counting up in big steps. */
	public static String timeMillisecondsCoarse(long milliseconds) { return time(milliseconds, true); }
	/** Given a number of milliseconds, describe the length of time with a String like "1 min 24 sec". */
	private static String time(long milliseconds, boolean coarse) {
		
		// Compute the number of whole seconds, minutes, and hours in the given number of milliseconds
		long seconds = milliseconds / 1000;
		if (coarse && seconds > 5) seconds -= seconds % 5; // If coarse and above 5, round down to the nearest multiple of 5
		long minutes = seconds / 60;
		long hours = minutes / 60;
		
		// Compose and return a String that describes that amount of time
		if      (seconds <    60) return seconds + " sec";                                        // "0 sec" to "59 sec"
		else if (seconds <   600) return minutes + " min " + (seconds - (minutes * 60)) + " sec"; // "1 min 0 sec" to "9 min 59 sec"
		else if (seconds <  3600) return minutes + " min";                                        // "10 min" to "59 min"
		else if (seconds < 36000) return hours + " hr " + (minutes - (hours * 60)) + " min";      // "1 hr 0 min" to "9 hr 59 min"
		else                      return commas(hours) + " hr";                                   // "10 hr" and up
	}

	// -------- Speed --------
	
	/** Given a number of bytes transferred in a second, describe the speed in kilobytes per second like "2.24 KB/s". */
	public static String speed(int bytesPerSecond) {
		int i = (bytesPerSecond * 100) / 1024; // Compute the number of hundreadth kilobytes per second
		if      (i == 0)    return "";                                                                                       // Return "" instead of "0.00 KB/s"
		else if (i <    10) return("0.0" + i + " KB/s");                                                                     // 1 digit   "0.09 KB/s"
		else if (i <   100) return("0." + i + " KB/s");                                                                      // 2 digits  "0.99 KB/s"
		else if (i <  1000) return(Text.start(Number.toString(i), 1) + "." + Text.clip(Number.toString(i), 1, 2) + " KB/s"); // 3 digits  "9.99 KB/s"
		else if (i < 10000) return(Text.start(Number.toString(i), 2) + "." + Text.clip(Number.toString(i), 2, 1) + " KB/s"); // 4 digits  "99.9 KB/s"
		else                return commas(Text.chop(Number.toString(i), 2)) + " KB/s";                                       // 5 or more "999 KB/s" or "1,234 KB/s"
	}
	
	/**
	 * Predict the number of seconds it will take to get the rest of a file.
	 * Never says 0 seconds, always rounds up to say 1 or more seconds.
	 * 
	 * @param saved    The number of bytes saved, 0 if none saved yet.
	 * @param size     The total number of bytes in the file, -1 if unknown.
	 * @param speed    Our current speed, in bytes/second, 0 if stopped or unknown.
	 * @param previous The arrival time the previous call to this method predicted, in seconds.
	 *                 0 if we've never reported an arrival time before.
	 * @return         The predicted arrival time in seconds, 1 or more seconds.
	 *                 -1 if we can't predict.
	 */
	public static int arrival(long saved, long size, int speed, int previous) {

		// Check the speed and size
		if (speed == 0) return -1; // No speed, we can't predict
		if (size == -1) return -1; // Unknown size, we can't predict

		// Calculate seconds remaining
		int a = (int)((size - saved) / speed) + 1; // Add 1 to round up, and to never report 0 seconds

		// Keep the arrival time from flickering in the user interface
		if (previous == 0 ||       // If we've never reported an arrival before, or
			a < previous  ||       // Our new prediction is sooner, a step in the right direction, or
			a > previous + wait) { // Our new prediction is more than 5 seconds later

			// Report our new prediction
			previous = a;
		}

		// Report our prediction
		return previous;
	}
}
