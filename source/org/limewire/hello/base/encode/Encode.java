package org.limewire.hello.base.encode;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.exception.MessageException;


// document which methods are reversible, and which are one way
// which are readable by the user, and which are not
// which are good for data that is mostly ascii text
// which keep the same length, which grow, and by how much
// which always expand or contract by the same amount, and which grow depending on contents
// talk about String, Data, Bay, and StringBuffer, and the shortcut methods
// to throws CodeException, while from throws MessageException

/** Use TextEncode methods to convert data to and from text letters and numbers using base 16, 32, and 62 encoding. */
public class Encode {
	
	// -------- Shortcut methods --------

	/** Turn data into text using base 16, each byte will become 2 characters, "00" through "ff". */
	public static String toBase16(Data d) { StringBuffer b = new StringBuffer(); toBase16(b, d); return b.toString(); }
	/** Turn data into text using base 32, each 5 bits will become a character a-z and 2-7. */
	public static String toBase32(Data d) { StringBuffer b = new StringBuffer(); toBase32(b, d); return b.toString(); }
	/** Turn data into text using base 62, each 4 or 6 bits will become a character 0-9, a-z, and A-Z. */
	public static String toBase62(Data d) { StringBuffer b = new StringBuffer(); toBase62(b, d); return b.toString(); }
	
	/** Turn base 16-encoded text back into the data it was made from. */
	public static Data fromBase16(String s) throws MessageException { Bay bay = new Bay(); fromBase16(bay, s); return bay.data(); }
	/** Turn base 32-encoded text back into the data it was made from. */
	public static Data fromBase32(String s) throws MessageException { Bay bay = new Bay(); fromBase32(bay, s); return bay.data(); }
	/** Turn base 62-encoded text back into the data it was made from. */
	public static Data fromBase62(String s) throws MessageException { Bay bay = new Bay(); fromBase62(bay, s); return bay.data(); }
	
	/** Turn data into text by putting bytes that aren't characters in square braces in base 16, "a[b]c\r\n" becomes "a[[b]]c[0d0a]". */
	public static String box(Data d) { StringBuffer b = new StringBuffer(); box(b, d); return b.toString(); }
	/** Turn box-encoded text back into the data it was made from. */
	public static Data unbox(String s) throws MessageException { Bay bay = new Bay(); unbox(bay, s); return bay.data(); }
	/** Turn data into text like "hello--", striking out non-ASCII bytes with hyphens. */
	public static String strike(Data d) { StringBuffer b = new StringBuffer(); strike(b, d); return b.toString(); }

	/** Make a Data object with some data from base 16 text quoted in the code, like Encode.data("00ff00ff") to make those 4 bytes. */
	public static Data data(String s) { try { return fromBase16(s); } catch (MessageException e) { throw new CodeException(); } }

	// -------- Base 16, 32, and 62 encoding --------

