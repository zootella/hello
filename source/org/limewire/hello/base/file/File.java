package org.limewire.hello.base.file;


import java.io.IOException;
import java.io.RandomAccessFile;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.pattern.StripePattern;

// an open file on the disk with access to its data
public class File {

	// -------- Open a file on the disk to get to its data --------
	
	/**
	 * Open the file at path.
	 * @param access  "r" for read-only access, "rw" to read and write
	 * @param pattern A StripePattern that shows where data is in the file
	 */
	public File(Path path, String access, StripePattern pattern) throws FileException {
		try {
			
			// Save the Path were our file is, and open it
			this.path = path;
			file = new RandomAccessFile(path.toFile(), access);
			
			// Save the given StripePattern
			this.pattern = pattern;
			
		} catch (IOException e) { throw new FileException(); }
	}

	/**
	 * Open the file at path or make a new file there.
	 * @param access "r" for read-only access, "rw" to read and write
	 */
	public File(Path path, String access) throws FileException {
		try {
			
			// Save the Path where this File is, and open it
			this.path = path;
			file = new RandomAccessFile(path.toFile(), access);

			// Make our StripePattern which shows where data is in this File
			pattern = new StripePattern(); // If file is empty, pattern is ready
			long size = file.getChannel().size(); // If the file has gaps, size will be as if they are full
			if (size > 0) pattern = pattern.add(new Stripe(0, size)); // Mark the whole file as full

		} catch (IOException e) { throw new FileException(); }
	}

	/** The Path to our open file on the disk. */
	public final Path path;
	/** The Java RandomAccessFile object that gives us access to the data in our file. */
	private RandomAccessFile file;
	
	// -------- Close and delete --------

	/** Close this File. */
	public void close() throws FileException {
		try {
			file.close(); // Close our Java RandomAccessFile
		} catch (IOException e) { throw new FileException(); }
	}
	
	/** Close and delete this file. */
	public void delete() throws FileException {
		close(); // Close our Java RandomAccessFile
		path.delete(); // Delete it at its Path
	}
	
	// -------- Size --------
	
	/** A StripePattern that shows what parts of this File have data, and which parts are gaps. */
	public StripePattern pattern;
	
	/** The size of this file, as though any gaps in it are full. */
	public long size() { return pattern.size(); } // Ask our StripePattern

	// -------- Read and write file data --------

	/** Read the contents of this File into memory. */
	public Data read() throws FileException { Bay bay = new Bay(); read(bay); return bay.data(); }
	/** Read the part of the File stripe identifies into memory. */
	public Data read(Stripe stripe) throws FileException { Bay bay = new Bay(); read(bay, stripe); return bay.data(); }

	/** Read the contents of this File into bay. */
	public void read(Bay bay) throws FileException {
		if (size() == 0) return; // This File is empty
		read(bay, new Stripe(0, size())); // Call the next method with a Stripe that clips out this whole file
	}

	/** Read the part of this File stripe identifies into bay. */
	public void read(Bay bay, Stripe stripe) throws FileException {
		if (!pattern.is(true, stripe)) throw new FileException(); // Make sure we have data where stripe is
		bay.read(file.getChannel(), stripe); // Add stripe.size bytes from stripe.i in our Java File to bay
	}

	/** Write d a distance i bytes into this File. */
	public void write(long i, Data d) throws FileException {
		try {
			if (d.isEmpty()) return; // Nothing to write
			int wrote = file.getChannel().write(d.toByteBuffer(), i);
			if (wrote != d.size()) throw new IOException(); // Make sure write() wrote everything
			pattern = pattern.add(new Stripe(i, d.size())); // Update our StripePattern
		} catch (IOException e) { throw new FileException(); }
	}

	// -------- Open or save a small file in a single step --------
	
	/** Open the file at path, copy its contents into memory, and close it. */
	public static Data data(Path path) throws FileException {
		File f = new File(path, "r"); // We only need read access
		Data d = f.read(); // Copy the file's contents into memory
		f.close();
		return d;
	}

	/** Save d to a file at path, overwriting one already there. */
	public static void save(Path path, Data d) throws FileException {
		try {
			File f = new File(path, "rw"); // Get read and write access
			f.write(0, d);
			f.file.getChannel().truncate(d.size()); // Chop the file off after that
			f.close();
		} catch (IOException e) { throw new FileException(); }
	}
}
