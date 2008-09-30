package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.later.ReadLater;
import org.limewire.hello.base.move.FileMove;
import org.limewire.hello.base.move.Move;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class ReadValve extends Close implements Valve {
	
	// Make
	
	/** Make a ReadValve that will read data from file and put it in out(). */
	public ReadValve(Update update, File file) {
		this.update = update;
		this.file = file;
		if (file.hasData()) stripe = new Stripe(0, file.size());
		else                stripe = null;
		remain = stripe;
		out = Bin.medium();
		if (remain == null) close(); // Empty file, make a new closed object
	}

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
	
	public boolean processing() {
		if (closed()) return false;
		return later != null;
	}
	
	public boolean canStop() {
		if (closed()) return false;
		return later != null && later.closed();
	}
	public void stop() throws Exception {
		if (canStop()) { // Our later finished
			FileMove move = later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
			
			remain = remain.after(move.stripe); // What we'll try for next time
			if (remain == null) close(); // All done
		}
	}
	
	public boolean canStart() {
		if (closed()) return false;
		return later == null && out.hasSpace() && remain != null;
	}
	public void start() {
		if (canStart())
			later = new ReadLater(update, file, remain, out);
	}

	public Bin in() { return null; }
	
	public Bin out() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return out;
	}
	private Bin out;
}
