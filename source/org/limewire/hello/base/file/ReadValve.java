package org.limewire.hello.base.file;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.size.Meter;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class ReadValve extends Close implements Valve {
	
	// Make

	/** Make a ReadValve that will read stripe from file and put data in out(). */
	public ReadValve(Update update, File file, Range range) {
		this.update = update;
		this.file = file;
		meter = new Meter(range);
		out = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The File we read from. */
	private final File file;
	/** Our current task, null if we don't have one right now. */
	private ReadTask task;

	/** Close this Valve so it gives up all resources and won't start again. */
	public void close() {
		if (already()) return;
		if (task != null) {
			task.close();
			task = null; // Discard the closed later so in() and out() work
		}
	}
	
	// Use
	
	public Bin in() { return null; }
	
	public Bin out() {
		if (task != null) return null; // later's worker thread is using our bin, keep it private
		return out;
	}
	private Bin out;
	
	public Meter meter() { return meter; }
	private final Meter meter;
	
	public void start() {
		if (closed()) return;
		if (!meter.isDone() && task == null && out.hasSpace())
			task = new ReadTask(update, file, meter.remain(), out);
	}
	
	public void stop() throws Exception {
		if (closed()) return;
		if (task != null && task.closed()) { // Our later finished
			meter.add(task.result().stripe.size); // If an exception closed later, throw it
			task = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(); // All done
	}
	
	public boolean isEmpty() {
		return
			task == null && // No later using our bins
			out.isEmpty() && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
