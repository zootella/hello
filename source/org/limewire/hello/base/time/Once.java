package org.limewire.hello.base.time;

public class Once {
	
	
	private boolean done;

	public boolean once() {
		if (done) return false;
		done = true;
		return true;
	}

}
