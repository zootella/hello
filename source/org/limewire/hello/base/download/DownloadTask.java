package org.limewire.hello.base.download;

import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Open;
import org.limewire.hello.base.file.OpenLater;
import org.limewire.hello.base.file.WriteValve;
import org.limewire.hello.base.pattern.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.valve.ValveList;
import org.limewire.hello.base.web.Url;

/** Download and save a file from a HTTP GET request and see the progress. */
public class DownloadTask extends Close {
	
	// Make
	
	/** Download url to path. */
	public DownloadTask(Update up, Url url, Range range, Open open) {
		this.up = up;
		update = new Update(new MyReceive());
		this.url = url;
		this.range = range;
		this.open = open;
		this.done = new Range(range.i, 0);
		update.send(); // Move things forward
	}

	/** The Update above us we tell when we've changed. */
	private final Update up;
	/** Our Update we give to objects below to tell us when they've changed. */
	private final Update update;

	// Open url into get with openGet
	private final Url url;
	public final Range range;
	private OpenGetLater openGet;
	private Get get;

	// Open path into file with openFile
	private final Open open;
	private OpenLater openFile;
	private File file;
	
	/** Our list of Valve objects that download a get and write it to file, null before we make it. */
	private ValveList list;
	/** The first Valve in list, downloads data from get. */
	private DownloadGetValve getValve;
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
		up.send();
	}
	
	// Look

	/** If an Exception made us give up, throw it. */
	public void exception() throws Exception { if (exception != null) throw exception; }
	private Exception exception;
	
	/** How much we've downloaded and how much more we plan to get. */
	public Range done() { return done; }
	private Range done;

	// Receive

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				// Get
				if (get == null && openGet == null) // Open our Get
					openGet = new OpenGetLater(update, url, range);
				if (get == null && openGet != null && openGet.closed()) { // Get our open Get
					get = openGet.result();
					range = get.range();
					up.send();
				}

				// File
				if (file == null && openFile == null) // Open our File
					openFile = new OpenLater(update, open);
				if (file == null && openFile != null && openFile.closed()) { // Get our open File
					file = openFile.result();
					up.send();
				}

				// List
				if (list == null && get != null && file != null) {
					list = new ValveList(update, false, false);
					getValve = new DownloadGetValve(update, get);
					writeValve = new WriteValve(update, file, range);
					list.list.add(getValve);
					list.list.add(writeValve);
					up.send();
				}

				// Move data down the list and get progress
				if (list != null) {
					list.move();
					range = writeValve.range();
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
