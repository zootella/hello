package org.limewire.hello.base.internet.packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.internet.name.Port;
import org.limewire.hello.base.state.Close;

/** A UDP socket bound to port that can send and receive packets. */
public class ListenPacket extends Close {

	// Open

	/** Bind a new TCP server socket to port. */
	public ListenPacket(Port port) throws IOException {
		this.port = port;
		channel = DatagramChannel.open();
		if (channel.socket().getSendBufferSize() < Bin.big) // Handle 64 KB UDP packets, the default is just 8 KB
			channel.socket().setSendBufferSize(Bin.big);
		if (channel.socket().getReceiveBufferSize() < Bin.big)
			channel.socket().setReceiveBufferSize(Bin.big);
		channel.socket().bind(new InetSocketAddress(port.port));
	}

	// Look

	/** The port number this socket is bound to. */
	public final Port port;
	/** The Java DatagramChannel object that is this UDP socket. */
	public final DatagramChannel channel;

	// Close

	/** Stop listening on port. */
	@Override public void close() {
		if (already()) return;
		try { channel.close(); } catch (IOException e) {}
	}
}
