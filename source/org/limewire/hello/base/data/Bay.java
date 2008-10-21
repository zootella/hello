package org.limewire.hello.base.data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

import org.limewire.hello.base.file.File;
import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.size.Stripe;

// A Bay object holds data, growing to hold more you add to it.
// say one place in the program, probably in the javadoc or wiki page for bay
// this program holds teh following conventiosn when using ByteBuffers
// position and limit clip around the data, not the space afterwards
// nothing ever moves them, if a java method is going to move them, our code copies the ByteBuffer and gives the java a copy
public class Bay {

	// -------- Make a Bay --------
	
	/** Make a new empty Bay object that can hold data. */
	public Bay() {}
	/** Make a new empty Bay object prepared to hold size bytes of data. */
	public Bay(int size) { prepare(size); }

	/** Make a new Bay object with a copy of the given byte array in it. */
	public Bay(byte[] a) { add(a); }
	/** Make a new Bay object with the data of the given String in it. */
	public Bay(String s) { add(s); }
	/** Make a new Bay object with a copy of the data between b's position and limit in it, doesn't change b. */
	public Bay(ByteBuffer b) { add(b); }
	/** Make a new Bay object with a copy of d in it. */
	public Bay(Data d) { add(d); }
	
	// -------- Add data to the end --------
	
	/** Add a single byte to end of the data this Bay object holds. */
	public void add(byte y) { add(new Data(y)); }
	/** Copy the given byte array into this Bay, adding it to the end of the data this Bay holds. */
	public void add(byte[] a) { add(new Data(a)); }
	/** Convert the given String into data, and add it to the end of the data this Bay holds. */
	public void add(String s) { add(new Data(s)); }
	/** Copy the data between b's position and limit into the end of this Bay, doesn't change b. */
	public void add(ByteBuffer b) { add(new Data(b)); }
	/** Copy the data d views into this Bay, adding it to the end of the data this Bay holds. */
	public void add(Data d) {
		if (d.isEmpty()) return;      // Nothing given to add
		prepare(d.size());            // Prepare our ByteBuffer to hold d's size
		buffer.put(d.toByteBuffer()); // Copy d in, moves buffer's position forward to still clip out the empty space at the end
	}

	// -------- Look at the data this Bay is holding --------

	/** How much data this Bay object is holding, in bytes. */
	public int size() { return data().size(); }
	//TODO this would be faster if it just did the math
	
	/** Look at the data this Bay object holds. */
	public Data data() { return new Data(buffer()); }

	/**
	 * Make a read-only ByteBuffer with position and limit clipped around the data this Bay object holds.
	 * You can move the position without changing this Bay object.
	 */
	private ByteBuffer buffer() {
		
		// If we don't have a ByteBuffer, return a new empty one
		if (buffer == null) return ByteBuffer.allocate(0);
		
		// Make a new ByteBuffer clipped around our data, and return it
		ByteBuffer b = buffer.asReadOnlyBuffer(); // This doesn't copy the data
		b.position(offset); // Clip b's position and limit around our data
		b.limit(buffer.position());
		return b.slice(); // Return a ByteBuffer sized around the data
		//TODO the slice is unnecessary
	}
	
	// -------- Remove some data from the start --------

	/** Remove size bytes from the start of the data this Bay object holds. */
	public void remove(int size) {

		// Check the given size
		if (size < 0) throw new IllegalArgumentException(); // Can't be negative
		if (size == 0) return; // Nothing to remove
		if (buffer == null || size > size()) throw new IndexOutOfBoundsException(); // We don't have that much data
		
		// Remove the data
		if (size == size()) clear(); // Remove everything we've got
		else offset += size; // Move our offset past the chunk of data to remove
	}
	
	/** Remove data from the start of this Bay, keeping only the last size bytes. */
	public void keep(int size) {
		remove(size() - size); // Remove everything but size bytes
	}

	/** Clear all the data this Bay object is holding. */
	public void clear() {

		// We don't have any data to clear
		if (buffer == null) return;

		// Mark our buffer as empty
		buffer.clear(); // Moves position to the start and limit to the end
		offset = 0;     // No data to skip over at the start
	}

	// -------- How it works inside --------

	/**
	 * The ByteBuffer object that holds the data.
	 * The position and limit always clip around the empty space at the end of the buffer.
	 */
	private ByteBuffer buffer;

	/**
	 * The distance, in bytes, into the buffer where the data starts.
	 * There might be some removed data at the start, this offset will get you past it.
	 */
	private int offset;

	/** Prepare this Bay object to hold this many more bytes of data. */
	public void prepare(int more) {

		// Check the input
		if (more < 0) throw new IllegalArgumentException(); // Can't be negative
		if (more == 0) return; // No more space requested

		// We don't have a ByteBuffer to hold any data yet
		if (buffer == null) {

			// Make a new one exactly the right size
			buffer = allocate(more); // The position and limit clip out the whole empty thing

		// Our ByteBuffer isn't big enough to hold that much more data
		} else if (more > offset + buffer.remaining()) {

			// We'll make a new ByteBuffer, calculate how big it should be
			int size = ((size() + more) * 3) / 2; // It will be 2/3rds full
			if (size < 64) size = 64;             // At least 64 bytes

			// Replace our old ByteBuffer with a new, bigger one
			ByteBuffer b = allocate(size);
			b.put(buffer()); // Copy in all the data from the old one
			buffer = b;      // Point buffer at the new one, discarding our reference to the old one
			offset = 0;      // There's no removed data at the start of our new buffer

		// Our ByteBuffer will have enough space at the end once we shift the data to the start
		} else if (more > buffer.remaining()) {

			// Shift the data to the start of the ByteBuffer
			compact();
		}
	}

