package org.limewire.hello.base.download;

import java.io.IOException;
import java.util.Map;

import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.Open;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Progress;
import org.limewire.hello.base.web.Url;

/** Download a file with stop and progress. */
public class GetMachine extends Close {
	
	// Make

	/** Make a GetMachine that can download a file with stop and progress. */
	public GetMachine() {
		update = new Update(new MyReceive());
		model = new MyModel();
		reset(); // Start out blank
	}
	
	/** Close the file and socket we have open. */
	public void close() {
		if (already()) return;
		reset();
		model.close();
	}

	/** The Url we download, null if none right now. */
	private Url url;
	/** The Path to the file we write, null if none right now. */
	private Path path;
	/** The GetFlow we're using to download a file, null if we don't have one right now. */
	private GetFlow get;
	/** The Progress that tells how our downloading is going, null if we don't have one right now. */
	private Progress progress;
	/** The Exception that made us give up, null if there isn't one right now. */
	private Exception exception;

	// Command

	/** Download the given url to the given path. */ 
	public void start(String userUrl, String userPath) {
		if (!model.canStart()) return;
		try {

			// Reset everything and start from the given user text
			reset();
			url = new Url(userUrl);
			path = new Path(userPath);
			get = new GetFlow(update, this.url, new Range(), new Open(this.path, null, Open.overwrite));
			
			progress = get.progress; // Point progress at get's progress, we may keep it longer than we do get
			
		} catch (Exception e) { exception = e; }
		model.changed();
	}
	
	/** Stop the downloading we're doing right now, but keep the progress on the screen. */
	public void stop() {
		if (!model.canStop()) return;
		try {
			
			// Close and discard our get
			Close.close(get);
			get = null;
			
		} catch (Exception e) { exception = e; }
		model.changed();
	}
	
	/** Reset to start from the beginning. */
	public void reset() {
		if (!model.canReset()) return;
		try {

			// Reset everything
			url = null;
			path = null;
			Close.close(get);
			get = null;
			progress = null;
			exception = null;
			
		} catch (Exception e) { exception = e; }
		model.changed();
	}

	// Update

	/** When an object below us has changed, it calls our update's receive method. */
	private final Update update;
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {

				// We have a get doing something
				if (get != null) {
					model.changed();
				}

				// Our get closed
				if (get != null && get.closed()) {
					get = null;
					model.changed();
				}

			} catch (Exception e) { exception = e; model.changed(); }
		}
	}

	// Model

	/** This object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model {

		/** true if we can start trying to hash a new file. */
		public boolean canStart() {
			return url == null && path == null && get == null && progress == null && exception == null;
		}

		/** true if we can stop the hashing we're doing. */
		public boolean canStop() {
			return get != null && !get.closed();
		}
		
		/** true, we can always reset. */
		public boolean canReset() {
			return true;
		}
		
		/** Status text. */
		public String status() {
			if (exception != null) {
				if (exception instanceof MessageException) return "Bad Path";
				if (exception instanceof IOException) {
					if      (exception.getMessage().equals("not found")) return "File Not Found";
					else if (exception.getMessage().equals("empty"))     return "File Empty";
					else                                                 return "Cannot Read File";
				}
				return "Cannot";
			}
			if (false)
				return "Done"; //TODO how do you signal done
			if (progress != null)
				return progress.describeStatus();
			return "";
		}

		/** Size text. */
		public String size() {
			if (progress != null)
				return progress.describeSize();
			return "";
		}

		/** Compose text about our current state to show the user. */
		public Map<String, String> view() { return null; }

		/** The outer object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer object. */
	private GetMachine me() { return this; }
}
