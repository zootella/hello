package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.later.ReadLater;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Move;

public class ReadValve extends Close implements Valve {
	
	// Make

	/** Make a ReadValve that will read stripe from file and put data in out(). */
	public ReadValve(Update update, File file, Stripe stripe) {
		this.update = update;
		this.file = file;
		this.stripe = stripe;
		remain = stripe; // Start with the whole thing
		out = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The file we read from. */
	private final File file;
	/** The stripe in file we read. */
	public final Stripe stripe;
	/** Our current ReadLater, null if we don't have one right now. */
	private ReadLater later;

	/** The Stripe in file we still have to read, null when done. */
	public Stripe remain() { return remain; }
	private Stripe remain;

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
			Move move = later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
			
			remain = move.remain(); // What we'll try for next time
			if (remain == null) close(); // All done
		}
	}
	
	/** Tell this Valve to start, if possible. */
	public void start() {
		if (closed()) return;
		if (later == null && out.hasSpace() && remain != null)
			later = new ReadLater(update, file, remain, out);
	}

	/** A ReadValve doesn't have an input bin, its source of data is the file. */
	public Bin in() { return null; }
	
	/** Access this Valve's output Bin to get the data it read from the file, null if started. */
	public Bin out() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return out;
	}
	private Bin out;
}
