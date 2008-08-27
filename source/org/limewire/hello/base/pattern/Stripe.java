package org.limewire.hello.base.pattern;

import java.util.List;

// a Stripe always has width, you can't make a stripe with size 0
// a Stripe object is immutable, once you have one, you don't have to worry about it changing on you
public class Stripe {

	// -------- Make a Stripe --------
	
	/**
	 * Make a new Stripe object to locate a single stripe in a StripePattern.
	 * You can also use a Stripe to define a region of a StripePattern to clip out and look at.
	 * 
	 * @param i    The distance from the origin to the start of this Stripe, 0 or more
	 * @param size The size of this Stripe, it's width, 1 or more
	 */
	public Stripe(long i, long size) {

		// Make sure i isn't negative and size isn't 0
		if (i < 0 || size < 1) throw new IndexOutOfBoundsException();

		// Save the given dimensions
		this.i = i;
		this.size = size;
	}

	// -------- Distance and size --------
	
	/** The distance from the origin to the start of this Stripe, 0 or more. */
	public final long i;
	/** The size of this Stripe, it's width, 1 or more. */
	public final long size;

	/** The distance from the origin to the end of this Stripe, i + size. */
	public long end() {
		return i + size;
	}

	// -------- Math --------
	
	/** true if the given Stripe is the same as this one. */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Stripe)) return false;
		return i == ((Stripe)o).i && size == ((Stripe)o).size;
	}
	
	/**
	 * See if this Stripe overlaps a given one.
	 * Returns new Stripe which shows where both are true.
	 * If the two stripes don't overlap at all, returns null.
	 */
	public Stripe and(Stripe stripe) {
		List<Stripe> list = pattern().stripes(stripe); // Convert this Stripe into a StripePattern, and "and" it with the given Stripe
		if (list.isEmpty()) return null;               // No overlap, return null
		return list.get(0);                            // There was an overlap, return the Stripe that is it
	}
	
	/**
	 * Make a new Stripe that is this one shifted forward a distance of i, or backwards if i is negative.
	 * @throws IndexOutOfBoundsException if you try to shift back past 0.
	 */
	public Stripe shift(long i) {
		if (this.i + i < 0) throw new IndexOutOfBoundsException(); // Make sure i won't go negative
		return new Stripe(this.i + i, size);
	}

	// -------- Convert --------

	/** Convert this Stripe into a StripePattern that has just this one stripe in it. */
	public StripePattern pattern() {
		return new StripePattern(this);
	}

	/** Make a String like "0 5" showing the i and size of this Stripe. */
	public String toString() {
		return i + " " + size;
	}
}
