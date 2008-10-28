package org.limewire.hello.base.internet.socket;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Meter;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class UploadValve extends Close implements Valve {
	
	// Make

	/** Make an UploadValve that will upload data into socket. */
	public UploadValve(Update update, Socket socket, Range range) {
		this.update = update;
		this.socket = socket;
		meter = new Meter(range);
		in = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The socket we upload to. */
	private final Socket socket;
	/** Our current UploadLater that uploads data from in to socket, null if we don't have one right now. */
	private UploadTask later;

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
	
	public Meter meter() { return meter; }
	private final Meter meter;

	public void start() {
		if (closed()) return;
		if (!meter.isDone() && later == null && in.hasData())
			later = new UploadTask(update, socket, meter.remain(), in);
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
			in.isEmpty()  && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
