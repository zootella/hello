package org.limewire.hello.base.encode;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Open;
import org.limewire.hello.base.file.OpenTask;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.file.ReadValve;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Progress;
import org.limewire.hello.base.valve.Flow;

/** Hash data in a file and see the progress. */
public class HashFlow extends Close {
	
	// Make
	
	/** Hash range data in the file at path. */
	public HashFlow(Update up, String path, Range range) {
		this.up = up;
		update = new Update(new MyReceive());
		this.path = path;
		this.range = range;
		progress = new Progress("hash", "Hashing", "Hashed");
		update.send(); // Move things forward
	}

	/** The Update above us we tell when we've changed. */
	private final Update up;
	/** Our Update we give to objects below to tell us when they've changed. */
	private final Update update;
	
	// Open path into file with open
	private final String path;
	private OpenTask open;
	private File file;

	/** The Range in file we hash. */
	private Range range;

	/** Our list of Valve objects that read file and hash it, null before we make it. */
	private Flow list;
	/** The first Valve in list, reads data from file. */
	private ReadValve readValve;
	/** The last Valve in list, hashes data. */
	private HashValve hashValve;

	/** Close our access to the file we hash. */
	public void close() {
		if (already()) return;
		Close.close(file);
		Close.close(open);
		Close.close(list);
		Close.close(readValve);
		Close.close(hashValve);
		progress.pause();
		up.send();
	}
	
	// Look
	
	/** Our progress hashing file data. */
	public final Progress progress;
	
	/** Once we're closed, call result() for the hash value we computed or the Exception that made us give up. */
	public Data result() throws Exception {
		if (!closed()) throw new IllegalStateException();             // Don't call this until closed
		if (exception != null) throw exception;                       // An exception made us give up
		if (value == null) throw new IllegalStateException("cancel"); // Closed without exception or result, must have been cancelled
		return value;
	}
	private Exception exception;
	private Data value;

	// Receive

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				// File
				if (file == null && open == null)
					open = new OpenTask(update, new Open(new Path(path), null, Open.read));
				if (file == null && open != null && open.closed()) {
					file = open.result();
					range = range.know(file.size()); // Update range now that we know the file size
					progress.size(range.size); // Tell progress how much we're going to hash
					up.send();
				}

				// List
				if (list == null && file != null) {
					list = new Flow(update, false, false);
					readValve = new ReadValve(update, file, range);
					hashValve = new HashValve(update, range);
					list.list.add(readValve);
					list.list.add(hashValve);
					up.send();
				}
				
				// Move data down the list and get progress
				if (list != null) {
					list.move();
					progress.done(hashValve.meter().done()); // Tell progress hashValve's done distance
					up.send();
				}

				// The list is done
				if (list != null && list.isEmpty() && !list.closed()) {
					list.close();
					value = hashValve.hash.done();
					close();
					up.send();
				}

			} catch (Exception e) { exception = e; close(); }
		}
	}
}
