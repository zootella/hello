package org.limewire.hello.base.setting;

import java.io.IOException;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Outline;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Here;
import org.limewire.hello.base.file.OldFile;
import org.limewire.hello.base.file.Path;


public class Store {

	// -------- Open and save Store.txt --------

	/** Open Store.txt next to where this program is running. */
	public Store() {
		this(Here.folder().add("Store.txt")); // Give the next constructor the complete Path
	}

	/** Open the program's store file at the given path. */
	public Store(Path path) {

		// Keep path so close() knows where to save the file
		this.path = path;
		
		// Make a blank Outline object so outline is never null
		outline = new Outline("");
		
		try {
			
			// Open the store file, and parse the Outline inside
			outline = Outline.fromText(File.data(path));
			
		// That didn't work, outline will be the blank empty Outline
		} catch (ChopException e) {
		} catch (MessageException e) {
		} catch (IOException e) {}
	}

	/** The Path where the store file is on the disk. */
	private Path path;
	
	/**
	 * The Outline with settings and data the program keeps between times it runs.
	 * While it runs, the program looks at values in outline and changes them.
	 * When not running, outline is saved as a text outline in the file Store.txt.
	 */
	public Outline outline;
	
	/** Save the program's store file for the next time we run. */
	public void close() {
		try {
			
			// Turn outline into text, and save it to path
			File.save(path, new Data(outline.toString()));
			
		// That didn't work, but we can't do anything about it
		} catch (IOException e) {}
	}
	
	// -------- Make different kinds of setting objects that will save themselves in this Store --------

	/** Make a new DataSetting with the given default value that will save itself at path in this Store's Outline. */
	public DataSetting make(String path, Data value) { return new DataSetting(this, path, value); }
	/** Make a new StringSetting with the given default value that will save itself at path in this Store's Outline. */
	public StringSetting make(String path, String value) { return new StringSetting(this, path, value); }
	/** Make a new NumberSetting with the given default value that will save itself at path in this Store's Outline. */
	public NumberSetting make(String path, long value, long minimum) { return new NumberSetting(this, path, value, minimum); }
	/** Make a new BooleanSetting with the given default value that will save itself at path in this Store's Outline. */
	public BooleanSetting make(String path, boolean value) { return new BooleanSetting(this, path, value); }
	/** Make a new PathSetting with the given default value that will save itself at path in this Store's Outline. */
	public PathSetting make(String path, Path value) { return new PathSetting(this, path, value); }
}
