package org.limewire.hello.base.file;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.move.StripeMove;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class ReadValve extends Close implements Valve {
	
	// Make

	/** Make a ReadValve that will read stripe from file and put data in out(). */
	public ReadValve(Update update, File file, Stripe stripe) {
		this.update = update;
		this.file = file;
		this.stripe = stripe; // Start with the whole thing
		out = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The File we read from. */
	private final File file;
	/** The Stripe in file we still have to read, null when done. */
	public Stripe stripe() { return stripe; }
	private Stripe stripe;
	/** Our current ReadLater, null if we don't have one right now. */
	private ReadLater later;

	/** Close this Valve so it gives up all resources and won't start again. */
	public void close() {
		if (already()) return;
		if (later != null) {
			later.close();
			later = null; // Discard the closed later so in() and out() work
		}
	}
	
	// Use
	
	public Bin in() { return null; }
	
	public Bin out() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return out;
	}
	private Bin out;
	
	public void start() {
		if (closed()) return;
		if (later == null && out.hasSpace() && stripe != null)
			later = new ReadLater(update, file, stripe, out);
	}
	
	public void stop() throws Exception {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			StripeMove move = later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
			stripe = stripe.after(move.stripe); // What we'll try for next time
		}
		if (stripe == null) close(); // All done
	}
	
	public boolean isEmpty() {
		return
			later == null && // No later using our bins
			out.isEmpty() && // No data we read waiting for the next Valve to take
			stripe == null;  // No Stripe for us to still read
	}
}
