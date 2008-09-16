package org.limewire.hello.bittorrent.bencode;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Split;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;


public class BencodedInteger extends Bencoded {
	
	// -------- Hold our data --------
	
	/** The data of this BencodedInteger, a long integer number, positive, negative, or 0. */
	private long number;

	// -------- Make and parse --------
	
	/** Wrap the given number into a new BencodedInteger object. */
	public BencodedInteger(long l) { number = l; }
	
	/**
	 * Remove the data of one bencoded integer from the start of d, and parse it into this new BencodedInteger object.
	 * 
	 * @param  d                A Data object with data like "i567e" at the start
	 * @throws ChopException    If the end of the bencoded integer is chopped off
	 * @throws MessageException If there is a mistake in the bencoded data
	 */
	public BencodedInteger(Data d) throws ChopException, MessageException {

		// Make sure d starts "i" for integer, and clip data around everything after that
		if (!d.starts((byte)'i')) throw new MessageException();
		Data data = d.after(1);

		// Look for the "e" at the end, and convert the numerals between "i" and "e" into this new BencodedInteger object's number
		Split split = data.split((byte)'e');
		if (!split.found) throw new ChopException(); // It hasn't arrived yet
		number = Number.toLong(split.before.toString());

		// Remove the data we parsed
		d.remove(split.before.size() + 2); // The 2 removes the "i" and the "e"
	}

	// -------- Look at our data --------
	
	/** Get the number this BencodedInteger holds. */
	public long i() { return number; }
	
	// -------- Convert to data and show as text --------

	/**
	 * Convert this BencodedInteger object into bencoded data, and add it to bay.
	 * Adds data like "i567e" to the Bay.
	 */
	public void toBay(Bay bay) {
		bay.add("i" + number + "e");
	}
	
	/** Compose a line of text that shows this BencodedInteger to the user, like "  567 (i)\r\n". */
	public void toString(StringBuffer b, String indent) {
		b.append(indent + number + " (i)\r\n");
	}
}
