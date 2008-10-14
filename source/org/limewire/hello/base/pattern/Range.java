package org.limewire.hello.base.pattern;

public class Range {

	// Make

	/** Make a Range at i of size, 0 done, -1 no size limit. */
	public Range(long i, long size) {
		if (i < 0 || size < -1) throw new IndexOutOfBoundsException();
		this.i = i;
		this.size = size;
	}
	
	// Look
	
	/** The file index to start at, 0 or more. */
	public final long i;
	/** The range size, -1 no limit, 0 done, 1+ size. */
	public final long size;

	/** true if this Range is complete and done. */
	public boolean isDone() {
		if (size == -1) return false; // No size limit, never done
		return size == 0;
	}

	/** true if this Range is empty of responsibility to do more. */
	public boolean isEmpty() {
		if (size == -1) return true; // No size limit, always empty
		return size == 0;
	}
	
	// Move

	/** Make the Range that's this one after did at the start. */
	public Range after(long did) {
		if (did < 1) throw new IndexOutOfBoundsException();
		if (size == -1) return new Range(i + did, size);
		if (did > size) throw new IndexOutOfBoundsException();
		return new Range(i + did, size - did);
	}

	// Calculate

	/** Choose how many to ask to move, 1 or more, the given maximum unless we have a smaller size. */
	public int ask(int maximum) {
		if (maximum < 1) throw new IndexOutOfBoundsException(); // Bad argument
		if (size == -1) return maximum;                         // We have no size limit, ask for the given maximum
		if (size == 0) throw new IndexOutOfBoundsException();   // We have a size limit and are done, shouldn't be asking at all
		return (int)Math.min(maximum, size);                    // Both maximum and toDo() are 1 or more, return the smaller one
	}
}
