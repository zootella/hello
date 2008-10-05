package org.limewire.hello.base.internet.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.time.Duration;
import org.limewire.hello.base.time.Now;

/** An open TCP socket connection. */
public class Socket extends Close {

	// Open

	/** Open a new TCP socket connection to the given IP address and port number or throw an IOException. */
	public Socket(IpPort ipPort) throws IOException {
		Now start = new Now();
		outgoing = true;
		channel = SocketChannel.open();
		if (!channel.connect(ipPort.toInetSocketAddress())) throw new IOException("connect false");
		this.ipPort = ipPort;
		size();
		connect = new Duration(start);
	}
	
	/** Make a new Socket for the given SocketChannel that just connected in to us. */
	public Socket(SocketChannel channel) throws IOException {
		if (!channel.isConnected()) throw new IOException("not connected"); // Make sure the given channel is connected
		outgoing = false;
		this.channel = channel;
		ipPort = new IpPort((InetSocketAddress)channel.socket().getRemoteSocketAddress());
		size();
		connect = null;
	}
	
	/** Increase the socket buffer size if necessary. */
	private void size() throws IOException {
		if (channel.socket().getSendBufferSize() < Bin.medium)
			channel.socket().setSendBufferSize(Bin.medium);
		if (channel.socket().getReceiveBufferSize() < Bin.medium)
			channel.socket().setReceiveBufferSize(Bin.medium);
	}
	
	// Look

	/** The Java SocketChannel object that is this TCP socket connection. */
	public final SocketChannel channel;
	/** The IP address and port number of the peer on the far end of this connection. */
	public final IpPort ipPort;
	/** true if we connected out to the peer, false if the peer connected in to us. */
	public final boolean outgoing;
	/** How long we took to connect, null if the peer connected in to us. */
	public final Duration connect;
	
	// Close

	/** Disconnect this TCP socket connection. */
	@Override public void close() {
		if (already()) return;
		try { channel.close(); } catch (Exception e) {}
	}
}
