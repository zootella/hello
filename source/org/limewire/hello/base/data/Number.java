package org.limewire.hello.base.data;

import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.exception.MessageException;

public class Number {

	// -------- Number to data --------
	
	/** Convert n into size bytes, turns the number 0x0a0b0c0d into the 4 bytes 0a 0b 0c 0d. */
	public static Data data(int size, long n) { Bay bay = new Bay(); toBay(bay, size, n); return bay.data(); }
	/** Convert n into size bytes, turns the number 0x0a0b0c0d into the 4 bytes 0d 0c 0b 0a reversed for little endian encoding. */
	public static Data dataLittle(int size, long n) { Bay bay = new Bay(); toBayLittle(bay, size, n); return bay.data(); }

	/** Convert n into size bytes in bay, turns the number 0x0a0b0c0d into the 4 bytes 0a 0b 0c 0d. */
	public static void toBay(Bay bay, int size, long n) { numberToData(bay, size, n, false); }
	/** Convert n into size bytes in bay, turns the number 0x0a0b0c0d into the 4 bytes 0d 0c 0b 0a reversed for little endian encoding. */
	public static void toBayLittle(Bay bay, int size, long n) { numberToData(bay, size, n, true); }
	
	/**
	 * Convert a number into data.
	 * 
	 * @param  bay    A Bay object this method will add size bytes to.
	 * @param  size   The number of bytes to write, 1 through 8.
	 * @param  n      The number to turn into data and add to bay.
	 * @param  little false to use big endian byte ordering, adding the most significant byte first.
	 *                true to use little endian byte ordering, adding the least significant byte first.
	 */
	private static void numberToData(Bay bay, int size, long n, boolean little) {

		// Make sure we are told to produce 1 through 8 bytes
		if (size < 1 || size > 8) throw new IllegalArgumentException();

		// Loop with height starts bits high through end bits high
		int start = little ? 0                : (8 * (size - 1));
		int end   = little ? (8 * (size - 1)) : 0;
		int step  = little ? 8                : -8;
		for (int height = start; height != end + step; height += step) {
			
			// Isolate the byte height bits high in n, and add it to bay
			bay.add((byte)(((0xffL << height) & n) >> height));
		}
	}
	
	// -------- Number to text --------
	
	/** Convert a number into a String, turns 5 into "5". */
	public static String toString(long n) { return Long.toString(n); }
	/** Convert a number into text in base 16, turns 10 into "a". */ 
	public static String toStringBase16(long n) { return Long.toHexString(n); }

	// -------- Data to number --------

	/** Convert data into an int and make sure it's minimum or bigger, turns the 4 bytes 0a 0b 0c 0d into the number 0x0a0b0c0d. */
	public static int toInt(Data d, int minimum) throws MessageException { int i = toInt(d); if (i < minimum) throw new MessageException(); return i; }
	/** Convert data into an int and make sure it's minimum or bigger, turns the 4 bytes 0d 0c 0b 0a into the number 0x0a0b0c0d reversed from little endian encoding. */
	public static int toIntLittle(Data d, int minimum) throws MessageException { int i = toIntLittle(d); if (i < minimum) throw new MessageException(); return i; }
	/** Convert data into a long and make sure it's minimum or bigger, turns the 4 bytes 0a 0b 0c 0d into the number 0x0a0b0c0d. */
	public static long toLong(Data d, long minimum) throws MessageException { long l = toLong(d); if (l < minimum) throw new MessageException(); return l; }
	/** Convert data into a long and make sure it's minimum or bigger, turns the 4 bytes 0d 0c 0b 0a into the number 0x0a0b0c0d reversed from little endian encoding. */
	public static long toLongLittle(Data d, long minimum) throws MessageException { long l = toLongLittle(d); if (l < minimum) throw new MessageException(); return l; }

	/** Convert data into an int and make sure its minimum through maximum, turns the 4 bytes 0a 0b 0c 0d into the number 0x0a0b0c0d. */
	public static int toInt(Data d, int minimum, int maximum) throws MessageException { int i = toInt(d); if (i < minimum || i > maximum) throw new MessageException(); return i; }
	/** Convert data into an int and make sure its minimum through maximum, turns the 4 bytes 0d 0c 0b 0a into the number 0x0a0b0c0d reversed from little endian encoding. */
	public static int toIntLittle(Data d, int minimum, int maximum) throws MessageException { int i = toIntLittle(d); if (i < minimum || i > maximum) throw new MessageException(); return i; }
	/** Convert data into a long and make sure its minimum through maximum, turns the 4 bytes 0a 0b 0c 0d into the number 0x0a0b0c0d. */
	public static long toLong(Data d, long minimum, long maximum) throws MessageException { long l = toLong(d); if (l < minimum || l > maximum) throw new MessageException(); return l; }
	/** Convert data into a long and make sure its minimum through maximum, turns the 4 bytes 0d 0c 0b 0a into the number 0x0a0b0c0d reversed from little endian encoding. */
	public static long toLongLittle(Data d, long minimum, long maximum) throws MessageException { long l = toLongLittle(d); if (l < minimum || l > maximum) throw new MessageException(); return l; }

