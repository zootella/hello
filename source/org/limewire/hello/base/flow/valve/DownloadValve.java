package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.internet.Socket;
import org.limewire.hello.base.later.DownloadLater;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class DownloadValve extends Close implements Valve {
	
	// Make

	/** Make a DownloadValve that will download data from socket. */
	public DownloadValve(Update update, Socket socket) {
		this.update = update;
		this.socket = socket;
		out = Bin.medium();
	}
	
	/** The Update for the ValveList we're in. */
	private final Update update;
	/** The socket we download from. */
	private final Socket socket;
	/** Our current DownloadLater that downloads data from socket to out, null if we don't have one right now. */
	private DownloadLater later;

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
			later.result(); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
		}
	}
	
	/** Tell this Valve to start, if possible. */
	public void start() {
		if (closed()) return;
		if (later == null && out.hasSpace())
			later = new DownloadLater(update, socket, out);
	}

	/** A DownloadValve doesn't have an input bin. */
	public Bin in() { return null; }
	
	/** Access this Valve's output Bin to get the data it processed, null if started. */
	public Bin out() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return out;
	}
	private Bin out;
}
