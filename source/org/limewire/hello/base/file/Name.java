package org.limewire.hello.base.file;

import org.limewire.hello.all.Program;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.data.TextSplit;
import org.limewire.hello.base.web.Url;

/** A file name and extension, like "name.ext". */
public class Name {
	
	// -------- Make a Name object --------
	
	/** Make a new blank Name. */
	public Name() {
		name = "";
		extension = "";
	}

	/** Make a new Name from "name" and "ext", without a period, trims both. */
	public Name(String name, String extension) {
		this.name = name.trim();
		this.extension = extension.trim();
	}
	
	/** Parse a String like "name.ext" into a Name object. */
	public Name(String s) {
		TextSplit split = Text.splitLast(s, "."); // Split around the last "." to separate the file name from the extension
		name = split.before.trim(); // Remove any space from their edges
		extension = split.after.trim();
	}
	
	// -------- Look at it --------
	
	/** The file name, like "name". */
	public final String name;
	/** The file name extension, like "ext", without a period. */
	public final String extension;

	/** Turn this Name into a String like "name.ext". */
	public String toString() {
		String s = name;
		if (Text.hasText(extension)) s += "." + extension; // Only add the period if we have an extension
		return s;
	}
	
	/** true if this Name is blank, toString() will give you "". */
	public boolean isBlank() { return Text.isBlank(toString()); }
	/** true if this Name has text, toString() won't give you "". */
	public boolean hasText() { return Text.hasText(toString()); }
	
	// -------- Make a new Name based on this one --------
	
	/**
	 * Return a new Name that is this one, ready to save it to the disk.
	 * Turns codes like "%20" into the characters they represent.
	 * Replaces characters that can't appear in file names like \ / : * ? < > | " with - and '.
	 * If number is 2 or more, adds it like "name (2).ext".
	 * Makes the extension lower case.
	 * Never returns a blank Name, returns the Name "Index" instead.
	 */
	public Name save(int number) {
		return decode().safe().number(number).lower();
	}
	
	/** Return a new Name that is this one URL-decoded, turn "File%20Name.ext" into "File Name.ext". */
	public Name decode() {
		return new Name(Url.decode(name), Url.decode(extension)); // Name() removes "%20" at the edges with trim()
	}
	
	/** Return a new Name that is this one with safe characters, and turn blank into "Index". */
	public Name safe() {
		Name n = new Name(safe(name), safe(extension));
		if (n.isBlank()) n = new Name("Index"); // Return "Index" instead of a blank Name
		return n;
	}
	
	/** Replace characters that aren't allowed in a file name on the disk with characters that are. */
	private static String safe(String s) {
		s = Text.replace(s, "\\", "-"); // Replace \ / : * ? < > | with -
		s = Text.replace(s,  "/", "-");
		s = Text.replace(s,  ":", "-");
		s = Text.replace(s,  "*", "-");
		s = Text.replace(s,  "?", "-");
		s = Text.replace(s,  "<", "-");
		s = Text.replace(s,  ">", "-");
		s = Text.replace(s,  "|", "-");
		s = Text.replace(s, "\"", "'"); // Replace " with '
		return s;
	}
	
	/** Make a new Name that is this one with a number, like "name (2).ext", doesn't say 0 or 1. */
	public Name number(int number) {
		if (number < 2) return this; // No change necessary
		return new Name(name + " (" + number + ")", extension);
	}

	/** Make a new Name that is this one with the extension changed to lower case. */
	public Name lower() {
		return new Name(name, extension.toLowerCase());
	}

	// -------- Make a new Name for a temporary file --------

	/** Make a new Name like "Hello ryio3tz5.db" that won't conflict with files already in a folder. */
	public static Name temporary() {
		return new Name(Program.name + " " + Text.start(Data.random(8).base32(), 8), "db");
	}
}
