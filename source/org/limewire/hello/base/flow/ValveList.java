package org.limewire.hello.base.flow;

import java.util.LinkedList;
import java.util.List;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.flow.valve.Valve;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;

/** A list of Valve objects that data flows through. */
public class ValveList extends Close {

	// Make
	
	public ValveList(Update above, boolean in, boolean out) {
		this.above = above;
		update = new Update(new MyReceive());
		list = new LinkedList<Valve>();
		if (in) this.in = Bin.medium();
		if (out) this.out = Bin.medium();
	}
	
	/** The Update object above us that wants to know when we've changed. */
	private final Update above;
	/** Our Update object which all our valves notify when they've changed. */
	public final Update update;

	/** Our list of Valve objects, data flows from the first through to the last. */
	public final List<Valve> list;
	/** The first Valve in list. */
	public Valve first() { return list.get(list.size() - 1); }
	/** The last Valve in list. */
	public Valve last() { return list.get(0); }

	/** The list's original source of data, null if our first Valve produces data, call go() after adding. */
	public Bin in;
	/** The list's destination for the data it finishes processing, null if our last Valve consumes data, call go() after taking. */
	public Bin out;
	/** Call go() after putting data in in or taking data from out. */
	public void go() { update.send(); }

	/** Stop the data flowing through this ValveList. */
	public void close() {
		if (already()) return;
		for (Valve valve : list)
			valve.close(); // Close each Valve in our list
	}
	
	/** The exception that closed us, if any. */
	public Exception exception;
	


	public void pause(boolean pause) {
		if (paused == pause) return;
		paused = pause;
		update.send();
	}
	public boolean paused() { return paused; }
	private boolean paused;
	
	
	
	// actually, there could be a single Update for the task object, the valve list, and all the valves
	// the valve list just has a flow() method
	// yeah, make this change
	
	
	private class MyReceive implements Receive {
		public void receive() {
			if (closed() || paused()) return;
			try {
				
				// Stop each valve that doesn't have a later working on its bins
				for (Valve valve : list)
					valve.stop(); // Throws an exception if one stopped it
				
				// Move data down the list, end to start
				Bin.move(last().out(), out);       // Take from the last in the list
				for (Pair pair : Pair.pairs(list)) // Move data down the list
					Bin.move(((Valve)pair.a).out(), ((Valve)pair.b).in());
				Bin.move(in, first().in());        // Give to the first in the list
				
				// Start each valve that has data and space
				for (Valve valve : list)
					valve.start();

				above.send();
			} catch (Exception e) { exception = e; close(); above.send(); }
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
