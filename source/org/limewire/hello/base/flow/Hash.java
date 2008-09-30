package org.limewire.hello.base.flow;

import java.util.LinkedHashMap;
import java.util.Map;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.user.Refresh;

public class Hash extends Close {
	
	// Make

	public Hash() {
		update = new Update(new MyReceive());
		model = new MyModel();
		reset();
	}
	
	public void close() {
		if (already()) return;
		reset();
		model.close();
	}

	
	// Command
	
	public void start(String user) {
		reset();
		path = null;
		try {
			path = new Path(user);
		} catch (MessageException e) {
			status = "Cannot Open File";
			model.changed();
			return;
		}
		
		hash = new HashFile(update, path);
		
		
		model.changed();
	}
	
	public void reset() {

		path = null;
		
		Close.close(hash);
		hash = null;
		
		status = "";
		value = "";

		model.changed();
	}
	
	private Path path;
	private HashFile hash;
	private String status;
	private String value;
	
	
	// Update

	/** When an object below us has changed, it calls our update's receive method. */
	private final Update update;
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
				
			if (hash != null && hash.closed()) {
				Data d = hash.value;
				if (d != null)
					value = d.base16();
				Exception e = hash.exception();
				if (e != null)
					value = "exception: " + e.toString();
				hash = null;
				model.changed();
			}
		}
	}

	// Model

	/** This object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model {

		public boolean canStart() {
			return hash == null;
		}
		
		/** Status text. */
		public String status() {
			return Model.toString(status);
		}

		/** Hash value. */
		public String value() {
			return value;
		}

		/** Compose text about our current state to show the user. */
		public Map<String, String> view() {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("Status", status());
			map.put("Value",  value());
			return map;
		}

		/** The outer object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer object. */
	private Hash me() { return this; }
}
