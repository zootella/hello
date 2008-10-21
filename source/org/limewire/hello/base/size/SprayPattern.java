package org.limewire.hello.base.size;


import java.util.BitSet;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;

public class SprayPattern {

	// -------- Make a new SprayPattern --------

	/** Make a new SprayPattern with the given number of bits in it, 1 or more, all of which start out false. */
	public SprayPattern(int bits) {
		if (bits < 1) throw new IndexOutOfBoundsException();
		size = bits;
	}
	
	/** The number of bits in this SprayPattern. */
	public int size() { return size; }

	/** Shorten this SprayPattern so it has the given number of bits, discard extra ones at the end. */
	public void size(int bits) {
		if (bits <= 0 || bits > size) throw new IndexOutOfBoundsException();
		if (bits == size) return;          // No change
		if (array == null) {               // No BitSet, we're all false or all true
			if (ones == size) ones = bits; // Adjust the ones count if all true, and set the new size
			size = bits;
		} else {                           // We have a BitSet with a variety of values
			array.set(bits, size, false);  // Wipe away any true bits in the extra part wer're chopping off
			size = bits;                   // Make a record of our new size
			update();                      // Count our ones and discard our BitSet if it is all false or all true
		}
	}

	/**
	 * Count how many true bits our BitSet has, and discard it if it has no variety.
	 * Call update() after changing our BitSet.
	 */
	private void update() {
		if (array != null) ones = array.cardinality(); // Count the number of true bits in our BitSet
		if (ones == 0 || ones == size) array = null;   // If we're all 0s or all 1s, discard our BitSet
	}

	/** Make a copy of this SprayPattern you can change without changing this one. */
	public SprayPattern copy() {
		SprayPattern p = new SprayPattern(size);            // Same number of bits
		p.ones = ones;                                      // Same number set to true
		if (array != null) p.array = (BitSet)array.clone(); // Give p a copy of our BitSet, copying all its data
		return p;
	}
	
	/** Set this SprayPattern to be a copy of p. */
	public void set(SprayPattern p) {
		size = p.size;                     // Copy in the size and number of 1s
		ones = p.ones;
		if (p.array == null) array = null; // Copy in p's BitSet, if it has one
		else                 array = (BitSet)p.array.clone();
	}
	
