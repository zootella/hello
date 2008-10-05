package org.limewire.hello.base.internet.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.limewire.hello.base.internet.name.Port;
import org.limewire.hello.base.state.Close;

/** A TCP server socket bound to port that can listen for a new incoming connection. */
public class ListenSocket extends Close {

	// Open

	/** Bind a new TCP server socket to port. */
	public ListenSocket(Port port) throws IOException {
		this.port = port;
		channel = ServerSocketChannel.open();
		channel.socket().bind(new InetSocketAddress(port.port));
	}

	// Look

	/** The port number this socket is bound to. */
	public final Port port;
	/** The Java ServerSocketChannel object that is this TCP server socket. */
	public final ServerSocketChannel channel;
	
	// Close

	/** Stop listening on port. */
	@Override public void close() {
		if (already()) return;
		try { channel.close(); } catch (IOException e) {}
	}
}
