package org.limewire.hello.base.download;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Meter;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class GetValve extends Close implements Valve {
	
	// Make

	/** Make a GetValve that will download get and put data in out(). */
	public GetValve(Update update, Get get, Range range) {
		this.update = update;
		this.get = get;
		meter = new Meter(range);
		out = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The Get we download from. */
	private final Get get;
	/** Our current DownloadLater, null if we don't have one right now. */
	private GetTask later;

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
	
	public Meter meter() { return meter; }
	private final Meter meter;
	
	public void start() {
		if (closed()) return;
		if (!meter.isDone() && later == null && out.hasSpace())
			later = new GetTask(update, get, meter.remain(), out);
	}
	
	public void stop() throws Exception {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			meter.add(later.result().stripe.size); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(); // All done
	}
	
	public boolean isEmpty() {
		return
			later == null && // No later using our bins
			out.isEmpty() && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
