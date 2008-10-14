package org.limewire.hello.base.pattern;

public class Trip {
	
	// Make

	/** Make a Trip that starts at i and has no size limit. */
	public Trip(long i) { this(i, 0, -1); }
	
	/** Make a Trip that starts at i and has the given size limit beyond that. */
	public Trip(long i, long size) { this(i, 0, size); }
	
	/** Make a Trip that's this one with the given size limit. */
	public Trip size(long size) {
		return new Trip(i, done, size);
	}
	
	/** Make a Trip that's this one with more done. */
	public Trip add(long more) {
		if (more < 1) throw new IndexOutOfBoundsException(); // More must be 1 or more
		return new Trip(i, done + more, size);
	}
	
	/** Make a Trip to mark that we're done towards size both a distance i in, size -1 to not limit. */
	Trip(long i, long done, long size) {
		if (i < 0 || done < 0 || size < -1) throw new IndexOutOfBoundsException();
		if (size != -1 && done > size) throw new IndexOutOfBoundsException(); // No Trip can ever be done over a size limit
		this.i = i;
		this.done = done;
		this.size = size;
	}

	// Look
	
	/** The starting index. */
	public final long i;
	/** The size we've done at i, 0 or more. */
	public final long done;
	/** The size limit from i, 0 or more, -1 no limit. */
	public final long size;

	/** The index we are at now, i + done. */
	public long at() { return i + done; }
	
	/** The size that remains for us to do, 0 or more, -1 unknown. */
	public long remain() {
		if (size == -1) return -1;
		return size - done;
	}

	/** true if this Trip is at its limit and must not do more. */
	public boolean isDone() {
		if (size == -1) return false; // No size limit, never done
		return done == size;
	}

	/** true if this Trip is empty of responsibility to do more. */
	public boolean isEmpty() {
		if (size == -1) return true; // No size limit, always empty
		return done == size;
	}

	// Calculate

	/** Choose how many to ask to move, 1 or more, the given maximum unless we have a size limit and smaller remain(). */
	public int ask(int maximum) {
		if (maximum < 1) throw new IndexOutOfBoundsException();  // Bad argument
		if (size == -1) return maximum;                          // We have no size limit, ask for the given maximum
		if (remain() < 1) throw new IndexOutOfBoundsException(); // We have a size limit and are done, shouldn't be asking at all
		return (int)Math.min(maximum, remain());                 // Both maximum and toDo() are 1 or more, return the smaller one
	}
}
