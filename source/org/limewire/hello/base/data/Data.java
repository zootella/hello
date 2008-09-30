package org.limewire.hello.base.data;


import java.nio.ByteBuffer;
import java.util.Random;

import org.limewire.hello.base.encode.Encode;
import org.limewire.hello.base.encode.Hash;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;

public class Data implements Comparable<Data> {
	
	//TODO fix the 3 todos below to improve performance and stop copying the ByteBuffer a million times
	
	// -------- Make a new Data object to view some data --------

	/** Make a new Data object that views the single byte y. */
	public Data(byte y) { this(Convert.toByteBuffer(y)); }
	/** Make a new Data object that views the given byte array. */
	public Data(byte[] a) { this(Convert.toByteBuffer(a)); }
	/** Make a new Data object that views the data of the given String. */
	public Data(String s) { this(Convert.toByteBuffer(s)); }
	/** Make a new Data object that views the data between b's position and limit, doesn't change b. */
	public Data(ByteBuffer b) {
		buffer = b.asReadOnlyBuffer().slice(); // Save a copy of b so if b's position moves, buffer's position won't
		//TODO is it necessary to read only and slice, here, this copies it twice more, that may be slow
	}

	/**
	 * Make a copy of this Data object.
	 * Afterwards, you can remove some data from one, and the other will still view it.
	 */
	public Data copy() {
		return new Data(buffer); // Return a new Data object that has a copy of our ByteBuffer
	}

	/**
	 * Make a copy of the memory this Data object views.
	 * Afterwards, the object that holds the data can close, and the copy will still view it.
	 */
	public Data copyData() {
		return (new Bay(this)).data(); // Make a new Bay that will copy the data we view into it
	}
	
	// -------- Convert this Data into a byte array, String, or ByteBuffer --------

	/** Copy the data this Data object views into a new byte array, and return it. */
	public byte[] toByteArray() { return Convert.toByteArray(toByteBuffer()); }
	/** Convert this Data into a String. */
	public String toString() { return Convert.toString(toByteBuffer()); }
	
	/**
	 * Make a read-only ByteBuffer with position and limit clipped around the data this Data object views.
	 * You can move the position without changing this Data object.
	 */
	public ByteBuffer toByteBuffer() {
		return buffer.duplicate(); // Return a copy so if toByteBuffer()'s position changes, buffer's position won't
	}

	// -------- Size --------
	
	/** The number of bytes of data this Data object views. */
	public int size() {
		return buffer.remaining(); // Measure the distance between our ByteBuffer's position and limit
	}

	/** true if this Data object is empty, it has a size of 0 bytes. */
	public boolean isEmpty() { return !hasData(); }
	/** true if this Data object views some data, it has a size of 1 or more bytes. */
	public boolean hasData() {
		return buffer.hasRemaining(); // True if our ByteBuffer's position and limit aren't closed together
	}

	// -------- Change this Data object --------

	/** Remove size bytes from the start of the data this Data object views. */
	public void remove(int size) throws ChopException {
		if (size == 0) return; // Nothing to remove
		if (size > size()) throw new ChopException(); // Asked to remove more than we have
		buffer.position(buffer.position() + size); // Move our ByteBuffer's position forward size bytes
		buffer = buffer.slice(); // Make a new ByteBuffer clipped around just that part
		//TODO stop using slice entirely
	}

	/** Remove data from the start of this Data object, keeping only the last size bytes. */
	public void keep(int size) throws ChopException {
		remove(size() - size); // Remove everything but size bytes
	}
	
	/** Remove size bytes from the start of this Data object, and return a new Data object that views them. */
	public Data cut(int size) throws ChopException {
		Data d = start(size); // Make a new Data d to return that clips out size bytes at the start
		remove(size); // Remove size bytes from the start of this Data object
		return d;
	}

	// -------- Inside parts --------

	/**
	 * A Data object has a ByteBuffer buffer that clips out the data it views.
	 * The data is between buffer's position and limit.
	 */
	private ByteBuffer buffer;
	//TODO can you make this final?

	// -------- Return new Data objects that clip out parts of this Data --------

