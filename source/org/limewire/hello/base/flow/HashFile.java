package org.limewire.hello.base.flow;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.encode.Hash;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.flow.valve.HashValve;
import org.limewire.hello.base.flow.valve.ReadValve;
import org.limewire.hello.base.later.OpenLater;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;

public class HashFile extends Close {
	
	public HashFile(Update up, Path path) {
		this.up = up;
		update = new Update(new MyReceive());
		open = new OpenLater(update, path, false);
	}

	
	private final Update up;
	private final Update update;
	
	private File file;
	private OpenLater open;
	private ValveList list;

	public Exception exception() { return exception; }
	private Exception exception;
	
	public Data value;

	

	public void close() {
		if (already()) return;
		Close.close(file);
		Close.close(open);
		Close.close(list);
		up.send();
	}
	
	private ReadValve readValve;
	private HashValve hashValve;
	
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				// Get the open file
				if (open != null && open.closed()) {
					file = open.result();
					open = null;
					up.send();
				}

				// Make the list
				if (file != null && list == null) {
					list = new ValveList(update, false, false);
					readValve = new ReadValve(update, file);
					hashValve = new HashValve(update);
					list.list.add(readValve);
					list.list.add(hashValve);
					up.send();
				}
				
				// Move data down the list
				if (list != null) {
					list.move();
					up.send();
				}

				// The list is done
				if (list != null && readValve.remain() == null && list.isEmpty()) {
					list.close();
					value = ((HashValve)list.last()).hash.done();
					list = null;
					close();
					up.send();
				}

			} catch (Exception e) { exception = e; close(); }
		}
	}


	
	
	/** Give inner classes a link to this outer object. */
	private HashFile me() { return this; }
	
	

}
