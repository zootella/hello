package org.limewire.hello.base.pattern;

/** A size 1 or more units big. */
public class Size {
	
	// Object

	/**
	 * Confirm size is 1 or more, and save it in a new Size object.
	 * @throws IndexOutOfBoundsException if size is 0 or negative
	 */
	public Size(long size) {
		if (size < 1) throw new IndexOutOfBoundsException();
		this.size = size;
	}
	
	/** The size, 1 or more. */
	public final long size;
	
	// Math

	/** Return a Size which is this one minus the given size, null if nothing left. */
	public Size minus(long size) {
		if (size < 1 || size > this.size) throw new IndexOutOfBoundsException();
		if (size == this.size) return null;
		return new Size(this.size - size);
	}

	/** Return the smaller Size, this one or the given one. */
	public Size small(long size) {
		return new Size(Math.min(this.size, size));
	}

	// Units

	/** 1, number of bytes in a byte. */
	public static final int bytes = 1;
	/** 1024, number of bytes in a kilobyte. */
	public static final int kilobyte = 1024;
	/** Number of bytes in a megabyte. */
	public static final long megabyte = 1024 * kilobyte;
	/** Number of bytes in a gigabyte. */
	public static final long gigabyte = 1024 * megabyte;
	/** Number of bytes in a terabyte. */
	public static final long terabyte = 1024 * gigabyte;
}