	/** Clip out up to size bytes from the start of this Data. */
	public Data begin(int size) {
		try {
			return start(Math.min(size, size())); // Don't try to clip out more data than we have
		} catch (ChopException e) { throw new CodeException(); } // There is a mistake in the code in this try block
	}

	/** Clip out the first size bytes of this Data, start(3) is DDDddddddd. */
	public Data start(int size) throws ChopException { return clip(0, size); }
	/** Clip out the last size bytes of this Data, end(3) is dddddddDDD. */
	public Data end(int size) throws ChopException { return clip(size() - size, size); }
	/** Clip out the bytes after index i in this Data, after(3) is dddDDDDDDD. */
	public Data after(int i) throws ChopException { return clip(i, size() - i); }
	/** Chop the last size bytes off the end of this Data, returning the start before them, chop(3) is DDDDDDDddd. */
	public Data chop(int size) throws ChopException { return clip(0, size() - size); }
	/** Clip out part this Data, clip(5, 3) is dddddDDDdd. */
	public Data clip(int i, int size) throws ChopException {

		// Make sure the requested index and size fits inside this Data
		if (i < 0 || size < 0 || i + size > size()) throw new ChopException();

		// Make and return a new Data that clips around the requested part of this one
		ByteBuffer b = toByteBuffer(); // Make a new ByteBuffer b that looks at our data too
		b.position(b.position() + i);  // Move its position and limit inwards to clip out the requested part
		b.limit(b.position() + size);
		return new Data(b);            // Wrap a new Data object around it, and return it
	}

	/** Get the first byte in this Data. */
	public byte first() throws ChopException { return get(0); }
	/** Get the byte i bytes into this Data. */
	public byte get(int i) throws ChopException {
		if (i < 0 || i >= size()) throw new ChopException(); // Make sure i is in range
		return buffer.get(buffer.position() + i); // ByteBuffer.get() takes an index from the start of the ByteBuffer
	}

	// -------- Determine if this Data views the same data as a given object --------

	/** true if this Data object views a single byte, y. */
	public boolean same(byte y) { return same(new Data(y)); }
	/** true if this Data object views the same data as the given byte array. */
	public boolean same(byte[] a) { return same(new Data(a)); }
	/** true if this Data object views the same data as of the given String. */
	public boolean same(String s) { return same(new Data(s)); }
	/** true if this Data object views the same data as the data between b's position and limit, doesn't change b. */
	public boolean same(ByteBuffer b) { return same(new Data(b)); }
	/** true if this Data object views the same data as the given one. */
	public boolean same(Data d) {
		if (size() != d.size()) return false; // Make sure this Data and d are the same size
		else if (size() == 0) return true;    // If both are empty, they are the same
		return search(d, true, false) != -1;  // Search at the start only
	}

	// -------- Determine if this Data starts, ends, or has the data of a given object --------

	/** true if this Data starts with the byte y. */
	public boolean starts(byte y) { return starts(new Data(y)); }
	/** true if this Data starts with the given byte array. */
	public boolean starts(byte[] a) { return starts(new Data(a)); }
	/** true if this Data starts with the data of the given String. */
	public boolean starts(String s) { return starts(new Data(s)); }
	/** true if this Data starts with the data between b's position and limit, doesn't change b. */
	public boolean starts(ByteBuffer b) { return starts(new Data(b)); }
	/** true if this Data starts with d. */
	public boolean starts(Data d) { return search(d, true, false) != -1; }

	/** true if this Data ends with the byte y. */
	public boolean ends(byte y) { return ends(new Data(y)); }
	/** true if this Data ends with the given byte array. */
	public boolean ends(byte[] a) { return ends(new Data(a)); }
	/** true if this Data ends with the data of the given String. */
	public boolean ends(String s) { return ends(new Data(s)); }
	/** true if this Data ends with the data between b's positon and limit, doesn't change b. */
	public boolean ends(ByteBuffer b) { return ends(new Data(b)); }
	/** true if this Data ends with d. */
	public boolean ends(Data d) { return search(d, false, false) != -1; }

