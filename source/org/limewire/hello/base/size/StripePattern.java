package org.limewire.hello.base.size;


import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.exception.MessageException;

public class StripePattern {

	// -------- The list of numbers --------

	/**
	 * A StripePattern has a list of numbers, like this:
	 * 
	 * 0 1 3 8 50
	 * 
	 * Negative numbers aren't allowed, the numbers are in ascending order, and there are no duplicates.
	 * If the list includes 0, there will be a single 0 at the start.
	 * The list can be empty, or include any number of numbers.
	 * 
	 * The list describes the location and size of the true and false stripes that make up this StripePattern.
	 * All the numbers are distances from the origin in bytes, or whatever unit the StripePattern was made to measure.
	 * In the example above, the StripePattern is true between 0 and 1, between 3 and 8, and from 50 onwards.
	 * It is false between distances of 1 and 3, and between 8 and 50.
	 * An empty list describes a StripePattern that is false everywhere, while a list with just "0" is true everywhere.
	 * 
	 * list is a Java List of Long objects, each Long object holds one number.
	 */
	private List<Long> list;
	
	/** Make a new empty StripePattern that is false everywhere. */
	public StripePattern() {
		list = new ArrayList<Long>(); // Use ArrayList to look up numbers by their index quickly
	}

	/** Make a new StripePattern that has the single given Stripe, true within it and false everywhere else. */
	public StripePattern(Stripe stripe) {
		this();
		list.add(stripe.i); // Add two numbers, the distances to the start and to the end of the stripe
		list.add(stripe.end());
	}
	
	/** Return a copy of this StripePattern you can change without changing this one. */
	public StripePattern copy() {
		StripePattern p = new StripePattern();
		p.list.addAll(list); // Copy the list
		return p;
	}

	/** The distance to the last edge in this StripePattern, or 0 if it has none. */
	public long size() {
		if (list.isEmpty()) return 0;     // list has no numbers
		return list.get(list.size() - 1); // Return the last one
	}

