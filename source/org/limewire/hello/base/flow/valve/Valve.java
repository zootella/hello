package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;

/** Ways to control a Valve. */
public interface Valve {

	/** Access this Valve's input Bin to give it data, null if started. */
	public Bin in();
	/** Access this Valve's output Bin to get the data it processed, null if started. */
	public Bin out();
	
	/** Have this Valve stop if it's done, and throw the exception that stopped it. */
	public void stop() throws Exception;
	/** Tell this Valve to start if possible. */
	public void start();
	
	/** Close this Valve so it gives up all resources and doesn't change again. */
	public void close();
}
