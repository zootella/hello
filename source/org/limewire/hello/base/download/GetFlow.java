package org.limewire.hello.base.download;

import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Open;
import org.limewire.hello.base.file.OpenTask;
import org.limewire.hello.base.file.WriteValve;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Progress;
import org.limewire.hello.base.valve.Flow;
import org.limewire.hello.base.web.Url;

/** Download data from a url and write it to a file. */
public class GetFlow extends Close {
	
	// Make
	
	/** Download range in url to open.path. */
	public GetFlow(Update up, Url url, Range range, Open open) {
		this.up = up;
		update = new Update(new MyReceive());
		this.url = url;
		this.range = range;
		this.open = open;
		progress = new Progress("get", "Getting", "Downloaded");
		update.send(); // Move things forward
	}

	/** The Update above us we tell when we've changed. */
	private final Update up;
	/** Our Update we give to objects below to tell us when they've changed. */
	private final Update update;

	// Open url into get with openGet
	private final Url url;
	private OpenGetTask openGet;
	public Get get() { return get; }
	private Get get;

	// Open path into file with openFile
	private final Open open;
	private OpenTask openFile;
	private File file;

	/** The Range we download and write. */
	private Range range;
	
	/** Our list of Valve objects that download get and write it to file, null before we make it. */
	private Flow list;
	/** The first Valve in list, downloads data from get. */
	private GetValve getValve;
	/** The last Valve in list, writes data to file. */
	private WriteValve writeValve;

	/** Close our access to the file we hash. */
	public void close() {
		if (already()) return;
		Close.close(file);
		Close.close(openGet);
		Close.close(openFile);
		Close.close(list);
		Close.close(getValve);
		Close.close(writeValve);
		progress.pause();
		up.send();
	}
	
	// Look
	
	/** Our progress writing downloaded data to file. */
	public final Progress progress;

	/** Once we're closed, call result() for the Exception that made us give up. */
	public void result() throws Exception {
		if (!closed()) throw new IllegalStateException(); // Don't call this until closed
		if (exception != null) throw exception;           // An exception made us give up
	}
	private Exception exception;

	// Receive

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				// Get
				if (get == null && openGet == null) // Open our Get
					openGet = new OpenGetTask(update, url, range);
				if (get == null && openGet != null && openGet.closed()) { // Get our open Get
					get = openGet.result();
					range = range.know(get.size());
					progress.size(range.size);
					up.send();
				}

				// File
				if (file == null && openFile == null) // Open our File
					openFile = new OpenTask(update, open);
				if (file == null && openFile != null && openFile.closed()) { // Get our open File
					file = openFile.result();
					up.send();
				}

				// List
				if (list == null && get != null && file != null) {
					list = new Flow(update, false, false);
					getValve = new GetValve(update, get, range);
					writeValve = new WriteValve(update, file, range);
					list.list.add(getValve);
					list.list.add(writeValve);
					up.send();
				}

				// Move data down the list and get progress
				if (list != null) {
					list.move();
					progress.done(writeValve.meter().done()); // Tell progress writeValve's done distance
					up.send();
				}

				// The list is done
				if (list != null && list.isEmpty() && !list.closed()) {
					list.close();
					close();
					up.send();
				}

			} catch (Exception e) { exception = e; close(); }
		}
	}
}
