package org.limewire.hello.base.data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.internet.IpPort;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.time.Move;
import org.limewire.hello.base.time.Now;
import org.limewire.hello.base.time.Size;

public class Bin {
	
	// Static
	
	/** 8 KB in bytes, the capacity of a normal Bin, our buffer size for TCP sockets. */
	public static final int medium = 8 * Size.kilobyte;
	/** 64 KB in bytes, the capacity of a big Bin, our buffer size for UDP packets. */
	public static final int big = 64 * Size.kilobyte;

	// Make
	
	/** Get a new empty 8 KB Bin. */
	public static Bin medium() {
		return new Bin(medium);
	}
	
	/** Get a new empty 64 KB Bin. */
	public static Bin big() {
		return new Bin(big);
	}

	/** Make a new Bin with the given ByteBuffer inside. */
	private Bin(int size) {
		this.buffer = ByteBuffer.allocateDirect(size);
	}
	
	// Look

	/** Look at the Data in this Bin. */
	public Data data() {
		ByteBuffer b = buffer.asReadOnlyBuffer(); // This doesn't copy the data
		b.flip(); // Clip b's position and limit around our data
		b = b.slice(); // Size b around the data
		return new Data(b); // Make it into a new Data object
	}

	/** The number of bytes of data in this Bin, 0 if empty. */
	public int size() {
		return buffer.position();
	}
	
	/** The total number of bytes this Bin is capable of holding. */
	public int capacity() {
		return buffer.capacity();
	}

	/** The amount of free space in this Bin, 0 if totally full. */
	public int space() {
		return capacity() - size();
	}
	
	/** true if this Bin has at least 1 byte of data. */
	public boolean hasData() { return size() != 0; }
	/** true if this Bin has no data, not even 1 byte. */
	public boolean isEmpty() { return size() == 0; }
	/** true if this Bin has at least 1 byte of space. */
	public boolean hasSpace() { return size() != capacity(); }
	/** true if this Bin is half space or more. */
	public boolean halfSpace() { return size() <= capacity() / 2; }
	/** true if this Bin is completely full of data, with no space for even 1 more byte. */
	public boolean isFull() { return size() == capacity(); }
	
	// Change

	/** Move as much data as fits from bin to this one. */
	public void add(Bin bin) {
		if (bin.isEmpty() || isFull()) { // Nothing given or no space here
		} else if (isEmpty() && capacity() == bin.capacity()) { // We're empty and have the same capacity
			ByteBuffer b = bin.buffer; // Switch buffers
			bin.buffer = buffer;
			buffer = b;
		} else { // Move some data in
			Data data = bin.data();
			add(data);
			bin.keep(data.size()); // Have bin keep only what add didn't take
		}
	}

	/** Move as much data as fits from bay to this Bin, removing what we take from bay. */
	public void add(Bay bay) {
		Data data = bay.data();
		add(data);
		bay.keep(data.size()); // Have bay keep only what add didn't take
	}

	/** Move as much data as fits from data to this Bin, removing what we take from data. */
	public void add(Data data) {
		if (data.isEmpty() || isFull()) return; // Nothing given or no space here
		try {
			int did = Math.min(data.size(), space()); // Figure out how many bytes we can move
			Data d = data.start(did);                 // Clip d around that size
			buffer.put(d.toByteBuffer());             // Copy in the data
			data.remove(did);                         // Remove what we took from the given Data object
		} catch (ChopException e) { throw new CodeException(); }
	}

	/** Remove size bytes from the start of the data in this Bin. */
	public void remove(int size) {
		if (size < 0 || size > size()) throw new IndexOutOfBoundsException(); // Can't be negative or more data than we have
		if (size == 0) return; // Nothing to remove
		buffer.limit(buffer.position()); // Clip around the data we're going to keep
		buffer.position(size);
		buffer.compact(); // Slide that data to the start and clip position and limit around the space at the end
	}
	
	/** Remove data from the start of this Bin, keeping only the last size bytes. */
	public void keep(int size) {
		remove(size() - size); // Remove everything but size bytes
	}

	/** Remove all the data this Bin is holding, leaving it empty. */
	public void clear() {
		buffer.position(0); // Move position back to the start to clip out the whole thing as empty
	}

	// Buffer

	/**
	 * Our ByteBuffer object that holds the data.
	 * The position and limit always clip around the empty space at the end of the buffer.
	 * The data always starts at the start.
	 */
	private ByteBuffer buffer;

	/** Copy our buffer clipped around space for moving data in. */
	private ByteBuffer in() { return in(space()); }
	/** Copy our buffer clipped around space bytes of space for moving data in. */
	private ByteBuffer in(int space) {
		if (space < 1 || space > space()) throw new IndexOutOfBoundsException();
		ByteBuffer b = buffer.duplicate();
		b.limit(b.position() + space);
		return b;
	}
	
	/** Make sure we did at least 1 byte and position moved forward correctly. */
	private void inCheck(int did, ByteBuffer space) throws IOException {
		if (did < 1) throw new IOException("did " + did); // Hit the end got nothing
		if (buffer.position() + did != space.position()) throw new IOException("position"); // Moved position forward incorrectly
	}
	
	/** Save our buffer after moving data in. */
	private void inDone(ByteBuffer space) {
		space.limit(space.capacity());
		buffer = space;
	}

