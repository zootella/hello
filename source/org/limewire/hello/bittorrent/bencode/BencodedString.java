package org.limewire.hello.bittorrent.bencode;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Split;
import org.limewire.hello.base.encode.Encode;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;


public class BencodedString extends Bencoded implements Comparable<BencodedString> {
	
	// -------- Hold our data --------
	
	/** The Data value of this BencodedString, ASCII text or binary data, copied into this BencodedString object. */
	private Data value;

	// -------- Make and parse --------

	/** Wrap the given Data into a new BencodedString object. */
	public BencodedString(Data d, boolean make) { value = d.copyData(); } // make keeps this separate from the parse constructor below
	/** Wrap the given String into a new BencodedString object. */
	public BencodedString(String s) { value = new Data(s); }
	
	/**
	 * Remove the data of one bencoded string from the start of d, and parse it into this new BencodedString object.
	 * 
	 * @param  d                A Data object with data like "5:hello" at the start
	 * @throws ChopException    If the end of the bencoded string is chopped off
	 * @throws MessageException If there is a mistake in the bencoded data
	 */
	public BencodedString(Data d) throws ChopException, MessageException {
		
		// Look for the ":" which separates the length from the data
		Split split = d.split((byte)':');
		if (!split.found) throw new ChopException(); // Not found, try again when we have more data

		// Convert the numerals before the ":" into a number
		int size = Number.toInt(split.before.toString(), 0); // Allow a length of 0
		
		// Copy the data after the ":" into this new BencodedString object
		value = split.after.start(size).copyData(); // Throws ChopException if split.after doesn't have size bytes
		
		// Remove the data we parsed
		d.remove(split.before.size() + 1 + size); // The 1 removes the ":"
	}

	// -------- Look at our data --------
	
	/** Get the data this BencodedString holds. */
	public Data getData() { return value; } // We can't name this method data(), because that method produces bencoded data
	/** Get the data this BencodedString holds, converted into a String. */
	public String getString() { return value.toString(); }
	
	// -------- Compare --------
	
	/**
	 * Compare this BencodedString object to another one to determine which should appear first in ascending sorted order.
	 * BencodedDictionary.map is a TreeMap that keeps its keys sorted, and its keys are BencodedString objects.
	 * Java will call this compareTo() method to see which of two BencodedString objects goes first in the TreeMap.
	 */
	public int compareTo(BencodedString b) {
		return value.compareTo(b.value); // Compare the data the BencodedString objects carry
	}

	/** Determine if this BencodedString holds exactly the same data as a given one. */
	public boolean equals(Object o) {
		return value.equals(((BencodedString)o).value);
	}

	// -------- Convert to data and show as text --------
	
	/**
	 * Convert this BencodedString object into bencoded data, and add it to bay.
	 * Adds data like "5:hello" to the Bay.
	 */
	public void toBay(Bay bay) {
		bay.add(value.size() + ":"); // Add the start that tells the length, like "5:"
		bay.add(value);              // Add the data after that
	}
	
	/** Compose a line of text that shows this BencodedString to the user, like "  hello\r\n". */
	public void toString(StringBuffer b, String indent) {
		b.append(indent);
		Encode.strike(b, value);
		b.append("\r\n");
	}
}