	/** true if this Data contains the byte y. */
	public boolean has(byte y) { return has(new Data(y)); }
	/** true if this Data contains the given byte array. */
	public boolean has(byte[] a) { return has(new Data(a)); }
	/** true if this Data contains the data of the given String. */
	public boolean has(String s) { return has(new Data(s)); }
	/** true if this Data contains the data between b's position and limit, doesn't change b. */
	public boolean has(ByteBuffer b) { return has(new Data(b)); }
	/** true if this Data contains d. */
	public boolean has(Data d) { return search(d, true, true) != -1; }

	// -------- Find where in this Data a tag appears --------

	/** Find the distance in bytes from the start of this Data to where the byte y first appears, -1 if not found. */
	public int find(byte y) { return find(new Data(y)); }
	/** Find the distance in bytes from the start of this Data to where the given byte array first appears, -1 if not found. */
	public int find(byte[] a) { return find(new Data(a)); }
	/** Find the distance in bytes from the start of this Data to where the data of the given String first appears, -1 if not found. */
	public int find(String s) { return find(new Data(s)); }
	/** Find the distance in bytes from the start of this Data to where the data between b's position and limit first appears, -1 if not found, doesn't change b. */
	public int find(ByteBuffer b) { return find(new Data(b)); }
	/** Find the distance in bytes from the start of this Data to where d first appears, -1 if not found. */
	public int find(Data d) { return search(d, true, true); }

	/** Find the distance in bytes from the start of this Data to where the byte y last appears, -1 if not found. */
	public int last(byte y) { return last(new Data(y)); }
	/** Find the distance in bytes from the start of this Data to where the given byte array last appears, -1 if not found. */
	public int last(byte[] a) { return last(new Data(a)); }
	/** Find the distance in bytes from the start of this Data to where the data of the given String last appears, -1 if not found. */
	public int last(String s) { return last(new Data(s)); }
	/** Find the distance in bytes from the start of this Data to where the data between b's position and limit last appears, -1 if not found, doesn't change b. */
	public int last(ByteBuffer b) { return last(new Data(b)); }
	/** Find the distance in bytes from the start of this Data to where d last appears, -1 if not found. */
	public int last(Data d) { return search(d, false, true); }

	/**
	 * Find where in this Data d appears.
	 * 
	 * @param d       The tag to search for.
	 * @param forward true to search forwards from the start.
	 *                false to search backwards from the end.
	 * @param scan    true to scan across all the positions possible in this Data.
	 *                false to only look at the starting position.
	 * @return        The byte index in this Data where d starts.
	 *                -1 if not found.
	 */
	private int search(Data d, boolean forward, boolean scan) {
		try {

			// Check the sizes
			if (d.size() == 0 || size() < d.size()) return -1;
			
			// Our search will scan this Data from the start index through the end index
			int start = forward ? 0                 : size() - d.size();
			int end   = forward ? size() - d.size() : 0;
			int step  = forward ? 1                 : -1;
			
			// If we're not allowed to scan across this Data, set end to only look one place
			if (!scan) end = start;
			
			// Scan i from the start through the end in the specified direction
			for (int i = start; i != end + step; i += step) {
				
				// Look for d at i
				int j;
				for (j = 0; j < d.size(); j++) {
					
					// Mismatch found, break to move to the next spot in this Data
					if (get(i + j) != d.get(j)) break;
				}
				
				// We found d, return the index in this Data where it is located
				if (j == d.size()) return i;
			}
			
			// Not found
			return -1;
		
		} catch (ChopException e) { throw new CodeException(); } // There is a mistake in the code in this try block
	}

	// -------- Split this Data around a tag, clipping out what is before and after it --------

	/** Split this Data around the given byte y, clipping out the parts before and after it. */
	public Split split(byte y) { return split(new Data(y)); }
	/** Split this Data around the given byte array, clipping out the parts before and after it. */
	public Split split(byte[] a) { return split(new Data(a)); }
	/** Split this Data around the data of the given String, clipping out the parts before and after it. */
	public Split split(String s) { return split(new Data(s)); }
	/** Split this Data around the data between b's position and limit, clipping out the parts before and after it, doesn't change b. */
	public Split split(ByteBuffer b) { return split(new Data(b)); }
	/** Split this Data around d, clipping out the parts before and after it. */
	public Split split(Data d) { return split(d, true); }

