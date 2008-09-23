package org.limewire.hello.base.internet.tube;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.later.DownloadLater;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.state.old.OldClose;
import org.limewire.hello.base.state.old.OldUpdate;
import org.limewire.hello.base.time.Now;

public class TubeDown extends OldClose {
	
	public TubeDown(Tube tube) {
		this.tube = tube;
		update = new Update(new MyReceive());
		bay = new Bay();
	}
	
	public void close() {
		if (later != null) later.close();
	}
	
	private final Tube tube;
	
	private DownloadLater later;
	/** only touch bin when later is null. */
	private Bin bin;
	private Bay bay;
	
	private Update update;
	
	private boolean closed;

	
	
	/** Look at the Data this Tube has downloaded. */
	public Data download() {
		return bay.data();
	}

	/** Keep just this end part of the Data this Tube has downloaded. */
	public void keep(Data data) {
		bay.keep(data.size());
		tube.update.send(); // Now we have room to download more
	}


	// When a worker object we gave our Update has progressed or completed, it calls this receive() method
	private class MyReceive implements Receive {
		public void receive() {
			if (closed) return;
			try {

				// We downloaded
				if (later != null && later.closed()) {
					
					bin = Bin.medium();
					bay.add(bin.data());
					bin = null;
					later = null;
					
					tube.timeResponse = new Now();
					tube.above.send();
				}
				
				// If we have room, start another download
				if (later == null && bay.size() < Bin.medium)
					later = new DownloadLater(update, tube.socket, bin);

			} catch (Exception e) { tube.exception = e; tube.close(); }
		}
	}
	

}
