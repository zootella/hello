package org.limewire.hello.base.flow;

import java.io.IOException;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.flow.valve.HashValve;
import org.limewire.hello.base.flow.valve.ReadValve;
import org.limewire.hello.base.later.OpenLater;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Progress;

/** Hash data in one file and see the progress. */
public class HashTask extends Close {
	
	// Make
	
	/**
	 * Hash some data in a file.
	 * 
	 * @param up     The Update we'll tell when we've changed
	 * @param path   A Path to the file to hash, or null to supply file instead
	 * @param file   The open File to hash, or null to supply path instead
	 * @param stripe The Stripe in the file to hash, or null to hash the whole thing
	 */
	public HashTask(Update up, Path path, File file, Stripe stripe) {
		if (path == null && file == null) throw new IllegalArgumentException(); // We need one or the other, not both, not neither
		if (path != null && file != null) throw new IllegalArgumentException();
		this.up = up;
		update = new Update(new MyReceive());
		this.path = path;
		this.file = file;
		this.stripe = stripe;
		progress = new Progress("hash", "Hashing", "Hashed");
		update.send(); // Move things forward
	}

	/** The Update above us we tell when we've changed. */
	private final Update up;
	/** Our Update we give to objects below to tell us when they've changed. */
	private final Update update;
	
	/** The Path to the File we hash. */
	private Path path;
	/** The open File we hash. */
	private File file;
	/** The stripe in file we hash, null to hash the whole thing. */
	private Stripe stripe;

	/** Opens the file at path, null after we used it. */
	private OpenLater open;
	
	/** Our list of Valve objects that read file and hash it, null before we make it. */
	private ValveList list;
	
	/** The first Valve in list that reads data from file. */
	private ReadValve readValve;
	/** The last Valve in list that hashes data. */
	private HashValve hashValve;

	/** Close our access to the file we hash. */
	public void close() {
		if (already()) return;
		Close.close(file);
		Close.close(open);
		Close.close(list);
		progress.pause();
		up.send();
	}
	
	// Look
	
	/** Our progress hashing file data. */
	public final Progress progress;
	
	/** Once we're closed, call result() for the hash value we computed or the Exception that made us give up. */
	public Data result() throws Exception {
		if (!closed()) throw new IllegalStateException();    // Must be closed
		if (exception != null) throw exception;              // If we had an exception, throw it
		if (value == null) throw new NullPointerException(); // Never return null
		return value;
	}
	private Exception exception;
	private Data value;

	// Receive

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				// Open the file
				if (file == null && open == null) {
					open = new OpenLater(update, path, false);
				}
				
				// Get the open file
				if (file == null && open != null && open.closed()) {
					file = open.result();
					if (stripe == null) stripe = file.stripe();
					if (stripe == null) throw new IOException("empty");
					progress.size(stripe.size); // Tell progress how much we're going to hash
					open = null;
					up.send();
				}

				// Make the list
				if (file != null && list == null) {
					list = new ValveList(update, false, false);
					readValve = new ReadValve(update, file, stripe);
					hashValve = new HashValve(update);
					list.list.add(readValve);
					list.list.add(hashValve);
					up.send();
				}
				
				// Move data down the list and get progress
				if (list != null) {
					list.move();
					progress.done(hashValve.distance()); // Tell progress hashValve's distance
					up.send();
				}

				// The list is done
				if (list != null && list.isEmpty()) {
					list.close();
					value = hashValve.hash.done();
					list = null;
					close();
					up.send();
				}

			} catch (Exception e) { exception = e; close(); }
		}
	}
}
