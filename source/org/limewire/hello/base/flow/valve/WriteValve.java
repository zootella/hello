package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.later.WriteLater;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class WriteValve extends Close implements Valve {
	
	// Make

	/** Make a WriteValve that will take data from in() and write it to stripe in file. */
	public WriteValve(Update update, File file, Stripe stripe) {
		this.update = update;
		this.file = file;
		this.stripe = stripe;
		in = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The file we write to. */
	private final File file;
	/** The stripe in file we write. */
	public final Stripe stripe;
	/** Our current WriteLater, null if we don't have one right now. */
	private WriteLater later;

	/** Close this Valve so it gives up all resources and won't start again. */
	public void close() {
		if (already()) return;
		if (later != null) {
			later.close();
			later = null; // Discard the closed later so in() and out() work
		}
	}
	
	// Use

	public Bin in() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	public Bin out() { return null; }
	
	public void start() {
		if (closed()) return;
		if (later == null && in.hasSpace())
			later = new WriteLater(update, file, stripe, in);
	}
	
	public void stop() throws Exception {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
		}
	}
	
	public boolean isEmpty() {
		return
			later == null && // No later using our bins
			in.isEmpty();    // No data
	}
}
