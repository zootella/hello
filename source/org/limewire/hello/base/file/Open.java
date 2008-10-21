package org.limewire.hello.base.file;

import org.limewire.hello.base.size.StripePattern;

/** How to make or open a File. */
public class Open {
	
	// Make
	
	/** Group the given path, pattern, and how into a new Open object that tells how to open a file. */
	public Open(Path path, StripePattern pattern, int how) {
		this.path = path;
		this.pattern = pattern;
		this.how = how;
	}
	
	// Look

	/** The Path to the file to open. */
	public final Path path;
	/** A StripePattern that shows where data is in the file, null don't know or assume the file has no gaps. */
	public final StripePattern pattern;
	/** How to open the file. */
	public final int how;
	
	// How

	/** Make a new empty file and get read and write access, throw an IOException if something already exists at the path. */
	public final static int make = 1;
	/** Make a new empty file and get read and write access, delete a file or empty folder already there first. */
	public final static int overwrite = 2;
	/** Open an existing file with read access, throw an IOException if not found. */
	public final static int read = 3;
	/** Open an existing file with read and write access, throw an IOException if not found. */
	public final static int write = 4;
}
