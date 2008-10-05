package org.limewire.hello.base.user;

import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.time.Time;

public class Describe {
	
	// Number
	
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
	
	// Size
	
	/** Given a number of bytes, describe the size in kilobytes like "1,234 KB". */
	public static String size(long bytes) {
		if      (bytes == -1) return "";
		else if (bytes ==  0) return "0 KB";
		else                  return commas(((int)((bytes - 1) / 1024)) + 1) + " KB"; // Remove and add 1 to match Windows Explorer's Size column exactly
	}
	
	// Time
	
	/** Describe the given number of milliseconds with a String like "1 min 24 sec". */
	public static String time(long milliseconds) { return time(milliseconds, false); }
	/** Describe the given number of milliseconds with a String like "5 sec" or "10 sec", counting up in big steps. */
	public static String timeCoarse(long milliseconds) { return time(milliseconds, true); }
	/** Given a number of milliseconds, describe the length of time with a String like "1 min 24 sec". */
	private static String time(long milliseconds, boolean coarse) {
		
		// Compute the number of whole seconds, minutes, and hours in the given number of milliseconds
		long seconds = milliseconds / Time.second;
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
	
	// Speed

	/** Given a number of bytes transferred in a second, describe the speed in kilobytes per second like "2.24 KB/s". */
	public static String speed(int bytesPerSecond) {
		int i = (bytesPerSecond * 100) / 1024; // Compute the number of hundreadth kilobytes per second
		if      (i == 0)    return "";                                                                                      // Return "" instead of "0.00 KB/s"
		else if (i <    10) return "0.0" + i + " KB/s";                                                                     // 1 digit   "0.09 KB/s"
		else if (i <   100) return "0." + i + " KB/s";                                                                      // 2 digits  "0.99 KB/s"
		else if (i <  1000) return Text.start(Number.toString(i), 1) + "." + Text.clip(Number.toString(i), 1, 2) + " KB/s"; // 3 digits  "9.99 KB/s"
		else if (i < 10000) return Text.start(Number.toString(i), 2) + "." + Text.clip(Number.toString(i), 2, 1) + " KB/s"; // 4 digits  "99.9 KB/s"
		else                return commas(Text.chop(Number.toString(i), 2)) + " KB/s";                                      // 5 or more "999 KB/s" or "1,234 KB/s"
	}
}
