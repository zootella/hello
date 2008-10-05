package org.limewire.hello.base.flow;

import java.util.LinkedHashMap;
import java.util.Map;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;

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
	
	private Path path;
	private HashFile hash;
	private String status;
	private String value;
	

	
	// Command
	
	public void start(String user) {
		if (!model.canStart()) return;
		
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
	
	public void stop() {
		if (!model.canStop()) return;
	}
	
	public void reset() {
		if (!model.canReset()) return;

		path = null;
		
		Close.close(hash);
		hash = null;
		
		status = "";
		value = "";

		model.changed();
	}
	
	
	
	// Update

	/** When an object below us has changed, it calls our update's receive method. */
	private final Update update;
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			
			if (hash != null) {
				model.changed();
			}
				
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
		
		public boolean canStop() {
			return hash != null;
		}
		
		public boolean canReset() {
			return true;
		}
		
		/** Status text. */
		public String status() {
			if (Text.hasText(status)) return status;
			if (hash == null) return "no hash";
			return hash.progress.describeStatus();
		}

		/** Size text. */
		public String size() {
			if (hash == null) return "";
			return hash.progress.describeSize();
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