	/** Copy our buffer clipped around data for moving data out. */
	private ByteBuffer out() { return out(size()); }
	/** Copy our buffer clipped around size bytes of data at the start for moving data out. */
	private ByteBuffer out(int size) {
		if (size < 1 || size > size()) throw new IndexOutOfBoundsException();
		ByteBuffer b = buffer.duplicate();
		b.position(0);
		b.limit(size);
		return b;
	}
	
	/** Make sure we did at least 1 byte and position moved forward correctly. */
	private void outCheck(int did, ByteBuffer data) throws IOException {
		if (did < 1) throw new IOException("did " + did); // Error or wrote nothing
		if (did != data.position()) throw new IOException("position"); // Moved position forward incorrectly
	}
	
	/** Save our buffer after moving data out. */
	private void outDone(ByteBuffer data) {
		data.limit(buffer.position());
		data.compact(); // Slide the data to the start and clip position and limit around the space after it
		buffer = data;
	}
	
	// File
	
	/** Add 1 byte or more from the start of stripe in file to this Bin, or throw an IOException. */
	public Move read(FileChannel file, Stripe stripe) throws IOException {
		stripe = stripe.begin(space()); // Don't try to read more bytes than we have space for
		ByteBuffer space = in((int)stripe.size);
		Now start = new Now();
		int did = file.read(space, stripe.i); // Read from the file at i and move space.position forward
		inCheck(did, space);
		inDone(space);
		return new Move(start, stripe, did, null);
	}
	
	/** Move 1 byte or more from this Bin to stripe in file, or throw an IOException. */
	public Move write(FileChannel file, Stripe stripe) throws IOException {
		stripe = stripe.begin(size()); // Don't try to write more bytes than we have
		ByteBuffer data = out((int)stripe.size);
		Now start = new Now();
		int did = file.write(data, stripe.i); // Write to the file at i and move data.position forward
		outCheck(did, data);
		outDone(data);
		return new Move(start, stripe, did, null);
	}

	// Socket
	
	/** Download 1 or more bytes from socket, adding it to this Bin, or throw an IOException. */
	public Move download(SocketChannel socket) throws IOException {
		ByteBuffer space = in(); // We must have room for at least 1 byte
		Now start = new Now();
		int did = socket.read(space); // Download data and move position forward
		inCheck(did, space);
		inDone(space);
		return new Move(start, null, did, null);
	}

	/**
	 * Upload 1 or more bytes from this Bin into socket, or throw an IOException.
	 * Uploads as much data as socket will take in one call.
	 * Removes what's uploaded from this Bin.
	 */
	public Move upload(SocketChannel socket) throws IOException {
		ByteBuffer data = out();
		Now start = new Now();
		int did = socket.write(data); // Upload data and move position forward
		outCheck(did, data);
		outDone(data);
		return new Move(start, null, did, null);
	}

	// Packet
	
	/**
	 * Receive the data of a single UDP packet from gram, putting it in this empty Bin.
	 * @return A Move with the size of the packet we got and the IP address and port number it came from.
	 */
	public Move receive(DatagramChannel gram) throws IOException {
		if (hasData()) throw new IllegalStateException("not empty"); // Make sure we start out empty
		ByteBuffer space = in();
		Now start = new Now();
		InetSocketAddress a = (InetSocketAddress)gram.receive(space); // Receive a packet and move position forward
		if (a == null)                      throw new IOException("returned null");
		if (space.position() == 0)          throw new IOException("got nothing");
		if (space.position() == capacity()) throw new IOException("may have truncated");
		inDone(space);
		return new Move(start, null, size(), new IpPort(a));
	}
	
	/** Use gram to send the data in this Bin as a UDP packet to p. */
	public Move send(DatagramChannel gram, IpPort p) throws IOException {
		ByteBuffer data = out();
		Now start = new Now();
		int did = gram.send(data, p.toInetSocketAddress()); // Send a packet and move position forward
		if (did != size())         throw new IOException("not everything");
		if (data.remaining() != 0) throw new IOException("left remaining");
		outDone(data);
		return new Move(start, null, did, null);
	}

	// Direct
	
	/** Download data directly from socket to file, filling the start of stripe there, or throw an IOException. */
	public static Move down(SocketChannel socket, FileChannel file, Stripe stripe) throws IOException {
		Now start = new Now();
		long did = file.transferFrom(socket, stripe.i, stripe.size); // Download up to size bytes to the file at i
		if (did < 1) throw new IOException("did " + did); // Wrote nothing
		return new Move(start, stripe, did, null);
	}

	/** Upload data from the start of stripe in file directly to socket, or throw an IOException. */
	public static Move up(SocketChannel socket, FileChannel file, Stripe stripe) throws IOException {
		Now start = new Now();
		long did = file.transferTo(stripe.i, stripe.size, socket); // Upload up to size bytes from the file at i
		if (did < 1) throw new IOException("did " + did); // Wrote nothing
		return new Move(start, stripe, did, null);
	}
	
	// Close
	
	/** Close the given channel, takes null and ignores exception. */
	public static void close(SelectableChannel channel) {
		if (channel == null) return; // Nothing to close
		try {
			channel.close(); // It's OK if channel is already closed TODO does this block?
		} catch (IOException e) {} // Ignore exception
	}
}
