package org.limewire.hello.base.valve;

import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.later.DownloadLater;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Update;

public class ReadValve extends Close implements Valve {
	
	// Make

	/** Make a ReadValve that will read stripe from file. */
	public ReadValve(Update update, FileChannel file, Stripe stripe) {
		this.update = update;
		this.file = file;
		out = Bin.medium();
	}
	
	/** The Update for the Tube we're in. */
	private final Update update;
	/** The file we read from. */
	private final FileChannel file;
	
	public final Stripe stripe;
	/** Our current ReadLater that reads data from file to out, null if we don't have one right now. */
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
