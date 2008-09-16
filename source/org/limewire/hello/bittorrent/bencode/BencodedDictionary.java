package org.limewire.hello.bittorrent.bencode;


import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;

public class BencodedDictionary extends Bencoded {
	
	// -------- Hold our data --------
	
	/**
	 * This BencodedDictionary keeps a Map.
	 * In map, a BencodedString key leads to an object that extends Bencoded, like a BencodedInteger.
	 * The Map is actually a TreeMap, which keeps the keys in ascending sorted order.
	 */
	private Map<BencodedString, Bencoded> map;
	
	// -------- Make and parse --------
	
	/** Make a new BencodedDictionary object, which will start out empty. */
	public BencodedDictionary() {
		map = new TreeMap<BencodedString, Bencoded>(); // The TreeMap will call BencodedString.compareTo() to keep sorted
	}
	
	/**
	 * Add a new key and value pair to this BencodedDictionary.
	 * 
	 * @param key   The String key name, like "length"
	 * @param value The Bencoded object, like a BencodedInteger, to put under the key
	 */
	public void add(String key, Bencoded value) {
		map.put(new BencodedString(key), value); // Wrap the String key name into a BencodedString before putting it in the Map
	}
	
	/**
	 * Remove the data of one bencoded dictionary from the start of d, and parse it into this new BencodedDictionary object.
	 * 
	 * @param  d                A Data object with data like "d(key)(value)(key)(value)e" at the start
	 * @throws ChopException    If the end of the bencoded dictionary is chopped off
	 * @throws MessageException If there is a mistake in the bencoded data
	 */
	public BencodedDictionary(Data d) throws ChopException, MessageException {
		
		// Call the first constructor to make map a new empty TreeMap
		this();

		// Make d starts "d" for dictionary, and clip data around everything after that
		if (!d.starts((byte)'d')) throw new MessageException();
		Data data = d.after(1);
		
		// Loop until we reach the "e" that ends the dictionary we're parsing
		while (data.first() != 'e') { // If data is empty, our "e" is missing, throws ChopException

			// Parse the key and its value, and add them to map
			BencodedString key = new BencodedString(data); // Removes the bencoded string key data from data
			Bencoded value = Bencoded.parse(data);         // If data is empty, the value is missing, throws ChopException
			map.put(key, value);
		}

		// Remove the data we parsed
		d.keep(data.size() - 1); // The 1 removes the dictionary's ending "e"
	}

	// -------- Look at our data --------
	
	/**
	 * Get a Set that will let you loop through the keys in this BencodedDictionary.
	 * The Set will contain a number of Map.Entry objects.
	 * The key in each Map.Entry will be a BencodedString object.
	 * The keys will be in ascending sorted order.
	 * The value in each Map.Entry will be an object that extends Bencoded, like BencodedString or BencodedList.
	 */
	public Set<Map.Entry<BencodedString, Bencoded>> d() {
		return map.entrySet();
	}
	
	/**
	 * Look up the Bencoded object stored in this BencodedDictionary under a given key name.
	 * 
	 * @param  key              The key name as a String, like "length"
	 * @return                  The Bencoded object under that key, like a BencodedString or another BencodedDictionary
	 * @throws MessageException If the key is not found
	 */
	public Bencoded d(String key) throws MessageException {
		Bencoded b = map.get(new BencodedString(key)); // Wrap the key name into a BencodedString object
		if (b == null) throw new MessageException(); // Not found
		return b;
	}

	/** Look to see if this BencodedDictionary has the given key or not. */
	public boolean has(String key) {
		return map.containsKey(new BencodedString(key));
	}
	
	// -------- Convert to data and show as text --------

	/**
	 * Convert this BencodedDictionary object into bencoded data, and add it to bay.
	 * Adds data like "d(key)(value)(key)(value)e" to the Bay, with the bencoded keys and values inside.
	 */
	public void toBay(Bay bay) {

		// Add the "d" at the start
		bay.add("d");

		// Loop through all the keys in our Map
        for (Map.Entry<BencodedString, Bencoded> entry : map.entrySet()) { // Because map is a TreeMap, they will come in ascending sorted order
        	
			// Add this key to the Bay, followed by its value
        	entry.getKey().toBay(bay);
        	entry.getValue().toBay(bay);
        }

		// Add the "e" at the end
		bay.add("e");
	}
	
	/**
	 * Compose a text outline that shows this BencodedDictionary to the user, like:
	 * 
	 * d
	 *   key1
	 *     value1
	 *   key2
	 *     value2
	 * e
	 */
	public void toString(StringBuffer b, String indent) {
		
		// Add the "d" at the start
		b.append(indent + "d" + "\r\n");
		
		// Loop through all the keys in our Map
		for (Map.Entry<BencodedString, Bencoded> entry : map.entrySet()) {
			
			// Add the key on one line, followed by its value on the next
			entry.getKey().toString(b, indent + "  "); // Add the key with a 2 space indent
			entry.getValue().toString(b, indent + "    "); // Add the value with a 4 space indent
		}
		
		// Add the "e" at the end
		b.append(indent + "e" + "\r\n");
	}
}
