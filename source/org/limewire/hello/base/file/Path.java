package org.limewire.hello.base.file;

import java.io.File;
import java.io.IOException;

import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.exception.MessageException;

/** A Path is a parsed and valid looking absolute disk path, like "C:\folder\folder\file.ext". */
public class Path {
	
	// Make

	/** Parse the given String into a new absolute Path, or throw a MessageException. */
	public Path(String s) throws MessageException {

		// Turn "C:" into "C:/", "/C:" into "/C:/", otherwise the Java File constructor will make a relative File
		if ((s.length() == 2 &&                       Text.isLetter(s.charAt(0)) && s.charAt(1) == ':') ||
			(s.length() == 3 && s.charAt(0) == '/' && Text.isLetter(s.charAt(1)) && s.charAt(2) == ':'))
			s = s + "/";

		// Have the Java File constructor parse and keep the text, this doesn't use the disk
		File file = new File(s);
		if (!file.isAbsolute()) throw new MessageException(); // Absolute like "C:\file" or "\\computer\share\file", not relative like "file" or "folder\file"
		this.file = file; // Save it
	}

	/** Confirm File is absolute and make it into a new Path, or throw a MessageException. */
	public Path(File file) throws MessageException {
		if (!file.isAbsolute()) throw new MessageException(); // Make sure it's absolute
		this.file = file; // Save it
	}
	
	// Look
	
	/**
	 * A Java File object with the path text parsed inside.
	 * This isn't a file the program has open, and Java won't use the disk to make this object.
	 * This reference is final, and File is immutable, so path.file will never change.
	 */
	public final File file;
	
	// Convert

	/** Convert this Path to a String like "C:\folder\file.ext" or "\\computer\share\folder\file.ext". */
	public String toString() {
		return file.getPath(); // Return the String inside our Java File object as the File constructor parsed it
	}
	
	/** Get the file or folder Name at the end of this Path, like "file.ext" or "folder". */
	public Name name() {
		return new Name(file.getName()); // Get the last name in the sequence
	}
	
	// Navigate
	
	/** Return a new Path which is this one with name like "file.ext" added to it. */
	public Path add(Name name) { return add(name.toString()); }
	/** Return a new Path which is this one with path like "folder\folder\file.ext" added to it. */
	public Path add(PathName path) { return add(path.toString()); }
	/** Return a new Path which is this one with s like "folder\folder\file.ext" added to it. */
	public Path add(String s) {
		try {
			File f = new File(file, s); // Have the Java File constructor combine them
			return new Path(f);
		} catch (MessageException e) { throw new IndexOutOfBoundsException(); } // New Path not absolute
	}

	/**
	 * Return a new Path which is this one with the last folder or file name chopped off.
	 * @throws IndexOutOfBoundsException This Path was a root, like "C:\"
	 */
	public Path up() {
		try {
			File f = file.getParentFile(); // As our File for its parent
			if (f == null) throw new IndexOutOfBoundsException(); // Make sure it has one
			return new Path(f);
		} catch (MessageException e) { throw new IndexOutOfBoundsException(); } // New Path not absolute
	}
	
	// Disk
	
	/** True if there is a file or folder on the disk at this Path, false if it is unoccupied. */
	public boolean exists() { return file.exists(); }
	/** True if there is a file on the disk at this Path. */
	public boolean existsFile() { return exists() && !existsFolder(); }
	/** True if there is a folder on this disk at this Path. */
	public boolean existsFolder() { return file.isDirectory(); }
	
	/** Confirm this Path is to a folder on the disk, making folders as needed, throw an IOException if it's not. */
	public void folder() throws IOException {
		if (existsFolder()) return; // It's already a folder
		if (!file.mkdirs()) throw new IOException(); // Turn returning false into an exception
	}
	
	/**
	 * Move a file or folder at this Path to the given destination Path.
	 * move() can rename a file or a folder, even if the folder has contents.
	 * move() can move a file into an existing folder, but can't make a folder to move the file into.
	 */
	public void move(Path destination) throws IOException {
		if (!file.renameTo(destination.file)) throw new IOException(); // Move it by renaming it
	}
	
	/**
	 * Delete the file at the given path, or throw an IOException.
	 * delete() can delete a file or an empty folder, but not a folder with contents.
	 */
	public void delete() throws IOException {
		if (!exists()) return; // Nothing to delete, delete() below would return false
		if (!file.delete()) throw new IOException(); // Turn returning false into an exception
	}
}