	/** true if the given SprayPattern is the same as this one. */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof SprayPattern)) return false;
		SprayPattern p = (SprayPattern)o;
		if (size != p.size || ones != p.ones) return false; // Check their size and number of 1s
		if (array != null && p.array != null) return array.equals(p.array); // Both have a BitSet, compare them
		return true; // Same size and number of 1s, no BitSet, both must be all false or all true, they're equal
	}
	
	/** The number of bits in this SprayPattern. */
	private int size;
	/** The number of bits in this SprayPattern set to true. */
	private int ones;
	/**
	 * A Java BitSet object that has an array of size bits, some set to true, the others false.
	 * If this whole SprayPattern is false, or if it's all true, we don't need this BitSet, array will be null.
	 * If array is null, get the overall value from size and ones, (ones == 0) means all false, (ones == size) all true.
	 */
	private BitSet array;
	
	// -------- Convert to and from text --------

	/** Make a new SprayPattern from a String like "11001001". */
	public SprayPattern(String s) {
		this(s.length());                         // Each character will become a bit, make sure size is 1 or more
		for (int i = 0; i < s.length(); i++)      // Loop for each character in s
			if (s.charAt(i) == '1') set(i, true); // If it's not a "1", leave the bit 0
	}
	
	/** Convert this SprayPattern into a String like "11001001". */
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < size; i++) { // Loop for each bit in this SprayPattern
			if (get(i)) b.append('1');   // If the bit at index i is true, append a "1"
			else        b.append('0');   // The bit at i is false, append a "0"
		}
		return b.toString();
	}

	// -------- Convert to and from data --------

	/** Parse d into this new SprayPattern object, taking bits number of bits from the start. */
	public SprayPattern(Data d, int bits) throws ChopException, MessageException {
		
		// Make sure d isn't empty and bits isn't 0
		int bytes = d.size();
		if (bytes < 1 || bits < 1) throw new MessageException();
		
		// Save the bit size in this new SprayPattern object
		size = bits;

		// Notice if d is all 0 bytes, making this new SprayPattern all false
		int i;
		for (i = 0; i < bytes; i++)   // Loop through each byte in d
			if (d.get(i) != 0) break; // If we find a byte that isn't 00000000, stop looking
		if (i == bytes) return;       // Every byte was 0, we set the size for all false, done

		// Loop through each bit in d, up to bits
		int byteIndex, bitIndex;
		for (i = 0; i < bits; i++) {
			byteIndex = i / 8; // Divide by 8 and chop off the remainder to get the byte index
			bitIndex  = i % 8; // The bit index within that byte is the remainder
			
			// If the bit at i in d is true, set it true in this new SprayPattern
			if ((d.get(byteIndex) & (1 << (7 - bitIndex))) != 0) set(i, true);
		}
	}
	
	/** The number of bytes of data this SprayPattern will take up when you call data() or toBay() on it. */
	public int dataSize() { return (size + 7) / 8; } // 8 bits in a byte, round up to hold 1 through 7 remainder bits
	
	/** Convert this SprayPattern into data. */
	public Data data() { Bay bay = new Bay(); toBay(bay); return bay.data(); }
	/** Convert this SprayPattern into data added to bay. */
	public void toBay(Bay bay) {

		// Make a byte array that's all false
		byte[] a = new byte[dataSize()];
		
		// Step through each true bit in this SprayPattern
		Step step = step(true);
		int byteIndex, bitIndex;
		while (step.next()) { // If this SprayPattern is all false, this loop won't run once
			byteIndex = step.i() / 8; // Divide by 8 and chop off the remainder to get the byte index
			bitIndex  = step.i() % 8; // The bit index within that byte is the remainder
			
			// Flip the corresponding bit in a to 1
			a[byteIndex] = (byte)((a[byteIndex] & 0xff) | (1 << (7 - bitIndex)));
		}
		
		// Add the byte array we made and filled to the given Bay
		bay.add(a);
	}

	// -------- Set and get bit values --------

	/** Set the bit at i in this SprayPattern to the value b. */
	public void set(int i, boolean b) {
		
		// Check i
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
		
		// If our bit at i is already b, there is nothing for us to change
		if (get(i) == b) return; // Otherwise, we know we are going to flip the bit at i

		// Update our count of how many true bits we'll have after we flip the one at i
		if (b) ones++; // The bit at i is false, and we're going to set it to true
		else   ones--; // The bit at i is true, and we're going to set it to false

		// Flipping the bit will make this whole SprayPattern false, or true
		if (ones == 0 || ones == size) {
			
			// Discard our BitSet
			array = null;

		// Flipping the bit will leave us with a mixture of true and false bits
		} else {
			
			// If we don't have a BitSet, make one
			if (array == null) {
				array = new BitSet(size); // The BitSet starts out all 0s
				if (!b && ones == size - 1) array.set(0, size); // If all our bits were true, make array all 1s
			}
			
			// Flip the bit at i to the given value
			array.set(i, b);
		}
	}

	/** Set all the bits in this SprayPattern to b. */
	public void set(boolean b) {
		array = null;       // We don't need a BitSet anymore
		if (b) ones = size; // Set the number of 1s we have, all or nothing
		else   ones = 0;
	}
	
	/** Get the value of the bit at index i in this SprayPattern. */
	public boolean get(int i) {
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
		if (array == null) return ones == size; // If we don't have a BitSet, ones is either 0 or size
		return array.get(i); // We have a BitSet, look up the bit at i
	}

	/** true if all the bits in this SprayPattern are set to b, false if some or all are not. */
	public boolean is(boolean b) {
		if (array == null) return (ones == size) == b; // Return true if our value matches the given one
		return false; // If we have an array, it must have a mixture of true and false bits
	}

	// -------- Count how many false or true bits there are, and step through them --------

	/** Count how many bits in this SprayPattern are set to b. */
	public int count(boolean b) {
		if (b) return ones;        // We save the count of how many bits are true
		else   return size - ones; // Subtract to find how many bits are false
	}

	/**
	 * Make a Step object that will let you step through all the false bits or all the true bits in this SprayPattern.
	 * Use it like this:
	 * 
	 * SprayPattern.Step step = p.step(true); // Look for true bits in the SprayPattern p
	 * while (step.next()) {                  // Loop until next() returns false
	 *     i = step.i();                      // Get the index of the true bit we found
	 * }
	 * 
	 * @param b false to step through all the false bits, true to step through all the true bits
	 * @return  A new Step object to call next() and i() on
	 */
	public Step step(boolean b) { return new Step(this, b); }
	
	/** A SprayPattern.Step object steps through all the false bits or all the true bits in a SprayPattern. */
	public static class Step {
		
		/** The SprayPattern this Step object is stepping through. */
		private SprayPattern p;
		/** The value this Step object is stopping on, false bits or true bits. */
		private boolean b;
		/** The index in p where the next search will begin, 0 to begin, then beyond the last bit we found. */
		private int i;
		
		/** Make a Step object to step through all the false bits or all the true bits in a SprayPattern. */
		public Step(SprayPattern p, boolean b) {
			this.p = p; // Save the given SprayPattern and value to look for
			this.b = b;
		}
		
		/** Find the next bit that has the requested value, keep calling next() until it returns false. */
		public boolean next() {
			
			// A previous call to next() moved i to the end of p
			if (i == p.size) return false; // Prevent another call to next()

			// Move i forward to the next bit set to b, if i is already on a bit like that, don't move it
			if (p.array == null) {                        // We don't have a BitSet because all our bits are false or true
				if ((p.ones == p.size) == b);             // Our overall value matches b, the next b from i is at i
				else return false;                        // Our overall value isn't b, not found
			} else {                                      // We have a BitSet with a mixture of false and true bits
				if (b) i = p.array.nextSetBit(i);         // Have it look for the next false or true bit at i or beyond
				else   i = p.array.nextClearBit(i);
				if (i == -1 || i == p.size) return false; // nextClearBit() will find a false bit at size
			}

			// Move i past the bit we found to look beyond it the next time
			i++;

			// Permit another call to next()
			return true;
		}
		
		/** The index of the bit that next() found. */
		public int i() { return i - 1; } // Subtract 1 because next() moved i forward for the next call
	}

	// -------- Binary math --------

	/** Change this SprayPattern to be (!this), flipping every bit. */
	public SprayPattern not() {
		if      (is(false)) set(true);
		else if (is(true))  set(false);
		else                { array.flip(0, size); ones = size - ones; }
		return this; // Return a pointer to this SprayPattern object so you can call this method in a chain
	}

	/** Change this SprayPattern to be (this | p), collecting 1s from both. */
	public SprayPattern or(SprayPattern p) {
		if (size != p.size) throw new IndexOutOfBoundsException();
		if      (is(false))   set(p);
		else if (is(true))    ; // No change
		else if (p.is(false)) ; // No change
		else if (p.is(true))  set(true);
		else                  { array.or(p.array); update(); }
		return this;
	}
	
	/** Change this SprayPattern to be (this & p), only 1s both places survive. */
	public SprayPattern and(SprayPattern p) {
		if (size != p.size) throw new IndexOutOfBoundsException();
		if      (is(false))   ; // No change
		else if (is(true))    set(p);
		else if (p.is(false)) set(false);
		else if (p.is(true))  ; // No change
		else                  { array.and(p.array); update(); }
		return this;
	}
	
	/** Change this SprayPattern to be (this & (!p)), showing what this has that p needs. */
	public SprayPattern andNot(SprayPattern p) {
		if (size != p.size) throw new IndexOutOfBoundsException();
		if      (is(false))   ; // No change
		else if (is(true))    { set(p); not(); }
		else if (p.is(false)) ; // No change
		else if (p.is(true))  set(false);
		else                  { array.andNot(p.array); update(); }
		return this;
	}
	
	/** Change this SprayPattern to be (this xor p), true where the two are different. */
	public SprayPattern xor(SprayPattern p) {
		if (size != p.size) throw new IndexOutOfBoundsException();
		if      (is(false))   set(p);
		else if (is(true))    { set(p); not(); }
		else if (p.is(false)) ; // No change
		else if (p.is(true))  not();
		else                  { array.xor(p.array); update(); }
		return this;
	}
}
