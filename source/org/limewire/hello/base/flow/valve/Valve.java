package org.limewire.hello.base.flow.valve;

import org.limewire.hello.base.data.Bin;

/** Ways to control a Valve. */
public interface Valve {
	
	// Required

	/** Access this Valve's input Bin to give it data, null if in use or doesn't have one. */
	public Bin in();
	/** Access this Valve's output Bin to get the data it processed, null if in use or doesn't have one. */
	public Bin out();
	
	/** true if this Valve has its bins pulled inside for a Later to process data. */
	public boolean processing();

	/** true if this Valve has a Later that's closed, and calling stop() will produce its bins. */
	public boolean canStop();
	/** Have this Valve stop if it's done, and throw the exception that stopped it. */
	public void stop() throws Exception;
	
	/** true if this Valve has what it needs to start, calling start() will have it pull in its bins and make a Later. */
	public boolean canStart();
	/** Tell this Valve to start if possible. */
	public void start();
}
