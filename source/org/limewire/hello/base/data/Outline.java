package org.limewire.hello.base.data;


import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.encode.Encode;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.internet.name.IpPort;

// rules for designing your outline
// tag names can only be numbers and lowercase letters, as short as possible
// blank ok, duplicate tag names ok
// order can't matter
// tag names can't contain data, or be generated from data, that's what values are for
// values can't contain outline data, that's what contents are for
// numbers are text numerals in values, no numbers in bits
// values shouldn't have compression or encoding that requires more transformation, data in its most raw form
// values shouldn't have structure that requires more parsing, data in its most granular form
// no version numbers, the outline grows without breaking compatibility
// no vendor codes, the outline is a single unified common area
// an outline should be short, 8k or less when turned into data

public class Outline {

	// -------- Inside parts --------
	
	/** The name of this Outline object. */
	public final String name;
	/** The value data of this Outline object. */
	private Data value; // Copy data into this Outline object so value doesn't view a buffer that might change or close
	/** The contents of this Outline object, a List of more Outline objects beneath it in the outline. */
	private List<Outline> contents;

	// -------- Value --------
	
	/** Get this Outline object's data value. */
	public Data getData() { return value; }
	/** Get this Outline object's value as a String. */
	public String getString() { return value.toString(); }
	/** Get this Outline object's value as a number, throw MessageException if it's not a number. */
	public long getNumber() throws MessageException { return Number.toLong(getString()); }
	/** Get this Outline object's value as an IpPort object, throw MessageException if it's not an IP address and port number. */
	public IpPort getIpPort() throws MessageException { return new IpPort(value); }
	/** Get this Outline object's value as a List of IpPort objects, throw MessageException if it's not IP addresses and port numbers. */
	public List<IpPort> getIpPortList() throws MessageException { return IpPort.list(value); }
	/** Get this Outline object's value as a boolean, throw MessageException if it's not a boolean. */
	public boolean getBoolean() throws MessageException {
		String s = getString();
		if      (s.equals("t")) return true;
		else if (s.equals("f")) return false;
		else throw new MessageException();
	}

	/** Set the value of this Outline object to the given Data. */
	public void set(Data d) {
		this.value = d.copyData(); // Copy the value data into this new Outline object
	}
	
	/** Set the value of this Outline object to the given String. */
	public void set(String s) {
		this.value = new Data(s); // Wrap the given String into a new Data object
	}
	
	/** Set the value of this Outline object to the given number. */
	public void set(long n) {
		this.value = new Data(Number.toString(n)); // An Outline holds a number as a string of numerals like "786"
	}
	
	/** Set the value of this Outline object to the given IpPort. */
	public void set(IpPort p) {
		this.value = p.data(); // Save 6 bytes of data like "123405"
	}
	
	/** Set the value of this Outline object to the given List of IpPort objects. */
	public void set(List<IpPort> list) {
		this.value = IpPort.data(list); // Save data like "123405123405", a multiple of 6 bytes long
	}
	
	/** Set the value of this Outline object to the given boolean. */
	public void set(boolean b) {
		if (b) set("t");
		else   set("f");
	}
	
	/** Clear the value and contents of this Outline object. */
	public void clear() {
		set(""); // No value
		contents.clear(); // Remove all the Outline objects in our contents list
	}

	// -------- Make a new Outline object and set its name and value --------
	
	/** Make a new Outline object with no name, value, or contents. */
	public Outline() { this(""); }
	/** Make a new Outline object with the given name and no value. */
	public Outline(String name) {
		this.contents = new ArrayList<Outline>();
		this.name = name;
		this.value = Data.empty(); // No data
	}
	
	/** Make a new Outline object with the given name and Data value. */
	public Outline(String name, Data d) { this(name); set(d); }
	/** Make a new Outline object with the given name and String value. */
	public Outline(String name, String s) { this(name); set(s); }
	/** Make a new Outline object with the given name and number value. */
	public Outline(String name, long n) { this(name); set(n); }
	/** Make a new Outline object with the given name and boolean value. */
	public Outline(String name, boolean b) { this(name); set(b); }
	/** Make a new Outline object with the given name and IpPort value. */
	public Outline(String name, IpPort p) { this(name); set(p); }
	/** Make a new Outline object with the given name and List of IpPort objects as its value. */
	public Outline(String name, List<IpPort> list) { this(name); set(list); }
	
	// -------- Contents --------

	/** Add a new Outline to this one, with the given name and no value. */
	public void add(String name) { add(new Outline(name)); }
	/** Add a new Outline to this one, with the given name and Data value. */
	public void add(String name, Data d) { add(new Outline(name, d)); }
	/** Add a new Outline to this one, with the given name and String value. */
	public void add(String name, String s) { add(new Outline(name, s)); }
	/** Add a new Outline to this one, with the given name and number value. */
	public void add(String name, long n) { add(new Outline(name, n)); }
	/** Add a new Outline to this one, with the given name and boolean value. */
	public void add(String name, boolean b) { add(new Outline(name, b)); }
	/** Add a new Outline to this one, with the given name and IpPort value. */
	public void add(String name, IpPort p) { add(new Outline(name, p)); }
	/** Add a new Outline to this one, with the given name and List of IpPort objects as its value. */
	public void add(String name, List<IpPort> list) { add(new Outline(name, list)); }