	/** true if the given StripePattern is the same as this one. */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof StripePattern)) return false;
		return list.equals(((StripePattern)o).list); // Equate the lists
	}

	// -------- Save a StripePattern as a String like "0 2 2" --------

	/**
	 * Make a new StripePattern from a String with widths separated by spaces.
	 * Each number is the width of a stripe.
	 * The first stripe has a false value, and their values alternate after that.
	 * Only the first stripe can have a width of 0, all the numbers after that have to be 1 or more.
	 * 
	 * "2 2" becomes a StripePattern that is true between the distances 2 and 4.
	 * "0 2 2" becomes a StripePattern that is true from 0 to 2, and from 4 onwards.
	 */
	public StripePattern(String widths) throws MessageException {

		// Convert widths like "1 1 1" in the given String into distances like 1, 2, 3 for the list
		this();           // Make list a new empty ArrayList
		long minimum = 0; // The first width must be 0 or bigger
		long i = 0;       // Where we'll put the next stripe, measured from the start of the StripePattern

		// Loop for each width in the given String
		for (String word : Text.words(widths)) {
			long w = Number.toLong(word, minimum); // Make sure the width is minimum or bigger
			minimum = 1;                           // After the first width, each width must be 1 or bigger

			// Add a number to our list to define a new stripe
			i += w;        // Add the width to i, the total width of all the stripes we've added
			list.add(i); // Include the new total in the list we're building
		}
	}

	/**
	 * Convert this StripePattern into a String like "0 2 2".
	 * Each number is the width of a stripe, and the first stripe has a false value.
	 */
	public String toString() {
		
		// Convert distances like 1, 2, 3 in our list into widths like "1 1 1" for the String we'll return
		StringBuffer b = new StringBuffer();
		long previous = 0;                  // The index of the last number we found in our list
		for (Long n : list) {               // Loop for each number in our list
			b.append((n - previous) + " "); // Append the width, followed by a space
			previous = n;                   // Now n is the last number we found
		}
		return b.toString().trim(); // Trim the String to remove the extra space at the end
	}

	// -------- Stripes and gaps --------

	/** Get a List of all the true stripes in this StripePattern within clip. */
	public List<Stripe> stripes(Stripe clip) {
		StripePattern p = and(clip.pattern());          // Clip out the requested part, making p.list.size() even
		List<Stripe> stripes = new ArrayList<Stripe>(); // Make a new empty ArrayList to fill with new Stripe objects
		for (int i = 0; i < p.list.size(); i += 2)      // Loop for each pair of numbers, a pair defines a true stripe
			stripes.add(p.stripe(i));                   // Turn the number pair into a new Stripe object
		return stripes;                                 // Return the List we made and filled
	}

	/** Find the widest true Stripe in this StripePattern within clip, or null if there aren't any. */
	public Stripe biggestStripe(Stripe clip) {
		StripePattern p = and(clip.pattern());                      // Clip out the requested part, making p.list.size() even
		Stripe biggest = null;                                      // We'll point biggest at the biggest Stripe we find
		for (int i = 0; i < p.list.size(); i += 2)                  // Loop for each pair of numbers, a pair defines a true stripe
			if (biggest == null || biggest.size < p.stripe(i).size) // We do, but this stripe is bigger
				biggest = p.stripe(i);                              // Pick it as our winner
		return biggest;                                             // If there were no stripes, biggest will still be null
	}
	
	/** Total the sizes of all the true stripes in this StripePattern within clip. */
	public long sizeTrue(Stripe clip) {
		StripePattern p = and(clip.pattern());           // Clip out the requested part, making p.list.size() even
		long size = 0;                                   // Start the total from 0
		for (int i = 0; i < p.list.size(); i += 2)       // Loop for each pair of numbers, a pair defines a true stripe
			size += (p.list.get(i + 1) - p.list.get(i)); // Add the size of the stripe to the total
		return size;                                     // Return the size we summed
	}

	/** Get a List of all the false stripes in this pattern within clip. */
	public List<Stripe> gaps(Stripe clip) { return not().stripes(clip); } // Invert this StripePattern and call stripes() on it
	/** Find the widest false Stripe in this StripePattern within clip, or null if there aren't any. */
	public Stripe biggestGap(Stripe clip) { return not().biggestStripe(clip); }
	/** Total the size of all the false stripes in this StripePattern within clip. */
	public long sizeFalse(Stripe clip) { return not().sizeTrue(clip); }

	/** Make a new Stripe object from the numbers at index i and i + 1 in our list of numbers. */
	private Stripe stripe(int i) {
		return new Stripe(list.get(i), list.get(i + 1) - list.get(i)); // Calculate the stripe distance and width
	}

	// -------- Clip out a StripePattern from this one, and insert one back in --------

	/** Clip out the part of this StripePattern identified by the given Stripe. */
	public StripePattern clip(Stripe clip) {
		return and(clip.pattern()).shift(-clip.i); // Clip out with "&" and shift down
	}

	/** Insert the StripePattern p, which is clip.size big, at a distance clip.i into this one, overwriting what's there. */
	public StripePattern insert(StripePattern p, Stripe clip) {
		return remove(clip).or(p.shift(clip.i)); // Make a hole, shift p up into position, and use "|" to combine them
	}

	/** Return a new StripePattern with all the stripes from this one shifted a distance i. */
	private StripePattern shift(long i) {
		StripePattern p = new StripePattern();
		for (Long n : list) {                                     // Loop through each number in our list
			if (n + i < 0) throw new IndexOutOfBoundsException(); // Make sure this doesn't introduce a negative value
			p.list.add(n + i);                                    // Shift it i forward, or back if i is negative
		}
		return p;
	}
	
	// -------- Add or remove a Stripe, and see if one is there or not --------

	/** Make a new StripePattern which is this one with the given stripe added, true there. */
	public StripePattern add(Stripe stripe) {
		return or(stripe.pattern()); // Use "|" to add the stripe
	}

	/** Make a new StripePattern which is this one with the given stripe removed, false there. */
	public StripePattern remove(Stripe stripe) {
		return and(stripe.pattern().not()); // Use "!" and "&" to remove the stripe
	}

	/** true if this StripePattern is all value in clip. */
	public boolean is(boolean value, Stripe clip) {
		if (value) return and(clip.pattern()).equals(clip.pattern()); // true if this and stripe is stripe
		else return and(clip.pattern().not()).equals(new StripePattern()); // true if this and !stripe is blank
	}

	/** true if this StripePattern is a single Stripe at the start size big. */
	public boolean isComplete(long size) {
		return size() == size && is(true, new Stripe(0, size));
	}

	// -------- And, or, and not operations --------

	/** Make a new StripePattern which is (!this), true where this one is false, leaving this one unchanged. */
	public StripePattern not() {
		try {
			if (list.isEmpty())                // No numbers, false everywhere
				return new StripePattern("0"); // Return "0" which is true everywhere
			StripePattern p = copy();          // Make a copy of this StripePattern to change and return
			if (p.list.get(0) == 0)            // Flip the presence of "0" at the start of the list, flipping true and false everywhere after
				p.list.remove(0);              // The list has "0", remove it
			else
				p.list.add(0, (long)0);        // The list lacks "0", add it to the start
			return p;
		} catch (MessageException e) { throw new CodeException(); }
	}

	/** Return a new StripePattern which is (this | p), true everywhere this one or p are true, leaving this one unchanged. */
	public StripePattern or(StripePattern p) {
		StripePattern r = new StripePattern(); // Make a new empty StripePattern to fill and return
		Step step = new Step(list, p.list);    // Step through each number in the two lists
		while (step.next())
			if (step.or) r.list.add(step.n);   // If our Step object found a n we should include for an "|" operation, add it to result
		return r;
	}

	/** Return a new StripePattern which is (this & p), true only where this one and p are true, leaving this one unchanged. */
	public StripePattern and(StripePattern p) {
		StripePattern r = new StripePattern(); // Make a new empty StripePattern to fill and return
		Step step = new Step(list, p.list);    // Step through each number in the two lists
		while (step.next())
			if (step.and) r.list.add(step.n);  // If our Step object found a n we should include for an "&" operation, add it to result
		return r;
	}

	/** Return a new StripePattern which is (this xor p), true where this one and p are different, leaving this one unchanged. */
	public StripePattern xor(StripePattern p) {
		StripePattern r = new StripePattern(); // Make a new empty StripePattern to fill and return
		Step step = new Step(list, p.list);    // Step through each number in the two lists
		while (step.next())
			if (step.xor) r.list.add(step.n);  // If our Step object found a n we should include for a xor operation, add it to result
		return r;
	}
	
	/**
	 * Make a Step object to step through two lists of numbers at the same time.
	 * Each list can have any number of numbers, but the numbers have to count upwards with no duplicates.
	 * 
	 * listA: 0 2 3 5 9
	 * listB: 1 3
	 * 
	 * Given these lists, step.next() will return true 6 times, with step.n set to 0, 1, 2, 3, 5, and then 9.
	 * Check step.and to see if you should add n for an "&" operation, or step.or for "|".
	 */
	private static class Step {

		/**
		 * Make a new Step object to step through two lists of numbers at the same time.
		 * Swapping listA and listB won't change the results this Step object gives you.
		 */
		public Step(List<Long> listA, List<Long> listB) {
			this.listA = listA; // Save the lists in this new Step object
			this.listB = listB;
		}

		/** One of the two lists of ascending numbers this Step object is stepping through. */
		private List<Long> listA;
		/** The index in listA this Step object is currently on. */
		private int indexA;
		/**
		 * The value, false or true, of the StripePattern listA describes a distance n into it.
		 * If the StripePattern switches value at n, this is the value after the switch.
		 */
		private boolean valueA;
		
		/** One of the two lists of ascending numbers this Step object is stepping through. */
		private List<Long> listB;
		/** The index in listB this Step object is currently on. */
		private int indexB;
		/**
		 * The value, false or true, of the StripePattern listB describes a distance n into it.
		 * If the StripePattern switches value at n, this is the value after the switch.
		 */
		private boolean valueB;

		/** A number we found in listA, listB, or both, which this Step object is currently on. */
		public long n;
		/** true if the list of numbers that describes the StripePattern a | b should include n. */
		public boolean or;
		/** true if the list of numbers that describes the StripePattern a & b should include n. */
		public boolean and;
		/** true if the list of numbers that describes the StripePattern a xor b should include n. */
		public boolean xor;
		
		/**
		 * Step to the first, and after that, next, number n that appears in listA, listB, or both.
		 * If next() returns true, look at step.n, step.or, and step.and to see what next() found.
		 * 
		 * @return true if this Step object has found a number n, look at it and then call next() again.
		 *         false if this Step object has finished both lists, it's done.
		 */
		public boolean next() {
			
			// If listA has a number at n, we'll set edgeA to true
			boolean edgeA = false;
			boolean edgeB = false;

			// Both lists have a number left in them
			if (indexA < listA.size() && indexB < listB.size()) {
				n = Math.min(listA.get(indexA), listB.get(indexB)); // Set n from listA or listB, whichever is lower
				if (n == listA.get(indexA)) { // We got n from listA
					edgeA = true;     // Record that listA has an edge at n, flipping its value between true and false
					valueA = !valueA; // Record that value in listA at n, the edge flips it from what it was before
					indexA++;         // Next time, we'll look at the next number in listA
				}
				if (n == listB.get(indexB)) { // n might be in listB instead, or in listB also
					edgeB = true;
					valueB = !valueB;
					indexB++;
				}

			// Only listA has a number we haven't seen yet, we're done with listB
			} else if (indexA < listA.size()) {
				n = listA.get(indexA); // n is that number
				edgeA = true;
				valueA = !valueA;
				indexA++;

			// Only listB has a number left
			} else if (indexB < listB.size()) {
				n = listB.get(indexB);
				edgeB = true;
				valueB = !valueB;
				indexB++;

			// We're through both lists
			} else {
				return false; // Tell the caller to stop calling this next() method
			}
			
			// Count how many lists have n in them
			int edges = 0; // edges will be 1 or 2, never 0, because we wouldn't have stopped here if neither list had n
			if (edgeA) edges++;
			if (edgeB) edges++;

			// Count how many lists describe a StripePattern that has the value true a distance n in to them
			int heights = 0; // heights will be 0, 1, or 2
			if (valueA) heights++;
			if (valueB) heights++;

			// Figure out whether a list we're making to describe the StripePattern (a | b) or (a & b) should include n or not
			if (edges == 1 && heights == 0)                                               { or = true;  and = false; xor = true;  } // One low, the other falls
			if (edges == 1 && heights == 1 && ((valueA &&  edgeA) || (valueB &&  edgeB))) { or = true;  and = false; xor = true;  } // One low, the other rises
			if (edges == 1 && heights == 1 && ((valueA && !edgeA) || (valueB && !edgeB))) { or = false; and = true;  xor = true;  } // One high, the other falls
			if (edges == 1 && heights == 2)                                               { or = false; and = true;  xor = true;  } // One high, the other rises
			if (edges == 2 && heights == 0)                                               { or = true;  and = true;  xor = false; } // Both fall
			if (edges == 2 && heights == 1)                                               { or = false; and = false; xor = false; } // They switch
			if (edges == 2 && heights == 2)                                               { or = true;  and = true;  xor = false; } // Both rise

			// Encourage the caller to call this next() method again
			return true;
		}
	}
}
