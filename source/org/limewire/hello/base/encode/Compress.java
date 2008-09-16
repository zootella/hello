package org.limewire.hello.base.encode;


import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.exception.ProgramException;

public class Compress {

	// -------- Compress a series of data blocks with a Compress object --------
	
	/**
	 * Compress the next block of data in a sequence of blocks.
	 * 
	 * @param destination The Bay object this method will add compressed data to.
	 * @param source      A Bay object with the next block of data to compress.
	 *                    This method will clear this source Bay.
	 * @return            The number of bytes of compressed data this method added to destination.
	 */
	public int compressAdd(Bay destination, Bay source) {
		if (deflater == null) deflater = new Deflater();       // Make our Deflater the first time we're called
		int before = destination.size();                       // Watch how the destination grows
		compress(deflater, destination, source.data(), false); // false because this is one block in a series
		source.clear();                                        // We compressed all the data
		return destination.size() - before;
	}

	/**
	 * Decompress the next block of data in a sequence of blocks.
	 * 
	 * @param destination The Bay object this method will add decompressed data to.
	 * @param source      A Bay object with the next block of compressed data to decompress.
	 *                    This method will clear this source Bay.
	 * @return            The number of bytes of uncompressed data this method added to destination.
	 */
	public int decompressAdd(Bay destination, Bay source) throws MessageException {
		if (inflater == null) inflater = new Inflater();  // Make our Inflater the first time we're called
		int before = destination.size();                  // Watch how the destination grows
		decompress(inflater, destination, source.data());
		source.clear();                                   // We decompressed all the data
		return destination.size() - before;
	}

	// -------- Compress a single block of data with these methods --------

	/** Compress a single block of data by itself and all at once. */
	public static Data compressAll(Data d) { Bay bay = new Bay(); compressAll(bay, d); return bay.data(); }
	/** Compress the single block of data d by itself and all at once, add the compressed data to bay. */
	public static void compressAll(Bay bay, Data d) {
		Deflater deflater = new Deflater(); // Make a Java Deflater object for us to use
		compress(deflater, bay, d, true);   // Compress d into bay, true because this is all the data
		deflater.end();                     // Tell the Deflater we're done with it
	}
	
	/** Decompress a single block of compressed data by itself and all at once. */
	public static Data decompressAll(Data d) throws MessageException { Bay bay = new Bay(); decompressAll(bay, d); return bay.data(); }
	/** Decompress the single block of compressed data d by itself and all at once, add the decompressed data to bay. */
	public static void decompressAll(Bay bay, Data d) throws MessageException {
		Inflater inflater = new Inflater(); // Make a Java Inflater object for us to use
		decompress(inflater, bay, d);       // Decompress d into bay
		inflater.end();                     // Tell the Inflater we're done with it
	}

	// -------- Inside parts --------
	
	/** The Java Deflater object this Compress object will use to compress each block in sequence. */
	private Deflater deflater;

	/** The Java Inflater object this Compress object will use to decompress each block in sequence. */
	private Inflater inflater;
	
	/** Make an empty temporary byte array size bytes big, keeping it between 64 bytes and 8 KB. */
	private static byte[] temporary(int size) {
		if (size < 64) size = 64;
		if (size > Bay.big) size = Bay.big;
		return new byte[size];
	}
	
	/**
	 * Compress some data.
	 * 
	 * @param deflater The Java Deflater object to use.
	 * @param bay      A Bay this method will add the compressed data to.
	 * @param d        The source data to compress.
	 * @param all      true if this is all the data to compress in a single block.
	 *                 false if this is just the next block in a series of blocks to compress together.
	 */
	private static void compress(Deflater deflater, Bay bay, Data d, boolean all) {

		// Prepare the Deflater and make our temporary array
		if (d.isEmpty()) return;                         // Nothing to compress
		deflater.setLevel(Deflater.DEFAULT_COMPRESSION); // Turn the Deflater's compression on
		deflater.setInput(d.toByteArray());              // Point it at the source data
		if (all) deflater.finish();                      // If this is all the data, tell it so
		byte[] a = temporary(d.size());                  // Make an array for the Deflater to put data in

		// Run the Deflater once
		if (compressOnce(deflater, a, bay)) {

			// We got something, keep running it until it gives us nothing
			while (compressOnce(deflater, a, bay));

		// We gave the Deflater data, called deflate() once, and got nothing
		} else {

			// There is data stuck in the Deflater, and we need to get it out
			deflater.setLevel(Deflater.NO_COMPRESSION); // Turn off compression
			deflater.setInput(new byte[0]); // Point the Deflater at an empty array

			// Run the deflater until it gives us nothing
			while (compressOnce(deflater, a, bay));
		}
	}
	
	/**
	 * Run a Deflater one time to see what it does and get compressed data from it.
	 * 
	 * @param deflater The Deflater object to run.
	 * @param a        A byte array for it to use.
	 * @param bay      Add data the Deflater produces to this Bay.
	 * @return         true if the Deflater produced at least 1 byte of compressed data.
	 *                 false if it gave us nothing.
	 */
	private static boolean compressOnce(Deflater deflater, byte[] a, Bay bay) {
		try {
			
			// Run the Deflater, add what it put in a to destination, and return the number of bytes it produced
			int wrote = deflater.deflate(a); // Writes data at the start of a and returns the number of bytes it wrote there
			bay.add((new Data(a)).start(wrote));
			
			return wrote != 0; // Return true if we got something
			
		} catch (ChopException e) { throw new ProgramException(); } // The Deflater didn't tell us how many bytes it wrote
	}

	/**
	 * Decompress some data.
	 * 
	 * @param inflater The Java Inflater object to use
	 * @param bay      A Bay this method will add the decompressed data to
	 * @param d        The compressed source data
	 */
	private static void decompress(Inflater inflater, Bay bay, Data d) throws MessageException {
		try {

			// Prepare the Inflater and make our temporary array
			if (d.isEmpty()) return;            // Nothing to decompress
			inflater.setInput(d.toByteArray()); // Point the Inflater at the source data
			byte[] a = temporary(d.size() * 3); // Prepare for the data to triple in size
			
			// Loop until the Inflater has given us all the decompressed data
			while (true) {
				
				// Get decompressed data from the Inflater, and add it to the destination Bay
				int wrote = inflater.inflate(a); // Writes decompressed data at the start of a and returns the number of bytes it wrote there
				bay.add((new Data(a)).start(wrote)); // Just add the start of a
				
				// The Inflater gave us nothing, it's out of data, we're done
				if (wrote == 0) break;
			}

		} catch (DataFormatException e) { throw new MessageException(); // There is a mistake in the compressed data
		} catch (ChopException e) { throw new ProgramException(); }     // The Inflater didn't tell us how many bytes it wrote
	}
}
