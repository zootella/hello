package org.limewire.hello.base.encode;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.time.Progress;

/** Hash files with stop and progress. */
public class HashMachine extends Close {
	
	// Make

	/** Make a HashMachine that can hash files with stop and progress. */
	public HashMachine() {
		update = new Update(new MyReceive());
		model = new MyModel();
		reset(); // Start out blank
	}
	
	/** Close a file we have open. */
	public void close() {
		if (already()) return;
		reset();
		model.close();
	}
	
	/** The Path to the file we're hashing, null if none right now. */
	private String path;
	/** The HashTask we're using to hash a file, null if we don't have one right now. */
	private HashTask hash;
	/** The Progress that tells how our hashing is going, null if we don't have one right now. */
	private Progress progress;
	/** The Exception that made us give up, null if there isn't one right now. */
	private Exception exception;
	/** The hash value we computed, null if we don't have one right now. */
	private Data value;

	// Command

	/** Open and hash the file at the given path text from the user. */ 
	public void start(String user) {
		if (!model.canStart()) return;
		try {

			// Reset everything and start from the given user text
			reset();
			path = user;
			hash = new HashTask(update, path, null);
			progress = hash.progress; // Point progress at hash's progress, we may keep it longer than we do hash
			
		} catch (Exception e) { exception = e; }
		model.changed();
	}
	
	/** Stop the hashing we're doing right now, but keep the progress on the screen. */
	public void stop() {
		if (!model.canStop()) return;
		try {
			
			// Close and discard our hash
			Close.close(hash);
			hash = null;
			
		} catch (Exception e) { exception = e; }
		model.changed();
	}
	
	/** Reset to start from the beginning. */
	public void reset() {
		if (!model.canReset()) return;
		try {

			// Reset everything
			path = null;
			Close.close(hash);
			hash = null;
			progress = null;
			exception = null;
			value = null;
			
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

				// We have a hash doing something
				if (hash != null) {
					model.changed();
				}

				// Our hash closed
				if (hash != null && hash.closed()) {
					value = hash.result(); // Get its result and discard it
					hash = null;
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
			return path == null && hash == null && progress == null && exception == null && value == null;
		}

		/** true if we can stop the hashing we're doing. */
		public boolean canStop() {
			return hash != null && !hash.closed();
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
			if (value != null)
				return "Done";
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

		/** Hash value. */
		public String value() {
			if (value != null)
				return value.base16();
			return "";
		}

		/** Compose text about our current state to show the user. */
		public Map<String, String> view() {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("Status", status());
			map.put("Size", size());
			map.put("Value", value());
			return map;
		}

		/** The outer object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer object. */
	private HashMachine me() { return this; }
}
