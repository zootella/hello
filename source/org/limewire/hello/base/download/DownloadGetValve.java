package org.limewire.hello.base.download;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.move.Move;
import org.limewire.hello.base.pattern.Size;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.Valve;

public class DownloadGetValve extends Close implements Valve {
	
	// Make

	/** Make a GetValve that will download size from get and put data in out(). */
	public DownloadGetValve(Update update, Get get) {
		this.update = update;
		this.get = get;
		out = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The Get we download from. */
	private final Get get;
	/** Our current DownloadLater, null if we don't have one right now. */
	private DownloadGetLater later;

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
		if (later == null && out.hasSpace())
			later = new DownloadGetLater(update, get, null, out);
	}
	
	public void stop() throws Exception {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			Move move = later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
		}
	}
	
	public boolean isEmpty() {
		return
			later == null && // No later using our bins
			out.isEmpty();   // No data we downloaded waiting for the next Valve to take
	}
}
