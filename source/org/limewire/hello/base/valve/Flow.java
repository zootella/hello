package org.limewire.hello.base.valve;

import java.util.LinkedList;
import java.util.List;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

/** A list of Valve objects that data flows through. */
public class Flow extends Close {

	// Make
	
	/** Make a new list of Valve objects that will take in and or put out data. */
	public Flow(Update update, boolean in, boolean out) {
		this.update = update;
		list = new LinkedList<Valve>();
		if (in) this.in = Bin.medium(); // Make the requested bins
		if (out) this.out = Bin.medium();
	}
	
	/** The Update object above us that wants to know when we've changed. */
	public final Update update;

	/** Our list of Valve objects, data flows from the first through to the last. */
	public final List<Valve> list;
	/** The first Valve in list. */
	public Valve first() { return list.get(0); }
	/** The last Valve in list. */
	public Valve last() { return list.get(list.size() - 1); }

	/** The list's original source of data, null if our first Valve produces data, call go() after adding. */
	public Bin in;
	/** The list's destination for the data it finishes processing, null if our last Valve consumes data, call go() after taking. */
	public Bin out;

	/** Stop the data flowing through this ValveList. */
	public void close() {
		if (already()) return;
		for (Valve valve : list)
			((Close)valve).close(); // Close each Valve in our list
		update.send();
	}
	
	// Go

	/** Move data down this list. */
	public void move() throws Exception {
		if (closed()) return;
			
		// Stop each valve that doesn't have a later working on its bins
		for (Valve valve : list)
			valve.stop(); // Throws an exception if one stopped it
		
		// Move data down the list, end to start
		Bin.move(last().out(), out);       // Take from the last in the list
		for (Pair pair : Pair.pairs(list)) // Move data down the list, bottom to top
			Bin.move(((Valve)pair.a).out(), ((Valve)pair.b).in());
		Bin.move(in, first().in());        // Give to the first in the list
		
		// Start each valve that has data and space
		for (Valve valve : list)
			valve.start();
	}

	/** true if this ValveList is empty of data. */
	public boolean isEmpty() throws Exception {
		if (in != null && in.hasData())   return false; // Not empty
		for (Valve valve: list)
			if (!valve.isEmpty())         return false;
		if (out != null && out.hasData()) return false;
		return true;
	}
}