	/** Add o to this Outline object's contents. */
	public void add(Outline o) {
		contents.add(o); // Add the given Outline object to our contents List of them
	}

	/** true if this Outline contains name. */
	public boolean has(String name) {
		for (Outline o : contents)
			if (o.name.equals(name)) return true; // Found an Outline object in our contents with the given name
		return false; // Not found
	}

	/** Remove all the Outline objects in our contents that have the given name. */
	public void remove(String name) {
		for (Outline o : new ArrayList<Outline>(contents))
			if (o.name.equals(name)) contents.remove(o); // Found name, remove it
	}

	/** Get a List of the Outline objects within this one that have the name "", the default list. */
	public List<Outline> list() {
		return list(""); // Give the blank name to the next method
	}

	/** Get a List of the Outline objects within this one that have the given name. */
	public List<Outline> list(String name) {
		List<Outline> list = new ArrayList<Outline>();
		for (Outline o : contents)
			if (o.name.equals(name)) list.add(o); // We found one with a matching name, add it to the list we'll return
		return list;
	}
	
	// -------- Navigate --------

	/** Move down from this Outline object to name within it, throw MessageException if name is not found. */
	public Outline o(String name) throws MessageException {
		for (Outline o : contents)
			if (o.name.equals(name)) return o; // Return the first Outline in our contents that has a matching name
		throw new MessageException();
	}

	/** Move down from this Outline object to name within it, make name if it doesn't exist yet. */
	public Outline m(String name) {
		try {
			if (!has(name)) add(new Outline(name)); // If name isn't there, make it
			return o(name);
		} catch (MessageException e) { throw new CodeException(); } // We just added it, so it has to be there
	}

	/** Move down into path like "name.name.name", throw MessageException if name is not found. */
	public Outline path(String path) throws MessageException {
		List<String> names = Text.words(path, ".");
		Outline o = this;
		for (String name : names) o = o.o(name); // Move down, throw MessageException if name is not found
		return o; // Return the Outline object at the end of the path
	}

	/** Move down into path like "name.name.name", making Outline objects that don't exist yet. */
	public Outline make(String path) {
		List<String> names = Text.words(path, "."); // Trims names and doesn't include blank names
		Outline o = this;
		for (String name : names) o = o.m(name); // Move down, making each one
		return o; // Return the Outline object at the end of the path
	}
	
	// -------- Convert to text --------
	
	/** Turn this Outline into a text outline. */
	public String toString() { StringBuffer b = new StringBuffer(); toString(b); return b.toString(); }
	/** Turn this Outline into a text outline, added to the given StringBuffer. */
	public void toString(StringBuffer b) {
		toString(b, "");  // Start with no indent
		b.append("\r\n"); // Mark the end of the text outline with a blank line
	}
	
	/** Turn this Outline into a text outline, added to b, with the given indent like "  ". */
	private void toString(StringBuffer b, String indent) {
		
		// Add a line that describes this Outline object like "  name:value\r\n" to the given StringBuffer
		b.append(indent + name + ":");
		Encode.box(b, value); // Box non-text bytes in base 16 in square braces
		b.append("\r\n");
		
		// Beneath our line, make lines describing each Outline in our contents
		for (Outline o : contents)        // Loop for each Outline in our contents
			o.toString(b, indent + "  "); // Have it describe itself, indented more than we are
	}

	// -------- Parse from text --------

	/**
	 * Parse the data of a text outline at the start of d into a new Outline object.
	 * There must be a blank line marking the end of the text outline, throws a ChopException if it hasn't arrived yet.
	 * Returns a new Outline object, and removes the data it parsed from d.
	 * If the text outline is invalid, removes it from d and throws a MessageException.
	 */
	public static Outline fromText(Data d) throws ChopException, MessageException {
		List<String> lines = Text.group(d); // Remove a group of lines that end with a blank line from the start of d
		List<Outline> list = new ArrayList<Outline>();
		for (String line : lines) list.add(parse(line)); // Parse each text line into an Outline object
		if (list.isEmpty()) throw new MessageException(); // Make sure we got at least one line
		Outline o = group(list); // Look at indent to group the list into a hierarchy
		if (!list.isEmpty()) throw new MessageException(); // Make sure there was just one outline
		return o;
	}

