package org.limewire.hello.base.internet.old;


import java.io.IOException;
import java.nio.channels.SelectionKey;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.encode.Compress;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.time.Speed;
import org.limewire.hello.base.time.OldTime;

public class OldTubeDownload {

	// -------- Internal parts --------
	
	/** A reference up to the Tube object that we're a part of. */
	private OldTube tube;
	
	/** A Bay that holds decompressed data. */
	private Bay bay;
	/** A Bay that holds compressed data. */
	private Bay compressed;
	
	/** A reference to the first Bay in the chain from the Internet down to the program. */
	private Bay first;
	/** A reference to the last Bay in the chain from the Internet down to the program. */
	private Bay last;

	// -------- Make a new TubeDownload object --------
	
	/**
	 * Make a new TubeDownload object to download data from a TCP socket connection.
	 * 
	 * @param tube The Tube object this new TubeDownload will be a part of
	 */
	public OldTubeDownload(OldTube tube) {
		
		// Save the link up to the Tube we're in
		this.tube = tube;
		
		// Make our Bay for normal data, and point both first and last at it
		bay = new Bay(Bay.big); // Prepare this much space to download data from the channel
		first = bay; // It's the only Bay in the chain
		last = bay;

		// Make objects that keep records
		speed = new Speed();
		transfer = new OldTime();
		
		// We have room for data, tell the Selector we're interested in reading
		interest();
	}

	// -------- Download data --------
	
	/**
	 * The size of the data this TubeDownload object has downloaded and is holding.
	 * 
	 * @return The data size in bytes
	 */
	public int size() {
		
		// All our data is always in the last Bay in the chain
		return last.size();
	}

	/**
	 * The data this TubeDownload object has downloaded, and is holding.
	 * 
	 * @return A Data object that views the data
	 */
	public Data data() {
		
		// Get the data from the last Bay in the chain
		return last.data();
	}

	/**
	 * Remove a given number of bytes from the start of the data this TubeDownload object has downloaded and is holding.
	 * 
	 * @param size The size of the chunk to remove
	 */
	public void remove(int size) {
		
		// Remove the data from the last Bay in the chain
		last.remove(size);
		interest(); // That freed up room, tell the Selector we're interested in downloading again
	}

	// -------- Interact with the Selector --------

	/**
	 * The Selector says our channel has some data.
	 * Download as much as we can from it into this TubeDownload object.
	 */
	public void selectDownload() throws IOException {
		
		// The last Bay in our chain is full, read interest should be off, the Selector shouldn't be telling us to download
		if (last.size() >= Bay.big) return; // Don't get more data, as the last Bay in our chain would grow huge
		
		// Move data from our channel to the first Bay in our chain
		int n = first.oldDownload(tube.channel);
		speed.add(n);               // Report the distance we just travelled to our Speed object
		if (n != 0) transfer.set(); // If we got data, set the last transfer time to now
		
		// Decompress the data if necessary, record the distance, and update our interest in downloading more
		decompress(n);
	}

	/**
	 * Decompress our data if necessary, record the distance, and update our interest in downloading more.
	 * 
	 * @param n The number of bytes of data we just downloaded, or realized we need to decompress
	 */
	private void decompress(int n) throws IOException {
		
		// The size of the data we got before and after decompression
		int sizeCompressed, size;
		sizeCompressed = size = n; // If decompression is off, the sizes are the same
		
		// If decompression is on, decompress the data from compressed to bay
		try {
			if (compress != null) size = compress.decompressAdd(bay, compressed);
		} catch (MessageException e) { throw new IOException(e); } // Bad compressed data, disconnect from this peer
		
		// Add to the total distances travelled
		distanceCompressed += sizeCompressed;
		distance += size;

		// Tell the Selector if we're still interested in downloading or not
		interest();
	}

