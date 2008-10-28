package org.limewire.hello.spin.utility;

/** Compose text that describes a number to the user. */
public class Describe {
	
	// Number

	/** Describe the given number like 1234 as a String like "1,234". */
	public static String commas(long l) { return commas(Long.toString(l)); }
	/** Insert commas in the given String, turn "1234" into "1,234". */
	public static String commas(String s) {
		String done = "";
		while (s.length() > 3) { // Loop, chopping groups of 3 characters off the end of s 
			done = "," + end(s, 3) + done;
			s = chop(s, 3);
		}
		return s + done;
	}
	
	// Text
	
	/** Clip out the last size characters in the given String s, end(s, 3) is cccccccCCC. */
	public static String end(String s, int size) { return s.substring(s.length() - size, s.length()); }
	/** Chop the last size characters off the end of a given String, returning the start before them, chop(s, 3) is CCCCCCCccc. */
	public static String chop(String s, int size) { return s.substring(0, s.length() - size); }
}
