package org.limewire.hello.base.internet.tube;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.later.UploadLater;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.state.old.OldClose;
import org.limewire.hello.base.state.old.OldUpdate;
import org.limewire.hello.base.time.Now;

public class TubeUp extends Close {
	
	public TubeUp(Tube tube) {
		this.tube = tube;
		update = new Update(new MyReceive());
		bin = Bin.medium();
	}
	
	public void close() {
		if (later != null) later.close();
	}
	
	private final Tube tube;
	
	private UploadLater later;
	/** only touch bin when later is null. */
	private Bin bin;
	
	Update update;
	
	private boolean closed;

	/** Upload data through this Tube, removes what it takes. */
	public void upload(Data data) {
		if (later != null) return; // Already trying to upload something, try again later
		
		bin.add(data);
		update.send();
	}

	

	// When a worker object we gave our Update has progressed or completed, it calls this receive() method
	private class MyReceive implements Receive {
		public void receive() {
			if (closed) return;
			try {
				
				// We uploaded
				if (later != null && later.closed()) {
					
					tube.timeResponse = new Now();
					tube.above.send();
				}
				
				// Still more to upload
				if (later == null && bin.hasData())
					later = new UploadLater(update, tube.socket, bin);

			} catch (Exception e) { tube.exception = e; tube.close(); }
		}
	}
	
	
	
	

}
