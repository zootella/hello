package org.limewire.hello.base.state;

/** Have your class extend Receive to get events from Update and Delay objects. */
public interface Receive {

	/** Implement a receive() method that Java will call when the event happens. */
	public void receive();
}
