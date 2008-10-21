package org.limewire.hello.base.size;

/** A Range starts at i and can impose a size limit, and can't change. */
public class Range {
	
	// Make

	/** Make a Range that starts at 0 and has no limit. */
	public Range() { this(0, -1); }
	/** Make a Range at i with size limit, -1 no limit, 0 at limit. */
	public Range(long i, long size) {
		if (i < 0 || size < -1) throw new IndexOutOfBoundsException();
		this.i = i;
		this.size = size;
	}
	
	/** The starting index, 0 start of file or not used. */
	public final long i;
	/** The size limit, -1 no limit, 0 done, 1+ size. */
	public final long size;

	// Look

	/** true if this Range must not do more. */
	public boolean isDone() {
		if (size == -1) return false; // No size limit, never done
		return size == 0;
	}

	/** true if this Range is empty of responsibility to do more. */
	public boolean isEmpty() {
		if (size == -1) return true; // No size limit, always empty
		return size == 0;
	}

	/** true if this Range has a size limit of 0, 1 or more, false no limit. */
	public boolean hasLimit() { return size != -1; }

	// Measure

	/** Throw IndexOutOfBoundsException if done is beyond our limit. */
	public void check(long did) {
		if (did < 0 || (size != -1 && did > size)) throw new IndexOutOfBoundsException();
	}

	/** Choose how many to ask to move, 1 or more, the given maximum unless we are limited with a smaller size. */
	public int ask(int maximum) {
		if (maximum < 1) throw new IndexOutOfBoundsException(); // Bad argument
		if (size == -1) return maximum;                         // We have no size limit, ask for the given maximum
		if (size == 0) throw new IndexOutOfBoundsException();   // We have a size limit and are done, shouldn't be asking at all
		return (int)Math.min(maximum, size);                    // Both maximum and toDo() are 1 or more, return the smaller one
	}
	
	// Change

	/** Make the Range that's this one after did at the start. */
	public Range after(long did) {
		if (did == 0) return this; // No change
		check(did);
		if (size == -1) return new Range(i + did, -1);
		else            return new Range(i + did, size - did);
	}

	/** Now that we know how much this Range has, check and set its size. */
	public Range know(long know) {
		if (know < -1) throw new IndexOutOfBoundsException();   // Bad input
		if (know == -1) return this;                            // Size still unknown, no change
		if (size == -1) return new Range(i, know);              // Learned size
		if (size > know) throw new IndexOutOfBoundsException(); // Less than we expected
		return this;                                            // Know is all we need or more, no change
	}
}
