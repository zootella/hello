package org.limewire.hello.base.download;

import java.util.Map;

import org.limewire.hello.base.download.resume.GetMachineAdvanced.MyModel;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;

public class DownloadModel extends Close {
	
	// this is the pattern!
	/*
	public Download(String address, Path folder) {
		try {
			
			
			
			
		} catch (Exception e) { exception = e; close(); }
		model.changed();
	}
	
	
	
	public Download(String user, Path folder) {
		
		
	}
	*/
	
	
	
	// Make

	/** Make a Download that can download a file with pause and progress. */
	public DownloadModel() {
		model = new MyModel();
	}
	
	/** Close the file and sockets we have open. */
	public void close() {
		if (already()) return;
	}
	
	// Command
	
	public void enter(String user) {
		
	}
	
	public void get() {
		
	}
	
	public void pause() {
		
	}
	
	public void open() {
		
	}
	
	public void openSavedFile() {
		
	}
	
	public void openContainingFolder() {
		
	}
	
	public void reset() {
		
	}
	
	public void delete() {
		
	}
	
	public void remove() {
		
	}
	
	// Model

	/** This object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model {
		
		public boolean canEnter() { return false; }
		public boolean canGet() { return false; }
		public boolean canPause() { return false; }
		public boolean canOpen() { return false; }
		public boolean canOpenSavedFile() { return false; }
		public boolean canOpenContainingFolder() { return false; }
		public boolean canReset() { return false; }
		public boolean canDelete() { return false; }

		public String status() { return ""; }
		public String name() { return ""; }
		public String size() { return ""; }
		public String type() { return ""; }
		public String savedTo() { return ""; }

		/** Compose text about our current state to show the user. */
		public Map<String, String> view() { return null; }

		/** The outer object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer object. */
	private DownloadModel me() { return this; }
}
