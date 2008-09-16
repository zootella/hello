package org.limewire.hello.base.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.later.MoveLater;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class MoveValve extends Close implements Valve {
	
	// Make

	/** Make a MoveValve that will move data from our in bin to our out bin, just as an example. */
	public MoveValve(Update update) {
		this.update = update;
		in = Bin.medium();
		out = Bin.medium();
	}
	
	/** The Update for the Tube we're in. */
	private final Update update;
	/** Our current MoveLater that moves data from in to out, null if we don't have one right now. */
	private MoveLater later;

	/** Close this Valve so it gives up all resources and won't start again. */
	public void close() {
		if (already()) return;
		if (later != null) {
			later.close();
			later = null; // Discard the closed later so in() and out() work
		}
	}

	// Use
	
	/** Have this Valve stop if it's done, and throw the exception that stopped it. */
	public void stop() throws Exception {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
		}
	}
	
	/** Tell this Valve to start, if possible. */
	public void start() {
		if (closed()) return;
		if (later == null && in.hasData() && out.hasSpace())
			later = new MoveLater(update, in, out);
	}
	
	/** Access this Valve's input Bin to give it data, null if started. */
	public Bin in() {
		if (later != null) return null; // later's worker thread is using our bins, keep them private
		return in;
	}
	private Bin in;
	
	/** Access this Valve's output Bin to get the data it processed, null if started. */
	public Bin out() {
		if (later != null) return null;
		return out;
	}
	private Bin out;
}
