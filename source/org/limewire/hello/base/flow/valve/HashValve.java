package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.encode.Hash;
import org.limewire.hello.base.later.HashLater;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class HashValve extends Close implements Valve {
	
	// Make

	/** Make a HashValve that will take data from in() and hash it. */
	public HashValve(Update update, Hash hash) {
		this.update = update;
		this.hash = hash;
		in = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The file we write to. */
	private final Hash hash;
	/** Our current HashLater, null if we don't have one right now. */
	private HashLater later;

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
		if (later == null && in.hasSpace())
			later = new HashLater(update, hash, in);
	}

	/** Access this Valve's input Bin to get the data it will hash, null if started. */
	public Bin in() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	/** A WriteValve doesn't have an output bin, it discards the data it hashes. */
	public Bin out() { return null; }
}
