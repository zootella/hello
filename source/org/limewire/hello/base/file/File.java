package org.limewire.hello.base.file;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.pattern.Stripe;
import org.limewire.hello.base.pattern.StripePattern;
import org.limewire.hello.base.state.Close;

/** An open file on the disk with access to its data. */
public class File extends Close {
	
	// Open
	
	/** Make and open a new empty file at the given available path, or throw an IOException. */
	public static File make(Path path) throws IOException {
		if (path.exists()) throw new IOException("exists"); // Make sure the given path is available
		return new File(path, "rw", null);
	}
	
	/** Open the file at path filled with data with read "r" or read and write "rw" access, or throw an IOException. */
	public static File open(Path path, String access) throws IOException { return open(path, access, null); }
	/** Open the file at path with pattern data inside with read "r" or read and write "rw" access, or throw an IOException. */
	public static File open(Path path, String access, StripePattern pattern) throws IOException {
		if (!path.existsFile()) throw new IOException("not found"); // Make sure there is a file at the given path
		return new File(path, access, pattern);
	}

	/** Make or open the file at path filled with pattern data with the given access, or throw an IOException. */
	private File(Path path, String access, StripePattern pattern) throws IOException {

		// Make or open the file
		RandomAccessFile file = new RandomAccessFile(path.toFile(), access);

		// If no pattern given, make one
		if (pattern == null) {
			pattern = new StripePattern(); // If file is empty, pattern is ready
			long size = file.getChannel().size(); // If the file has gaps, size will be as if they are full
			if (size > 0) pattern = pattern.add(new Stripe(0, size)); // Mark the whole file as full
		}

		// Save everything in this new File object
		this.path = path;
		this.access = access;
		this.file = file;
		this.pattern = pattern;
	}
	
	// Inside
	
	/** The Path to our open file on the disk. */
	public final Path path;
	/** We have "r" read or "rw" read and write access on the file. */
	public final String access;
	/** The Java RandomAccessFile object that gives us access to the data in our file. */
	public final RandomAccessFile file;
	/** A StripePattern that shows what parts of this File have data, and which parts are gaps. */
	public StripePattern pattern() { return pattern; }
	private StripePattern pattern;

	// Close

	/** Close our open connection to this file on the disk. */
	public void close() {
		if (already()) return;
		try { file.close(); } catch (IOException e) {} // Also closes file's FileChannel
	}

	/** Close and delete this file on the disk. */
	public void delete() throws IOException {
		close();
		path.delete(); // Delete it at its Path
	}

	// Size
	
	/** The size of this file, as though any gaps in it are full. */
	public long size() { return pattern.size(); } // Ask our StripePattern
	/** True if this File has a size of 0 bytes. */
	public boolean isEmpty() { return size() == 0; }
	/** True if this File has 1 or more bytes of data inside. */
	public boolean hasData() { return size() > 0; }

	/** The size of this file as a Stripe 0 through size(), as though any gaps in it are full, null if empty file. */
	public Stripe stripe() {
		if (size() == 0) return null;
		return new Stripe(0, size());
	}

	// Transfer

	/** Read the contents of this File into memory. */
	public Data read() throws IOException { Bay bay = new Bay(); read(bay); return bay.data(); }
	/** Read the part of the File stripe identifies into memory. */
	public Data read(Stripe stripe) throws IOException { Bay bay = new Bay(); read(bay, stripe); return bay.data(); }

	/** Read the contents of this File into bay. */
	public void read(Bay bay) throws IOException {
		if (size() == 0) return; // This File is empty
		read(bay, new Stripe(0, size())); // Call the next method with a Stripe that clips out this whole file
	}

	/** Read the part of this File stripe identifies into bay. */
	public void read(Bay bay, Stripe stripe) throws IOException {
		if (!pattern.is(true, stripe)) throw new IOException("hole"); // Make sure we have data where stripe is
		bay.read(this, stripe); // Add stripe.size bytes from stripe.i in our file to bay
	}

	/** Write d a distance i bytes into this File. */
	public void write(long i, Data data) throws IOException {
		if (data.isEmpty()) return; // Nothing to write
		int did = file.getChannel().write(data.toByteBuffer(), i);
		if (did != data.size()) throw new IOException("did " + did); // Make sure write() wrote everything
		pattern = pattern.add(new Stripe(i, data.size())); // Update pattern
	}

	// Small
	
	/** Open the file at path, copy its contents into memory, and close it. */
	public static Data data(Path path) throws IOException {
		File f = open(path, "r"); // We only need read access
		Data d = f.read(); // Copy the file's contents into memory
		f.close();
		return d;
	}

	/** Save d to a file at path, overwriting one already there. */
	public static void save(Path path, Data d) throws IOException {
		File f = make(path);
		f.write(0, d);
		f.file.getChannel().truncate(d.size()); // Chop the file off after that
		f.close();
	}
}