	/** Given a List of Outline objects made from lines of text, look at indent to group them into a hierarchy. */
	private static Outline group(List<Outline> list) {
		Outline o = list.remove(0); // Pull the first one in the list, this is us
		while (!list.isEmpty() && o.indent < list.get(0).indent) // Loop while list starts with a line indented more than we are
			o.add(group(list)); // Have it grab its sub-lines from list, and then add it to our contents
		return o;
	}

	/** Parse a line of text from a text outline like "  name:value" into a new Outline object. */
	private static Outline parse(String s) throws MessageException {
		try {
			
			// Count how many indent characters s has
			int indent = 0;
			while (s.charAt(indent) == ' ' || s.charAt(indent) == '\t') indent++; // Works with spaces or tabs
			s = Text.after(s, indent); // Move beyond them, making s like "name:value"
			
			// Split s around ":" to get the name and value
			TextSplit text = Text.split(s, ":");
			if (!text.found) throw new MessageException(); // Make sure there is a ":"
			String name = text.before;
			Data value = Encode.unbox(text.after); // Turn the box-encoded text back into the data it was made from

			// Make an Outline object and save the indent in it
			Outline o = new Outline(name, value);
			o.indent = indent; // Save the number of indent characters so group() will know what to do
			return o;

		} catch (IndexOutOfBoundsException e) { throw new MessageException(); } // charAt() went beyond the end of s
	}

	/** If this Outline was parsed from a line of text in a text outline, the number of indent characters it had, like 2 in "  name:value". */
	private int indent;
	
	// -------- Convert to data --------
	
	/** Turn this Outline into data. */
	public Data data() { Bay bay = new Bay(); toBay(bay); return bay.data(); }
	/** Turn this Outline into data added to bay. */
	public void toBay(Bay bay) {
		numberToBay(bay, name.length());  // Add the size of the name, and then the name
		bay.add(name);
		numberToBay(bay, value.size());   // Add the size of the value data, and then the value data
		bay.add(value);
		numberToBay(bay, contentsSize()); // Add the size of the contents, and then all the contents
		for (Outline o : contents)
			o.toBay(bay);
	}
	
	/** Predict how big this Outline will be turned into data. */
	private int size() {
		int size = 0;
		size += numberSize(name.length())  + name.length();  // Add the size of name length prefix, and the size of the name
		size += numberSize(value.size())   + value.size();   // Add the size of value length prefix, and the size of the value
		size += numberSize(contentsSize()) + contentsSize(); // Add the size of the contents length prefix, and the size of the contents
		return size;
	}
	
	/** Predict how big our contents will be turned into data. */
	private int contentsSize() {
		int size = 0;
		for (Outline o : contents) // Loop through each Outline in our contents List
			size += o.size();      // Add o's size to the total
		return size;
	}
	
	/** Turn n into 1 or more bytes of data added to bay. */
	private static void numberToBay(Bay bay, int n) {
		for (int height = (numberSize(n) - 1) * 7; height >= 0; height -= 7) { // Loop up to 4 times with height 21, 14, 7, 0
			int y = ((0x7f << height) & n) >> height;                          // Clip out 7 bits in n
			if (height != 0) y = y | 0x80;                                     // Mark bytes up to the last one with a leading 1
			bay.add((byte)y);                                                  // Add the byte we made to the given Bay
		}
	}
	
	/** Predict how big the number n will be turned into data, 1 or more bytes. */
	private static int numberSize(int n) {
		if      (n <= 0x0000007f) return 1; // 7 1s will fit in 1 byte
		else if (n <= 0x00003fff) return 2; // 14 1s will fit in 2 bytes
		else if (n <= 0x001fffff) return 3; // 21 1s will fit in 3 bytes
		else                      return 4; // 28 1s will fit in 4 bytes
	}

	// -------- Parse from data --------
	
	/** Parse data at the start of d into this new Outline object, and remove it from d. */
	public Outline(Data d) throws ChopException, MessageException {
		this.contents = new ArrayList<Outline>();
		Data data = d.copy();                           // Copy d so if we throw an exception, it won't be changed
		name = data.cut(numberParse(data)).toString();  // Parse the name size, and then the name
		value = data.cut(numberParse(data)).copyData(); // Copy the value data into this new Outline object
		Data c = data.cut(numberParse(data));           // Clip c around the data of the contents
		while (c.hasData())                             // Loop until c runs out of data
			contents.add(new Outline(c));               // Parse the Outline at the start of c, and add it to our list
		d.keep(data.size());                            // Done without an exception, remove what we parsed from d
	}
	
	/** Parse 1 or more bytes at the start of d into a number, remove them from d, and return the number. */
	private static int numberParse(Data d) throws ChopException, MessageException {
		int n = 0;
		while (true) {
			byte y = d.cut(1).first();  // Cut one byte from the start of d
			n = (n << 7) | (y & 0x7f);  // Move 7 bits into the bottom of n
			if ((y & 0x80) == 0) break; // If the leading bit is 0, we're done
		}
		if (n < 0) throw new MessageException(); // Don't allow a negative size
		return n;
	}
}
