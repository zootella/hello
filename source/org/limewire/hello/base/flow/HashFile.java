package org.limewire.hello.base.flow;

import org.limewire.hello.base.encode.Hash;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.flow.valve.HashValve;
import org.limewire.hello.base.flow.valve.ReadValve;
import org.limewire.hello.base.later.OpenLater;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;

public class HashFile extends Close {
	
	public HashFile(Update up, Path path) {
		this.up = up;
		
		update = new Update(new MyReceive());
		open = new OpenLater(update, path, false);
		
		hash = new Hash();
	}

	
	
	private final Update up;
	private final Update update;
	private final Hash hash;
	
	private File file;
	private OpenLater open;
	private ValveList list;

	public Exception exception() { return exception; }
	private Exception exception;

	// this object has pause(true) and paused()
	// this object has a Model which communicates up to views, or you could use it deeper
	

	@Override public void close() {
		if (already()) return;
		Close.close(file);
		Close.close(open);
		Close.close(list);
	}	
	
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				if (open != null && open.closed()) {
					file = open.result();
					open = null;
				}
				
				if (file != null && file.hasData() && list == null) {
					list = new ValveList(update, false, false);
					list.list.add(new ReadValve(list.update, file, new Stripe(0, file.size())));
					list.list.add(new HashValve(list.update, hash));
				}
				

			} catch (Exception e) { exception = e; close(); }
		}
	}


	
	
	
	

}
