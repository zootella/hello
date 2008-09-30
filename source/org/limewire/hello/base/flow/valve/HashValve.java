package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.encode.Hash;
import org.limewire.hello.base.later.HashLater;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class HashValve extends Close implements Valve {
	
	// Make

	/** Make a HashValve that will take data from in() and hash it. */
	public HashValve(Update update) {
		this.update = update;
		this.hash = new Hash();
		in = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The Hash that hashes the data. */
	public final Hash hash;
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

	public boolean processing() {
		if (closed()) return false;
		return later != null;
	}

	public boolean canStop() {
		if (closed()) return false;
		return later != null && later.closed();
	}
	public void stop() throws Exception {
		if (canStop()) {    // Our later finished
			later.result(); // If an exception closed later, throw it
			later = null;   // Discard the closed later, now in() and out() will work
		}
	}
	
	public boolean canStart() {
		if (closed()) return false;
		return later == null && in.hasData();
	}
	public void start() {
		if (canStart())
			later = new HashLater(update, hash, in);
	}

	public Bin in() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	public Bin out() { return null; }
}
