package org.limewire.hello.base.file;


import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.data.Text;

//immutable, never touches the disk, always a relative path
//in the spirit of cross platformness, PathName accepts both kinds of slashes, change it to output forward slashes
/** A PathName is a relative disk path of folders and a file, like "folder\folder\file.ext". */
public class PathName {

	// -------- Make a new PathName --------

	/** Make a new PathName object with an empty names List. */
	public PathName() {
		names = new ArrayList<Name>(); // Make names an empty List ready to hold Name objects
	}
	
	/** Make a new PathName with the single given Name, like "folder" or "file.ext". */
	public PathName(Name name) {
		this();      // Call the first constructor to make names an empty List
		check(name); // Add name if it's not blank
	}

	/** Make a new PathName from the given String like "folder\folder\file.ext", separate parts with "\" or "/". */
	public PathName(String s) {
		this();                                          // Call the first constructor to make Names an empty List
		s = Text.replace(s, "\\", "/");                  // Make all backslashes slashes
		List<String> words = Text.words(s, "/");         // Split on every slash, trimming words and eliminating blank ones
		for (String word : words) check(new Name(word)); // Only add non-blank Name objects
	}
	
	// -------- Make a new PathName based on this one --------

	/** Return a new PathName which is this one with name like "file.ext" added to it. */
	public PathName add(Name name) { return add(new PathName(name)); }
	/** Return a new PathName which is this one with s like "folder/file.ext" added to it. */
	public PathName add(String s) { return add(new PathName(s)); }
	/** Return a new PathName which is this one with the given PathName added to it. */
	public PathName add(PathName path) {
		PathName all = new PathName();
		for (Name name : names)      all.check(name); // Add our Name objects that aren't blank
		for (Name name : path.names) all.check(name); // Add the given Name objects that aren't blank
		return all;
	}

	// -------- Internal tools --------
	
	/** Wrap the given List of Name objects into a new PathName. */
	private PathName(List<Name> names) { this.names = names; }
	
	/** If name isn't blank, add it to our names List. */
	private void check(Name name) { if (name.hasText()) names.add(name); }

	// -------- Look at this PathName --------

	/**
	 * The List of Name objects that make up this PathName relative path.
	 * For the PathName "folder/folder/file.ext", names has 3 Name objects, "folder", "folder", and "file.ext".
	 */
	public List<Name> names;

	/** Get the last Name in this PathName, like "file.ext", or a blank Name if our names List is empty. */
	public Name name() {
		if (names.isEmpty()) return new Name(""); // If our names List is empty, return a new blank Name
		return names.get(names.size() - 1);       // Return the last Name in our names List
	}

	/**
	 * Get a List of the Name objects up to the last Name in this PathName, like "folder", "folder".
	 * If this PathName only has one Name, folders() returns an empty List.
	 */
	public List<Name> folders() {
		List<Name> copy = new ArrayList<Name>(names);        // Make a copy of our names List
		if (!names.isEmpty()) copy.remove(names.size() - 1); // Remove the last Name from the copy
		return copy;
	}

	/** Convert this PathName into a String like "folder/folder/file.ext". */
	public String toString() {
		String s = "";
		for (Name name : names) s += "/" + name.toString();
		if (Text.hasText(s)) s = Text.after(s, 1); // Remove the leading slash
		return s;
	}
}