	/** Allocate a new empty Java ByteBuffer object with a capacity of size bytes. */
	private static ByteBuffer allocate(int size) {

		// If the size is 8 KB or bigger, allocate the memory outside of Java in the native process space 
		if (size < big) return ByteBuffer.allocate(size);       // Back it with a Java byte array
		else            return ByteBuffer.allocateDirect(size); // Get memory outside of Java
	}

	/** Shift the data this Bay object holds to the start of our ByteBuffer. */
	private void compact() {
		
		// We don't have any data, or it's already at the start
		if (size() == 0 || offset == 0) return;
		
		// Clip our buffer's position and limit around the data
		buffer.limit(buffer.position());
		buffer.position(offset);
		
		// Shift that data to the start
		buffer.compact(); // Also restores limit and position to again clip around the free space at the end
		
		// There's no data at the start to skip over any more
		offset = 0;
	}

	/**
	 * 8192 bytes, 8 KB, the program considers this size to be big.
	 * 
	 * The program uses this size in several ways.
	 * Given a size 8 KB or larger, allocate() will get memory from the native process space instead of using a Java byte array.
	 * TubeUpload and TubeDownload use this size as a boundary.
	 * 
	 * This size is based on information from the platform.
	 * By default, SocketChannel and DatagramChannel objects have 8 KB underlying send and receive buffers.
	 * To see this, call channel.socket().getSendBufferSize() and channel.socket().getReceiveBufferSize().
	 */
	public static final int big = 8 * 1024;
	//TODO replace with Bin.size
	//TODO stop using all below
	// note Bin.medium 8K is our TCP buffer size, and Bin.big 64K is our UDP buffer size
	
	// In

	/** Add the given stripe of data from file to this Bay, or throw an IOException. */
	public void read(File file, Stripe stripe) throws IOException {
		
		// Prepare enough room
		prepare((int)stripe.size);

		// Copy our buffer to read in the stripe
		ByteBuffer fill = buffer.duplicate(); // Copy buffer to move b's position and limit separately
		fill.limit(fill.position() + (int)stripe.size); // Clip position and limit around stripe size of space after our data
		
		// Copy data from the file to this Bay
		int did = file.file.getChannel().read(fill, stripe.i); // Read from file at stripe.i to fill between b's position and limit
		if (did != stripe.size) throw new IOException("did " + did); // Make sure we got everything
		if (fill.hasRemaining()) throw new IOException("remain");

		// Move buffer's position past the new data we wrote
		buffer.position(fill.position());
	}

	// -------- Internet transfer --------

	/**
	 * Upload the data this Bay object holds into a given SocketChannel.
	 * Uploads the amount of data the channel can take right now.
	 * Removes the data it uploads from this Bay object.
	 * 
	 * @param c The SocketChannel this method will upload data into
	 * @return  The number of bytes we uploaded
	 */
	public int oldUpload(SocketChannel c) throws IOException {
		
		// Make sure we have data to upload
		if (size() == 0) return 0;
		//TODO throw IOException on nothing to upload
		
		// Clip our buffer's position and limit around our data
		buffer.limit(buffer.position()); // Normally, they clip the free space at the end
		buffer.position(offset);

		// Upload our data into the given channel
		int uploaded = c.write(buffer); // Moves position forward past the data it uploads
		if (uploaded == -1) throw new IOException(); // Make sure write() didn't report end of stream

		// The channel took all our data
		if (buffer.remaining() == 0) {

			// Mark us as empty
			clear();

		// We still have some data left to try uploading next time
		} else {

			// Clip offset and position around our data, and position and limit around the space beyond
			offset = buffer.position();
			buffer.position(buffer.limit());
			buffer.limit(buffer.capacity());
		}

		// Return the number of bytes the channel took from us
		return uploaded;
		//TODO throw IOException on nothing uploaded
		//TODO no counting, have the caller measure the size
	}

	/**
	 * Download data from a given SocketChannel, adding it to the data this Bay object holds.
	 * Shifts our data to the start, and fills the space at the end with data from the channel.
	 * Doesn't prepare more space in this Bay object.
	 * 
	 * @param c The SocketChannel this method will download data from
	 * @return  The number of bytes we downloaded
	 */
	public int oldDownload(SocketChannel c) throws IOException {

		// Make sure we're not already full
		if (buffer == null || size() == buffer.capacity()) return 0;

		// Shift any data in our ByteBuffer to the start
		compact();

		// Download data from the channel, adding it to our ByteBuffer
		int downloaded = c.read(buffer);               // Moves position forward so it still clips out the empty space at the end
		if (downloaded == -1) throw new IOException(); // Make sure read() didn't report end of stream
		return downloaded;                             // Return the number of bytes the channel gave us
		
		//TODO throw IOException on downloaded nothing
		//TODO no counting, have the caller measure the size
	}

	/**
	 * Download the data of a single UDP packet from a given DatagramChannel, putting it in this Bay object.
	 * Clears this Bay before downloading the new data.
	 * If the UDP packet is bigger than this Bay can hold, Java chops the end off without telling us.
	 * 
	 * @param c The DatagramChannel this method will download data from.
	 * @return  The IP address and port number of the computer that sent us the UDP packet, in an IpPort object.
	 *          null if there is no packet has arrived for us to download right now.
	 */
	public IpPort oldReceive(DatagramChannel c) throws IOException {

		// Clear this Bay so it can hold as much as possible
		clear();
		
		// Download data from the channel, putting it at the start of our ByteBuffer
		InetSocketAddress a = (InetSocketAddress)c.receive(buffer); // Moves position forward
		if (a == null) return null; // There is no packet for us right now
		
		// Return the IP address and port number in an IpPort object
		return new IpPort(a);
	}
}
