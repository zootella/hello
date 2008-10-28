package org.limewire.hello.spin.utility;

/** Extend Spin to make an object that tries to do something over and over again very fast. */
public abstract class Spin extends Close {

	/** The kind of Spin object this is. */
	public abstract String title();

	/** How fast this Spin object is spinning. */
	public abstract Speed speed();
}