	/** Convert data into an int, turns the 4 bytes 0a 0b 0c 0d into the number 0x0a0b0c0d. */
	public static int toInt(Data d) throws MessageException { return (int)dataToNumber(d, false); }
	/** Convert data into an int, turns the 4 bytes 0d 0c 0b 0a into the number 0x0a0b0c0d reversed from little endian encoding. */
	public static int toIntLittle(Data d) throws MessageException { return (int)dataToNumber(d, true); }
	/** Convert data into a long, turns the 4 bytes 0a 0b 0c 0d into the number 0x0a0b0c0d. */
	public static long toLong(Data d) throws MessageException { return dataToNumber(d, false); }
	/** Convert data into a long, turns the 4 bytes 0d 0c 0b 0a into the number 0x0a0b0c0d reversed from little endian encoding. */
	public static long toLongLittle(Data d) throws MessageException { return dataToNumber(d, true); }
	
	/**
	 * Convert data into a number.
	 * 
	 * @param  d                A Data object with bytes that are a number.
	 *                          d can have a size of 1 through 8 bytes.
	 * @param  little           false to use big endian byte ordering, making the first byte the most significant.
	 *                          true to use little endian byte ordering, making the first byte the least significant.
	 * @return                  The number, as a long.
	 * @throws MessageException data doesn't clip 1 through 8 bytes.
	 */
	private static long dataToNumber(Data d, boolean little) throws MessageException {
		try {
			
			// Make sure we are given 1 through 8 bytes
			if (d.size() < 1 || d.size() > 8) throw new MessageException();
			
			// The number we'll build up from data's bits, and return
			long n = 0;
			
			// Scan the data from the start index through the end index
			int start = little ? d.size() - 1 : 0;
			int end   = little ? 0            : d.size() - 1;
			int step  = little ? -1           : 1;
			for (int i = start; i != end + step; i += step) {
				
				// Shift the bits in n up 8 bits to make room, and copy in a byte from data
				n = (n << 8) | (d.get(i) & 0xffL);
			}
			
			// Return the number we made
			return n;
			
		} catch (ChopException e) { throw new CodeException(); } // There is a mistake in the code in this try block
	}

	// -------- Text to number --------

	/** Convert text into an int and make sure it's minimum or bigger, turns "5" into 5, throw MessageException if s is "" or not numerals. */
	public static int toInt(String s, int minimum) throws MessageException { int i = toInt(s); if (i < minimum) throw new MessageException(); return i; }
	/** Convert base 16 numerals into an int and make sure it's minimum or bigger, turns "a" or "A" into 10, throw MessageException if s is "" or not numerals. */
	public static int toIntBase16(String s, int minimum) throws MessageException { int i = toIntBase16(s); if (i < minimum) throw new MessageException(); return i; }
	/** Convert text into a long and make sure it's minimum or bigger, turns "5" into 5, throw MessageException if s is "" or not numerals. */
	public static long toLong(String s, long minimum) throws MessageException { long l = toLong(s); if (l < minimum) throw new MessageException(); return l; }
	/** Convert base 16 numerals into a long and make sure it's minimum or bigger, turns "a" into 10, throw MessageException if s is "" or not numerals. */
	public static long toLongBase16(String s, long minimum) throws MessageException { long l = toLongBase16(s); if (l < minimum) throw new MessageException(); return l; }

	/** Convert text into an int and make sure it's minimum through maximum, turns "5" into 5, throw MessageException if s is "" or not numerals. */
	public static int toInt(String s, int minimum, int maximum) throws MessageException { int i = toInt(s); if (i < minimum || i > maximum) throw new MessageException(); return i; }
	/** Convert base 16 numerals into an int and make sure it's minimum through maximum, turns "a" or "A" into 10, throw MessageException if s is "" or not numerals. */
	public static int toIntBase16(String s, int minimum, int maximum) throws MessageException { int i = toIntBase16(s); if (i < minimum || i > maximum) throw new MessageException(); return i; }
	/** Convert text into a long and make sure it's minimum through maximum, turns "5" into 5, throw MessageException if s is "" or not numerals. */
	public static long toLong(String s, long minimum, long maximum) throws MessageException { long l = toLong(s); if (l < minimum || l > maximum) throw new MessageException(); return l; }
	/** Convert base 16 numerals into a long and make sure it's minimum through maximum, turns "a" into 10, throw MessageException if s is "" or not numerals. */
	public static long toLongBase16(String s, long minimum, long maximum) throws MessageException { long l = toLongBase16(s); if (l < minimum || l > maximum) throw new MessageException(); return l; }

	/** Convert text into an int, turns "5" into 5, throw MessageException if s is "" or not numerals. */
	public static int toInt(String s) throws MessageException { return (int)textToNumber(s, 10); }
	/** Convert base 16 numerals into an int, turns "a" or "A" into 10, throw MessageException if s is "" or not numerals. */
	public static int toIntBase16(String s) throws MessageException { return (int)textToNumber(s, 16); }
	/** Convert text into a long, turns "5" into 5, throw MessageException if s is "" or not numerals. */
	public static long toLong(String s) throws MessageException { return textToNumber(s, 10); }
	/** Convert base 16 numerals into a long, turns "a" into 10, throw MessageException if s is "" or not numerals. */
	public static long toLongBase16(String s) throws MessageException { return textToNumber(s, 16); }

	/**
	 * Convert text into a number.
	 * 
	 * @param  s                Text numerals in a String, like "0", "-3", or "a"
	 * @param  base             The base to use, like 10 or 16
	 * @return                  The number, as a long
	 * @throws MessageException We can't read the text as a number
	 */
	private static long textToNumber(String s, int base) throws MessageException {
		try {
			return Long.parseLong(s, base);
		} catch (NumberFormatException e) { throw new MessageException(); }
	}
}
