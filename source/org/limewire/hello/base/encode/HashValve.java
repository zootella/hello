package org.limewire.hello.base.encode;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Meter;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class HashValve extends Close implements Valve {
	
	// Make

	/** Make a HashValve that will take data from in() and hash it. */
	public HashValve(Update update, Range range) {
		this.update = update;
		this.hash = new Hash();
		meter = new Meter(range);
		in = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The Hash that hashes the data. */
	public final Hash hash;
	/** Our current task, null if we don't have one right now. */
	private HashTask task;

	/** Close this Valve so it gives up all resources and won't start again. */
	public void close() {
		if (already()) return;
		if (task != null) {
			task.close();
			task = null; // Discard the closed later so in() and out() work
		}
	}

	// Valve
	
	public Bin in() {
		if (task != null) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	public Bin out() { return null; }
	
	public Meter meter() { return meter; }
	private final Meter meter;
	
	public void start() {
		if (closed()) return;
		if (!meter.isDone() && task == null && in.hasData())
			task = new HashTask(update, hash, in, meter.remain());
	}

	public void stop() throws Exception {
		if (closed()) return;
		if (task != null && task.closed()) { // Our later finished
			meter.add(task.result().stripe.size); // If an exception closed later, result() will throw it
			task = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(); // All done
	}
	
	public boolean isEmpty() {
		return
			task == null && // No later using our bins
			in.isEmpty()  && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
