package org.limewire.hello.base.download.resume;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.limewire.hello.base.download.Get;
import org.limewire.hello.base.download.GetFlow;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Open;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.web.Url;

/** Download a file with pause and progress. */
public class GetMachineAdvanced extends Close {
	
	// Make

	/** Make a DownloadMachine that can download a file with pause and progress. */
	public GetMachineAdvanced(GetSave save) {
		update = new Update(new MyReceive());
		model = new MyModel();
		this.save = save;
		list = new ArrayList<GetFlow>();
	}
	
	/** Close the file and sockets we have open. */
	public void close() {
		if (already()) return;
		for (GetFlow get : list)
			get.close();
		file.close();
	}
	
	private final GetSave save;
	
	/** true when we download and save this file successfully. */
	private boolean saved;//TODO change this to just be if closed() without exception
	/** null before something made us give up, or a String for the user that describes what happened. */
	private String cannot;
	
	private File file;
	private List<GetFlow> list;
	
	private boolean hasGetNotClosed() {
		for (GetFlow get : list)
			if (!get.closed())
				return true;
		return false;
	}
	
	private Get firstGet() {
		for (GetFlow get : list)
			return get.get();
		return null;
	}
	
	// Command
	
	public void get() {
		if (!model.canGet()) return;
		//TODO choose where to get the new file
		
		
		
//		list.add(new GetFlow(update, save.url, new Range(), new Open()));
	}
	
	public void pause() {
		if (!model.canPause()) return;
		
	}
	
	public void open() {
		if (!model.canOpen()) return;
		
	}
	
	public void openSavedFile() {
		if (!model.canOpenSavedFile()) return;
		
	}
	
	public void openContainingFolder() {
		if (!model.canOpenContainingFolder()) return;
		
	}
	
	public void delete() {
		if (!model.canDelete()) return;
		
	}

	// Update

	/** When an object below us has changed, it calls our update's receive method. */
	private final Update update;
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {


			} catch (Exception e) { model.changed(); }
		}
	}

	
	private Open open;
	
	
	// Model

	/** This object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model {
		
		public boolean canGet() { return !saved && cannot == null; }
		
		public boolean canPause() {
			return hasGetNotClosed();
		}
		
		public boolean canOpen() { return true; }
		
		public boolean canOpenSavedFile()        { return saved && open.path.existsFile(); }
		public boolean canOpenContainingFolder() { return saved && open.path.existsFile(); }
		public boolean canDelete()               { return saved && open.path.existsFile(); }

		public String status() {
			return "Get " + list.size();
		}
		
		public String name() {
			return "";
		}
		public String size() {
			return "";
		}
		public String type() {
			return "";
		}
		
		public String savedTo() {
			if (saved)
				return open.path.toString();
			else
				return "";
		}

		/** Compose text about our current state to show the user. */
		public Map<String, String> view() { return null; }

		/** The outer object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer object. */
	private GetMachineAdvanced me() { return this; }
}
