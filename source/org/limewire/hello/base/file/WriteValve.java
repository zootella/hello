package org.limewire.hello.base.file;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.move.StripeMove;
import org.limewire.hello.base.pattern.Trip;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class WriteValve extends Close implements Valve {
	
	// Make
	
	/** Make a WriteValve that will take data from in() and write it at index in file, limit size or -1 no limit. */
	public WriteValve(Update update, File file, Trip trip) {
		this.update = update;
		this.file = file;
		this.trip = trip;
		in = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The open File we write to. */
	private final File file;
	/** Our current WriteLater, null if we don't have one right now. */
	private WriteLater later;
	
	/** Where we start writing in the file, how much we've done, and the size limit. */
	public Trip trip() { return trip; }
	private Trip trip;

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
		if (later == null && in.hasData() && !trip.isDone())
			later = new WriteLater(update, file, trip, in);
	}
	
	public void stop() throws Exception {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			StripeMove move = later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
			trip = trip.add(move.stripe.size);
		}
		if (trip.isDone()) close(); // All done
	}
	
	public boolean isEmpty() {
		return
			later == null && // No later using our bins
			in.isEmpty()  && // No data from a Valve above waiting for us to write
			trip.isEmpty();  // No size limit, or a size limit we've filled
	}
}
