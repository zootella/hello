package org.limewire.hello.base.time;

public class Once {
	
	//TODO don't use this, instead, check a reference for null or something
	
	private boolean done;

	public boolean once() {
		if (done) return false;
		done = true;
		return true;
	}

}
