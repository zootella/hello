package org.limewire.hello.bittorrent.bencode;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;

// this lets you call print() on a bencoded object, without having to know what kind of bencoded object it is
// there is no such thing as a Bencoded object in the program
// if you see a Bencoded, it's actually something more specific, like a BencodedString or a BencodedInteger
// it's just that the program doesn't know what kind of Bencoded object it is at that point
public abstract class Bencoded {

	// -------- Bencoded.parse(d) turns bencoded data into an object that extends Bencoded --------

	/**
	 * Parse bencoded data into an object that extends Bencoded.
	 * For instance, if data starts with "5:hello", parse() will return a BencodedString object holding "hello".
	 * If data starts "i567e", parse() will instead return a BencodedInteger object with the number 567.
	 * If data starts "l(item)(item)(item)e", parse() will return a BencodedList object that has a list of the 3 parsed objects inside.
	 * 
	 * This method will remove the data it parses from the given Data object d.
	 * Let's say d contains "5:helloAndMoreAfterwards".
	 * This method will return a BencodedString with "hello", and remove 7 bytes from d to leave it "AndMoreAfterwards".
	 * If parse() hits bad data or a ragged end, it will throw an exception without changing d at all.
	 * 
	 * @param  d                A Data object with bencoded data at the start
	 * @return                  A new object that extends Bencoded, like a BencodedDictionary or a BencodedString
	 * @throws ChopException    The end of the bencoded data is missing, like "i567" with no "e", or data is empty
	 * @throws MessageException There is a mistake in the bencoded data that makes it impossible to parse
	 */
	public static Bencoded parse(Data d) throws ChopException, MessageException {

		// Get the first byte in the given data
		byte start = d.first(); // Throws ChopException if d doesn't have a single byte

		// Look at the first byte to see what kind of bencoded data d starts with
		if (start >= '0' && start <= '9')     // It's "0" through "9", d has a bencoded string like "5:hello"
			return new BencodedString(d);     // Parse it into a new BencodedString object, and return it
		else if (start == 'i')                // It's "i" for integer, d has a bencoded integer like "i567e"
			return new BencodedInteger(d);    // Parse it into a new BencodedInteger object, and return it
		else if (start == 'l')                // It's "l" for list, d has a bencoded list like "l(item)(item)(item)e"
			return new BencodedList(d);       // Parse it into a new BencodedList object, and return it
		else if (start == 'd')                // It's "d" for dictionary, d has a bencoded dictionary like "d(key)(value)(key)(value)e"
			return new BencodedDictionary(d); // Parse it into a new BencodedDictionary object, and return it
		else                                  // It has to be one of those things for this to be valid bencoded data
			throw new MessageException();
	}
	
	// -------- Methods Bencoded classes override --------

	/*
	 * A class that extends Bencoded will override some, but not all, of these methods.
	 * For instance, BencodedInteger is the only one that overrides number(), and it doesn't override anything else.
	 * Let's say you have a Bencoded object which you think is a BencodedInteger.
	 * You can call number() on it.
	 * If it really is a BencodedInteger, the call will go to BencodedInteger.number(), and you'll get the number.
	 * If it's something else, the call will come here, and you'll get a MessageException.
	 */
	
	// BencodedString overrides these methods
	public Data getData() throws MessageException { throw new MessageException(); }
	public String getString() throws MessageException { throw new MessageException(); }

	// BencodedInteger overrides this method
	public long i() throws MessageException { throw new MessageException(); }

	// BencodedList overrides these methods
	public List<Bencoded> l() throws MessageException { throw new MessageException(); }
	public Bencoded l(int i) throws MessageException { throw new MessageException(); }

	// BencodedDictionary overrides these methods
	public Set<Map.Entry<BencodedString, Bencoded>> d() throws MessageException { throw new MessageException(); }
	public Bencoded d(String key) throws MessageException { throw new MessageException(); }
	public boolean has(String key) throws MessageException { throw new MessageException(); }

	// -------- Methods on a Bencoded object to use --------
	
	/** Turn this Bencoded object into bencoded data. */
	public Data data() {
		Bay bay = new Bay();
		toBay(bay); // Calls the object-specific toBay() method, if this is a BencodedString, calls BencodedString.toBay()
		return bay.data();
	}

	/** Compose a text outline to describe this Bencoded object to the user. */
	public String toString() {
		StringBuffer b = new StringBuffer();
		toString(b, ""); // Calls the object-specific toString() method
		return b.toString();
	}
	
	// -------- Methods each class that extends Bencoded must have --------
	
	/**
	 * Turn this Bencoded object into bencoded data, added to bay.
	 * Each kind of Bencoded object has a toBay(Bay bay) method.
	 */
	public abstract void toBay(Bay bay);

	/**
	 * Compose a text outline to show this Bencoded object to the user.
	 * Each kind of Bencoded object has a toString(StringBuffer b, String indent) method.
	 */
	public abstract void toString(StringBuffer b, String indent);
}
