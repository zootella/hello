package org.limewire.hello.base.setting;

import org.limewire.hello.base.data.Outline;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.Path;


public class PathSetting {
	
	// -------- Make a PathSetting --------

	/** Make a PathSetting saved in store at path, with the given default value. */
	public PathSetting(Store store, String path, Path value) {
		
		// Save the given objects in this new one
		this.store = store;
		this.path = path;
		this.value = value;
		
		// If store's Outline has path, get the Outline object there
		try {
			this.outline = store.outline.path(path);
		} catch (MessageException e) {} // path not found, leave outline null
	}
	
	/** The Store this setting will save itself in, the file Store.txt. */
	private Store store;
	/** This setting's path like "name.name.name" in store's Outline. */
	private String path;
	/** The Outline object at path, or null if store's Outline doesn't have one there. */
	private Outline outline;
	
	/** This setting's default value the program set when it made this object. */
	private Path value;

	// -------- Get and set the value --------
	
	/** Get this setting's value in Store.txt, or the program's default value if not found. */
	public Path value() {
		if (outline == null) return value; // Not found in Store.txt, return our default
		try {
			return new Path(outline.getString());
		} catch (MessageException e) { return value; } // The outline value isn't a Path
	}
	
	/** Give this setting a new value, and save it in Store.txt for the next time the program runs. */
	public void set(Path value) {
		if (outline == null) outline = store.outline.make(path); // Make our object in store's Outline
		outline.set(value.toString());
	}
	
	// -------- Convert to and from a String --------
	
	/** Get this setting's value in Store.txt, or the program's default value if not found, as a String. */
	public String toString() {
		return value().toString(); // Get our value and convert it into a String
	}
	
	/** Give this setting a new value, and save it in Store.txt for the next time the program runs. */
	public void set(String value) {
		try {
			set(new Path(value));
		} catch (MessageException e) {} // Couldn't turn the given String into a number
	}
}
