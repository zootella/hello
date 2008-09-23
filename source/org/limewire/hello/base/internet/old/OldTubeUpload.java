package org.limewire.hello.base.internet.old;


import java.io.IOException;
import java.nio.channels.SelectionKey;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.encode.Compress;
import org.limewire.hello.base.time.Speed;
import org.limewire.hello.base.time.OldTime;

public class OldTubeUpload {

	// -------- Internal parts --------
	
	/** A reference up to the Tube object that we're a part of. */
	private OldTube tube;
	
	/** A Bay that holds data before we compress it. */
	private Bay bay;
	/** A Bay that holds the data we've compressed. */
	private Bay compressed;
	
	/** A reference to the first Bay in the chain from the program up to the Internet. */
	private Bay first;
	/** A reference to the last Bay in the chain from the program up to the Internet. */
	private Bay last;

	// -------- Make a new TubeUpload object --------
	
	/**
	 * Make a new TubeUpload object to upload data into a TCP socket connection.
	 * 
	 * @param tube The Tube object this new TubeUpload will be a part of
	 */
	public OldTubeUpload(OldTube tube) {
		
		// Save the link up to the Tube we're in
		this.tube = tube;
		
		// Make our Bay for normal data, and point both first and last at it
		bay = new Bay();
		first = bay; // It's the only Bay in the chain
		last = bay;
		
		// Make objects that keep records
		speed = new Speed();
		transfer = new OldTime();
		
		// We don't tell the Selector we're interested in writing because we don't have any data to write yet
	}
	
	// -------- Upload data --------

	/**
	 * Ask permission to add data to this TubeUpload object.
	 * 
	 * @return true, call add() to add your data now
	 *         false, we're full, wait a little while and then ask again
	 */
	public boolean permission() {
		
		// Refuse if the last Bay in our chain has a big amount waiting for our channel to take
		return last.size() < Bay.big;
	}

	/**
	 * Add data to upload.
	 * 
	 * @param d The data
	 */
	public void add(Data d) {

		// The size of the given data before and after compression
		int size, sizeCompressed;
		size = sizeCompressed = d.size(); // If compression is off, the sizes are the same
		
		// Add the given data to the first Bay in our chain
		first.add(d);
		
		// If compression is on, compress the data from bay to compressed
		if (compress != null) sizeCompressed = compress.compressAdd(compressed, bay);

		// Add to the total distances traveled
		distance += size;
		distanceCompressed += sizeCompressed;

		// Tell the Selector if we're still interested in uploading or not
		interest();
	}
	
	// -------- Interact with the Selector --------

	/**
	 * The Selector says our channel wants some data.
	 * Move as much data as we can from this TubeUpload object into it.
	 */
	public void selectUpload() throws IOException {
		
		// Move data from the last Bay in our chain to our channel
		int n = last.oldUpload(tube.channel);
		speed.add(n);               // Report the distance we just travelled to our Speed object
		if (n != 0) transfer.set(); // If our channel took some data, set the last transfer time to now

		// Tell the Selector if we're still interested in uploading or not
		interest();
	}

	/**
	 * Tell the Selector if we are interested in uploading data right now or not.
	 * Call interest() when this TubeUpload object changes to keep the Selector informed.
	 */
	private void interest() {

		// If the last Bay in our chain has data, we are interested in writing it into our channel
		tube.select.interest(tube.channel, SelectionKey.OP_WRITE, last.size() != 0);
	}

	// -------- Compression --------
	
	/**
	 * The Compress object this TubeUpload will use to compress data before uploading it.
	 * To determine if we have compression turned on, see if compress is null or not.
	 */
	private Compress compress;
	
	/**
	 * Have this TubeUpload compress the data it uploads.
	 * The data you've give add() so far will go up uncompressed.
	 * Everything you add after this will be compressed, then uploaded.
	 */
	public void enableCompression() {
		
		// Make a Compress object to stream compress the data we upload
		compress = new Compress(); // Now that compress isn't null, we know compression is turned on
		
		// Expand the Bay chain for compression
		compressed = bay;  // Consider the data in bay as already compressed
		bay = new Bay();   // Make a new Bay for the data before we compress it
		first = bay;       // Point first and last at the first and last Bay objects in our new chain
		last = compressed;
	}

	// -------- Records --------
	
	/**
	 * The current upload speed, in bytes/second.
	 * The current speed our channel is taking data from us.
	 * If compression is on, this speed is of compressed data.
	 */
	public Speed speed;
	
	/**
	 * The time when our channel last took data from us.
	 * If this time is a long time ago, we may have lost our TCP socket connection.
	 */
	public OldTime transfer;
	
	/**
	 * The upload speed in bytes/second, averaged over the whole time we've been connected.
	 * The average speed our channel is taking data from us.
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
	 * The total number of bytes of data we've uploaded and have to upload.
	 * This is the size we've given our channel, plus the size we're currently holding.
	 * If compression is on, this size is of compressed bytes.
	 * 
	 * @return The total size of data in bytes
	 */
	public long distance() {
		
		// Return distanceCompressed, the size of data we put into our channel
		return distanceCompressed; // If compression is off, distanceCompressed will be the same as distance
	}
	
	/**
	 * The total average data compression ratio.
	 * If we compressed 100 bytes down to 85, this method will return 0.85.
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
	 * The total number of bytes of data the program has given this TubeUpload to upload.
	 * We add to this count the moment the program gives us data to upload, not when our channel takes it.
	 */
	private long distance;
	
	/**
	 * The total size of the data the program has given this TubeUpload, once we compressed it. 
	 * If compression is off, distanceCompressed is the same as distance.
	 * We add to this count the moment the program gives us data and we compress it, not when our channel takes it.
	 */
	private long distanceCompressed;

	// -------- Throttling --------
	
	public void throttle(int speed) {} // set speed in bytes/second, 0 to disable
	public int throttle() { return 0; } // get speed, 0 means no throttle
	public void pulse() { interest(); } // turn interest back on if we've waited long enough
}
