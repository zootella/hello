package org.limewire.hello.bittorrent.bencode;


import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;

public class BencodedList extends Bencoded {
	
	// -------- Hold our data --------
	
	/**
	 * This BencodedList object keeps a list of objects that extend Bencoded.
	 * For instance, list could be empty, or it might contain 1 BencodedString.
	 * Or, it could contain 3 things: a BencodedString, another BencodedList, and then a BencodedInteger.
	 */
	private List<Bencoded> list;
	
	// -------- Make and parse --------
	
	/** Make a new BencodedList object, which will start out empty. */
	public BencodedList() {
		list = new ArrayList<Bencoded>(); // Make list, which will hold objects that extend Bencoded
	}
	
	/** Add a Bencoded object, like a BencodedString or a BencodedInteger, the the end of this BencodedList. */
	public void add(Bencoded b) {
		list.add(b);
	}
	
	/**
	 * Remove the data of one bencoded list from the start of d, and parse it into this new BencodedList object.
	 * 
	 * @param  d                A Data object with data like "l(item)(item)(item)e" at the start
	 * @throws ChopException    If the end of the bencoded list is chopped off
	 * @throws MessageException If there is a mistake in the bencoded data
	 */
	public BencodedList(Data d) throws ChopException, MessageException {
		
		// Call the first constructor to make list a new empty ArrayList
		this();
		
		// Make sure d starts with "l" for list, and clip data around everything after that
		if (!d.starts((byte)'l')) throw new MessageException();
		Data data = d.after(1);

		// Loop until we reach the "e" that ends the list we're parsing
		while (data.first() != 'e')         // If data is empty, our "e" is missing, throws ChopException
			list.add(Bencoded.parse(data)); // Parse the next bencoded object in data, make data smaller, and add it to list
		
		// Remove the data we parsed
		d.keep(data.size() - 1); // The 1 removes the list's ending "e"
	}

	// -------- Look at our data --------
	
	/** Get the List of objects that extend Bencoded that this BencodedList holds. */
	public List<Bencoded> l() { return list; }
	
	/**
	 * Get the Bencoded object at index i in this BencodedList.
	 * 
	 * @param  i                The index to look up, 0 is the first object
	 * @return                  The Bencoded object at that index, like a BencodedInteger or another BencodedList
	 * @throws MessageException If i is not found, like this BencodedList has 2 objects and you ask for index 2, the third
	 */
	public Bencoded l(int i) throws MessageException {
		Bencoded b = null;
		try {
			b = list.get(i);
		} catch (IndexOutOfBoundsException e) { throw new MessageException(); } // Not found
		return b;
	}
	
	// -------- Convert to data and show as text --------
	
	/**
	 * Convert this BencodedList object into bencoded data, and add it to bay.
	 * Adds data like "l(item)(item)(item)e" to the Bay, with the bencoded list items inside.
	 */
	public void toBay(Bay bay) {
		bay.add("l");                         // Add the "l" at the start
		for (Bencoded e : list) e.toBay(bay); // Add all the items in our list
		bay.add("e");                         // Add the "e" at the end
	}

	/**
	 * Compose a text outline that shows this BencodedList to the user, like:
	 * 
	 * l
	 *   item1
	 *   item2
	 * e
	 */
	public void toString(StringBuffer b, String indent) {
		b.append(indent + "l" + "\r\n");                      // Add the "l" at the start
		for (Bencoded e : list) e.toString(b, indent + "  "); // Add the items with a 2 space indent
		b.append(indent + "e" + "\r\n");                      // Add the "e" at the end
	}
}