	/**
	 * Tell the Selector if we are interested in downloading data right now or not.
	 * Call interest() when this TubeDownload object changes to keep the Selector informed.
	 */
	private void interest() {
		
		// If the last Bay in our chain doesn't have a big amount of data yet, we are interested in reading more
		tube.select.interest(tube.channel, SelectionKey.OP_READ, last.size() < Bay.big);
	}
	
	// -------- Compression --------
	
	/**
	 * The Compress object this TubeDownload will use to decompress data it downloads.
	 * To determine if we have decompression turned on, see if compress is null or not.
	 */
	private Compress compress;
	
	/**
	 * Have this TubeDownload decompress the data it downloads.
	 * The data you've removed with remove() or cut() won't get decompressed.
	 * The data after that in this TubeDownload will, as will all the data downloaded after it.
	 */
	public void enableCompression() throws IOException {
		
		// Make a Compress object to stream decompress the data we download
		compress = new Compress(); // Now that compress isn't null, we know that decompression is turned on
		
		// Expand the Bay chain for compression
		compressed = bay;   // The data in bay needs to be compressed
		bay = new Bay();    // Make a new Bay for the data after we decompress it
		first = compressed; // Point first and last at the first and last Bay objects in our new chain
		last = bay;

		// Decompress the data we elevated
		int n = compressed.size(); // Remove its size from the counts
		distance -= n;
		distanceCompressed -= n;
		decompress(n); // Count the sizes before and after decompression
	}

	// -------- Records --------
	
	/**
	 * The current download speed, in bytes/second.
	 * The current speed our channel is giving us data.
	 * If compression is on, this speed is of compressed data.
	 */
	public Speed speed;
	
	/**
	 * The time when our channel last gave us data.
	 * If this time is a long time ago, we may have lost our TCP socket connection.
	 */
	public OldTime transfer;

	/**
	 * The download speed in bytes/second, averaged over the whole time we've been connected.
	 * The average speed our channel is giving us data.
	 * If compression is on, this speed is of compressed data.
	 * 
	 * @return The speed in bytes/second
	 */
	public int averageSpeed() {
		
		// Compute the average from the total distance and time
		if (!tube.connect.isSet() || tube.connect.expired() == 0) return 0;
		else return (int)((distance() * 1000) / tube.connect.expired()); // Multiply by 1000 because expired() returns milliseconds
	}
	
	/**
	 * The total number of bytes of data we've downloaded.
	 * This is the size the program has taken, plus the size we're currently holding.
	 * If compression is on, this size is of compressed bytes.
	 * 
	 * @return The total size of data in bytes
	 */
	public long distance() {
		
		// Return distanceCompressed, the size of data we got from our channel
		return distanceCompressed; // If compression is off, distanceCompressed will be the same as distance
	}

	/**
	 * The total average data compression ratio.
	 * If we downloaded 85 compressed bytes that decompressed into 100, this method will return 0.85.
	 * If compression is off, returns 1.0.
	 * 
	 * @return The compression ratio as a float 0.0 through 1.0
	 */
	public float compression() {

		// Compute the ratio from the total sizes of compressed and not compressed data
		if (distance == 0) return 0;
		else if (distanceCompressed >= distance) return 1.0f; // Don't return 1.1 even if it's bigger compressed
		else return distanceCompressed / distance;
	}
	
	/**
	 * The total number of bytes of data our channel has given us.
	 * We add to this count the moment we get data from our channel, not when the program takes data from us.
	 */
	private long distanceCompressed;
	
	/**
	 * The total number of bytes of data our channel has given us, once we decompressed it.
	 * If decompression is off, distance is the same as distanceCompressed.
	 * We add to this count the moment we get data from our channel and decompress it, not when the program takes data from us.
	 */
	private long distance;

	// -------- Throttling --------
	
	public void throttle(int speed) {} // set speed in bytes/second, 0 to disable
	public int throttle() { return 0; } // get speed, 0 means no throttle
	public void pulse() { interest(); } // turn interest back on if we've waited long enough
}