	/** Split this Data around the place the given byte y last appears, clipping out the parts before and after it. */
	public Split splitLast(byte y) { return splitLast(new Data(y)); }
	/** Split this Data around the the place the given byte array last appears, clipping out the parts before and after it. */
	public Split splitLast(byte[] a) { return splitLast(new Data(a)); }
	/** Split this Data around the place the data of the given String last appears, clipping out the parts before and after it. */
	public Split splitLast(String s) { return splitLast(new Data(s)); }
	/** Split this Data around the place the data between b's position and limit last appears, clipping out the parts before and after it, doesn't change b. */
	public Split splitLast(ByteBuffer b) { return splitLast(new Data(b)); }
	/** Split this Data around the place d last appears, clipping out the parts before and after it. */
	public Split splitLast(Data d) { return split(d, false); }
	
	/**
	 * Split this Data around d, clipping out the parts before and after it.
	 * 
	 * @param d       The tag to search for.
	 * @param forward true to find the first place d appears.
	 *                false to search backwards from the end.
	 * @return        A Split object that tells if d was found, and clips out the parts of this Data before and after it.
	 *                If d is not found, split.before will clip out all our data, and split.after will be empty.
	 */
	private Split split(Data d, boolean forward) {
		try {
			
			// Make a Split object to fill with answers and return
			Split split = new Split();
			
			// Search this Data for d
			int i = search(d, forward, true);
			if (i == -1) { // Not found
				split.found  = false;
				split.before = copy();  // Make before a new Data that clips out everything we have
				split.tag    = empty(); // Make tag and after to empty Data objects
				split.after  = empty();
			} else {       // We found d at i, clip out the parts before and after it
				split.found  = true;
				split.before = start(i);
				split.tag    = clip(i, d.size());
				split.after  = after(i + d.size());
			}
			return split;

		} catch (ChopException e) { throw new CodeException(); } // There is a mistake in the code in this try block
	}
	
	// -------- Compare --------

	/** Compare this Data object to another one to determine which should appear first in ascending sorted order. */
	public int compareTo(Data d) {
		return buffer.compareTo(d.buffer); // Use Java's ByteBuffer.compareTo() method
	}

	/** Determine if this Data object views exactly the same data as a given one. */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Data)) return false; // Make sure o is a Data like us
		return buffer.equals(((Data)o).buffer); // Use Java's ByteBuffer.equals() method
	}

	// -------- Use features other classes offer --------
	
	/** Encode this Data into text using base 16, each byte will become 2 characters, "00" through "ff". */
	public String base16() { return Encode.toBase16(this); }
	/** Encode this Data into text using base 32, each 5 bits will become a character a-z and 2-7. */
	public String base32() { return Encode.toBase32(this); }
	/** Encode this Data into text using base 62, each 4 or 6 bits will become a character 0-9, a-z, and A-Z. */
	public String base62() { return Encode.toBase62(this); }
	/** Encode this Data into text like "hello[0d][0a]", putting non-text bytes into square braces. */
	public String box() { return Encode.box(this); }
	/** Turn this Data into text like "hello--", striking out non-text bytes with hyphens. */
	public String strike() { return Encode.strike(this); }

	/** Compute the SHA1 hash of this Data, return the 20-byte, 160-bit hash value. */
	public Data hash() { return Hash.hash(this); }
	
	// -------- Empty and random --------
	
	/** Make a new empty Data object that doesn't view any data, and has a size() of 0 bytes. */
	public static Data empty() {
		return new Data(ByteBuffer.allocate(0)); // Make a new Data object from a 0-byte ByteBuffer
	}

	/** Make size bytes of random data. */
	public static Data random(int size) {
		if (random == null) random = new Random(); // Make our random number generator if we don't have it yet
		byte[] a = new byte[size];                 // Make an empty byte array size bytes long
		random.nextBytes(a);                       // Fill it with random data
		return new Data(a);                        // Wrap a new Data object around it and return it
	}

    /** Our Java random number generator. */
    private static Random random;
}
