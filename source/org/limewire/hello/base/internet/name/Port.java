package org.limewire.hello.base.internet.name;

import org.limewire.hello.base.exception.MessageException;

public class Port {
	
	// Make

	/** Make sure port is 0 through 65535 or throw a MessageException. */
	public Port(int port) throws MessageException {
		if (port < minimum || port > maximum) throw new MessageException();
		this.port = port;
	}
	
	// Look
	
	/** The port number, 0 through 65535. */
	public final int port;

	// Define

	/** 0, the minimum possible port number. */
	public static final int minimum = 0;
	/** 65535, 0xffff in 2 bytes, the maximum possible port number. */
	public static final int maximum = 65535;
}