	/** Turn data into text using base 16, each byte will become 2 characters, "00" through "ff". */
	public static void toBase16(StringBuffer b, Data d) {
		try {
			
			// Use 0-9 and a-f, 16 different characters, to describe the data
			String alphabet = "0123456789abcdef";
			
			// Loop through each byte in the data
			for (int i = 0; i < d.size(); i++) {
				
				// Encode the byte into 2 characters
				b.append(alphabet.charAt((d.get(i) & 0xff) >> 4)); // Shift right 4 bits to read just the first part 1001----
				b.append(alphabet.charAt((d.get(i) & 0xff) & 15)); // Mask with 15 1111 to read just the second part ----1001
			}
			
		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}

	/** Turn data into text using base 32, each 5 bits will become a character a-z and 2-7. */
	public static void toBase32(StringBuffer b, Data d) {
		try {

			// Use a-z and 2-7, 32 different characters, to describe the data
			String alphabet = "abcdefghijklmnopqrstuvwxyz234567"; // Base 32 encoding omits 0 and 1 because they look like uppercase o and lowercase L
			
			// Loop through the memory, encoding its bits into letters and numbers
			int byteIndex, bitIndex;                    // The bit index i as a distance in bytes followed by a distance in bits
			int pair, mask, code;                       // Use the data bytes a pair at a time, with a mask of five 1s, to read a code 0 through 31
			for (int i = 0; i < d.size() * 8; i += 5) { // Move the index in bits forward across the memory in steps of 5 bits
				
				// Calculate the byte and bit to move to from the bit index
				byteIndex = i / 8; // Divide by 8 and chop off the remainder to get the byte index
				bitIndex  = i % 8; // The bit index within that byte is the remainder
				
				// Copy the two bytes at byteIndex into pair
				pair = (d.get(byteIndex) & 0xff) << 8; // Copy the byte at byteindex into pair, shifted left to bring eight 0s on the right
				if (byteIndex + 1 < d.size()) pair |= (d.get(byteIndex + 1) & 0xff); // On the last byte, leave the right byte in pair all 0s
				
				// Read the 5 bits at i as a number, called code, which will be 0 through 31
				mask = 31 << (11 - bitIndex);   // Start the mask 11111 31 shifted into position      0011111000000000
				code = pair & mask;             // Use the mask to clip out just that portion of pair --10101---------
				code = code >> (11 - bitIndex); // Shift it to the right to read it as a number       -----------10101
				
				// Describe the 5 bits with a numeral or letter
				b.append(alphabet.charAt(code));
			}
			
		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}

	/** Turn data into text using base 62, each 4 or 6 bits will become a character 0-9, a-z, and A-Z. */
	public static void toBase62(StringBuffer b, Data d) {
		try {
			
			// Use 0-9, a-z and A-Z, 62 different characters, to describe the data
			String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			
			// Loop through the memory, encoding its bits into letters and numbers
			int i = 0;                 // The index in bits, from 0 through all the bits in the given data
			int byteIndex, bitIndex;   // The same index as a distance in bytes followed by a distance in bits
			int pair, mask, code;      // Use the data bytes a pair at a time, with a mask of six 1s, to read a code 0 through 63
			while (i < d.size() * 8) { // When the bit index moves beyond the memory, we're done
				
				// Calculate the byte and bit to move to from the bit index
				byteIndex = i / 8; // Divide by 8 and chop off the remainder to get the byte index
				bitIndex  = i % 8; // The bit index within that byte is the remainder
				
				// Copy the two bytes at byteIndex into pair
				pair = (d.get(byteIndex) & 0xff) << 8; // Copy the byte at byteindex into pair, shifted left to bring eight 0s on the right
				if (byteIndex + 1 < d.size()) pair |= (d.get(byteIndex + 1) & 0xff); // On the last byte, leave the right byte in pair all 0s
				
				// Read the 6 bits at i as a number, called code, which will be 0 through 63
				mask = 63 << (10 - bitIndex);   // Start the mask 111111 63 shifted into position     0011111100000000
				code = pair & mask;             // Use the mask to clip out just that portion of pair --101101--------
				code = code >> (10 - bitIndex); // Shift it to the right to read it as a number       ----------101101
				
				// Describe the 6 bits with a numeral or letter, 111100 is 60 and Y, if more than that use Z and move forward 4, not 6
				if (code < 61) { b.append(alphabet.charAt(code)); i += 6; } // 000000  0 '0' through 111100 60 'Y'
				else           { b.append(alphabet.charAt(61));   i += 4; } // 111101 61, 111110 62, and 111111 63 are 'Z', move past the four 1s
			}

		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}

	/** Turn base 16-encoded text back into the data it was made from. */
	public static void fromBase16(Bay bay, String s) throws MessageException {

		// Loop for each character in the text
		char c;       // The character we are converting into bits
		int code;     // The 4 bits the character gets turned into
		int hold = 0; // A place to hold bits from 2 characters until we have 8 bits and can write a byte
		for (int i = 0; i < s.length(); i++) {

			// Get a character from the text, and convert it into its code
			c = Character.toUpperCase(s.charAt(i));             // Accept uppercase and lowercase letters
			if      (c >= '0' && c <= '9') code = c - '0';      // '0'  0 0000 through '9'  9 1001
			else if (c >= 'A' && c <= 'F') code = c - 'A' + 10; // 'A' 10 1010 through 'F' 15 1111
			else throw new MessageException();                  // Invalid character

			// This is the first character in a pair
			if (i % 2 == 0) {

				// Shift the 4 bytes it means into the high portion of the byte, like 1000----
				hold = code << 4;

			// This is the second character in a pair
			} else {

				// Copy the 4 bits from the second character in the pair into the low portion of the byte, like ----1100
				hold |= code; // Use the bitwise or operator to assemble the entire byte, like 10001100

				// Add the byte we made
				bay.add((byte)hold);
			}
		}
	}

	/** Turn base 32-encoded text back into the data it was made from. */
	public static void fromBase32(Bay bay, String s) throws MessageException {

		// Loop for each character in the text
		char c;        // The character we are converting into bits
		int  code;     // The bits the character gets turned into
		int  hold = 0; // A place to hold bits from several characters until we have 8 and can write a byte
		int  bits = 0; // The number of bits stored in the right side of hold right now
		for (int i = 0; i < s.length(); i++) {

			// Get a character from the text, and convert it into its code
			c = Character.toUpperCase(s.charAt(i));             // Accept uppercase and lowercase letters
			if      (c >= 'A' && c <= 'Z') code = c - 'A';      // 'A'  0 00000 through 'Z' 25 11001
			else if (c >= '2' && c <= '7') code = c - '2' + 26; // '2' 26 11010 through '7' 31 11111
			else throw new MessageException();                  // Invalid character

			// Insert the bits from code into hold
			hold = (hold << 5) | code; // Shift the bits in hold to the left 5 spaces, and copy in code there
			bits += 5;                 // Record that there are now 5 more bits being held

			// If we have enough bits in hold to write a byte
			if (bits >= 8) {

				// Move the 8 leftmost bits in hold to our Bay object
				bay.add((byte)(hold >> (bits - 8)));
				bits -= 8; // Remove the bits we wrote from hold, any extra bits there will be written next time
			}
		}
	}

	/** Turn base 62-encoded text back into the data it was made from. */
	public static void fromBase62(Bay bay, String s) throws MessageException {

		// Loop for each character in the text
		char c;        // The character we are converting into bits
		int  code;     // The bits the character gets turned into
		int  hold = 0; // A place to hold bits from several characters until we have 8 and can write a byte
		int  bits = 0; // The number of bits stored in the right side of hold right now
		for (int i = 0; i < s.length(); i++) {

			// Get a character from the text, and convert it into its code
			c = s.charAt(i);
			if      (c >= '0' && c <= '9') code = c - '0';      // '0'  0 000000 through '9'  9 001001
			else if (c >= 'a' && c <= 'z') code = c - 'a' + 10; // 'a' 10 001010 through 'z' 35 100011
			else if (c >= 'A' && c <= 'Y') code = c - 'A' + 36; // 'A' 36 100100 through 'Y' 60 111100
			else if (c == 'Z')             code = 61;           // 'Z' indicates 61 111101, 62 111110, or 63 111111 are next, we will just write four 1s
			else throw new MessageException();                  // Invalid character

			// Insert the bits from code into hold
			if (code == 61) { hold = (hold << 4) | 15;   bits += 4; } // Insert 1111 for 'Z'
			else            { hold = (hold << 6) | code; bits += 6; } // Insert 000000 for '0' through 111100 for 'Y'

			// If we have enough bits in hold to write a byte
			if (bits >= 8) {

				// Move the 8 leftmost bits in hold to our Bay object
				bay.add((byte)(hold >> (bits - 8)));
				bits -= 8; // Remove the bits we wrote from hold, any extra bits there will be written next time
			}
		}
	}

	// -------- Box encoding --------

	/** Turn data into text by putting bytes that aren't characters in square braces in base 16, "a[b]c\r\n" becomes "a[[b]]c[0d0a]". */
	public static void box(StringBuffer b, Data d) {
		try {

			// d is mostly text characters
			if (text(d)) {
				Data data = d.copy();    // Copy d to remove what we've encoded from data
				while (data.hasData()) { // Stop when data is empty
					byte y = data.first();
					if      (y == '[') { b.append("[["); data.remove(1); } // Turn "[" into "[["
					else if (y == ']') { b.append("]]"); data.remove(1); } // Turn "]" into "]]"
					else if (text(y)) b.append(data.cut(count(data, true)).toString()); // Bytes that are text characters don't change
					else { // Encode other bytes into base 16 in square braces
						b.append('[');
						toBase16(b, data.cut(count(data, false)));
						b.append(']');
					}
				}

			// d is mostly data bytes
			} else {
				b.append('['); // Encode it all into a single block in square braces
				toBase16(b, d);
				b.append(']');
			}

		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}
	
	/** Turn box-encoded text back into the data it was made from. */
	public static void unbox(Bay bay, String s) throws MessageException {
		try {

			// Move i down s, stopping when it reaches the end
			int i = 0;
			while (i != s.length()) {
				char c = s.charAt(i); // Get the character at i

				// i is at "[["
				if (c == '[') {
					if (s.charAt(i + 1) == '[') {
						bay.add((byte)'['); // Add "[" and move past the "[["
						i += 2;

					// i is at the start of a base 16 encoded block like "[0a0d]"
					} else {
						i++;                                     // Move i past the opening "["
						int j = i;                               // Start j there
						while (s.charAt(j) != ']') j++;          // Move j to the closing "]"
						fromBase16(bay, Text.clip(s, i, j - i)); // Base 16 decode the contents into bay
						i = j + 1;                               // Move i beyond the block
					}

				// i is at "]]"
				} else if (c == ']') {
					if (s.charAt(i + 1) == ']') {
						bay.add((byte)']'); // Add "]" and move past the "]]"
						i += 2;

					// The next character has to be the second "]" in "]]"
					} else {
						throw new MessageException();
					}

				// i is at a character like "a"
				} else {
					bay.add((byte)c); // Add it and move past it
					i++;
				}
			}

		// If we didn't have enough characters, like "hello[00", throw a MessageException
		} catch (IndexOutOfBoundsException e) { throw new MessageException(); }
	}

	// -------- Print data for the user, showing the text bytes it contains --------

	/** Turn data into text like "hello--", striking out non-text bytes with hyphens. */
	public static void strike(StringBuffer b, Data d) {
		try {
			for (int i = 0; i < d.size(); i++) {
				byte y = d.get(i);              // Loop for each byte of data y in d
				if (text(y)) b.append((char)y); // If it's " " through "~", include it in the text
				else         b.append('-');     // Otherwise, show a "-" in its place
			}
		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}
	
	// -------- Determine if a byte is a text character " " through "~" --------

	/** Count how many bytes at the start of d are text characters, or false to count data bytes. */
	private static int count(Data d, boolean text) {
		try {
			int i = 0;
			while (i < d.size()) {
				byte y = d.get(i);
				if (y == '[' || y == ']') break; // Stop at "[" or "]" when counting safe or unsafe bytes
				if (text ? !text(y) : text(y)) break;
				i++;
			}
			return i;
		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}

	/** true if d is mostly text characters, false to encode it all as data. */
	private static boolean text(Data d) {
		try {
			int text = 0; // The number of text bytes in d
			int data = 0; // The number of data bytes in d
			for (int i = 0; i < d.size(); i++) {
				byte y = d.get(i);   // Loop for each byte of data y in d
				if (text(y)) text++; // It's a text byte, count it
				else         data++; // It's a data byte, count it
			}
			return data == 0 || text > data; // Encode as text if no data, or more than half text
		} catch (ChopException e) { throw new CodeException(); } // The index can't go out of bounds
	}

	/** true if byte y is a text character " " through "~", false to encode y as data. */
	private static boolean text(byte y) {
		return y >= ' ' && y <= '~';
	}
}
