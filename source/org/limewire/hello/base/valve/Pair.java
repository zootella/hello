package org.limewire.hello.base.valve;

import java.util.ArrayList;
import java.util.List;

public class Pair {
	
	// Pair
	
	/** A Pair looks at two neighboring objects in a List, a that comes right before b. */
	public Pair(Object a, Object b) {
		this.a = a;
		this.b = b;
	}

	/** The first object in this Pair. */
	public final Object a;
	/** The second object in this Pair. */
	public final Object b;
	
	// List of pairs

	/** Given a list of objects, group them into pairs, last to first. */
	public static List<Pair> pairs(List objects) {
		List<Pair> pairs = new ArrayList<Pair>();
		if (objects.size() < 2)
			return pairs; // Not enough objects for even one Pair, return an empty list
		for (int i = objects.size() - 1; i >= 1; i--) // Start with the last Pair
			pairs.add(new Pair(objects.get(i - 1), objects.get(i))); // Add it to the list we'll return
		return pairs;
	}
}
