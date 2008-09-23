package org.limewire.hello.base.file;

import java.io.IOException;

public class Save {
	
	// -------- Make a temporary file like "Hello 56nu24pz.db" to fill with data --------

	/** Make a temporary file in folder you can download data into. */
	public Save(Path folder) throws IOException {
		this.folder = folder;
		folder.folder(); // Make sure folder is a Path to a folder on the disk
		file = new OldFile(folder.add(Name.temporary()), "rw"); // Create and open a new file like "Hello 56nu24pz.db" in folder
	}
	
	/**
	 * The File you can fill with data, save, and then look at some more.
	 * Before you call save(), file is like "Hello 56nu24pz.db" in folder.
	 * After you call save(), file is either null or the file at the saved Path open for upload.
	 */
	public OldFile file;
	
	// -------- When it's all downloaded, move it into a subfolder and give it a nice name for the user --------

	/**
	 * Rename our filled temporary file to a saved-to location for the user, and optionally open it again for upload.
	 * 
	 * @param path The PathName to try to save it to, like "Folder%20Name \File:Name.ext"
	 * @param open true to open the file for upload, false to leave it closed
	 * @return     The absolute Path were we actually put it, like "C:\Saved\Folder Name\File-Name (2).ext"
	 */
	public Path save(PathName path, boolean open) throws IOException {
		
		// Pick the Path we'll try to save our temporary file to
		Path p = avoid(folder, path); // Looks at the disk, but doesn't change it

		// Close our temporary file, and move it to the saved-to location
		file.close();
		p.up().folder(); // Make subfolders to save the file into
		file.path.move(p);
		file = null; // We don't have our temporary file anymore

		// Open the file at its new location for upload
		if (open) file = new OldFile(p, "r"); // We only need read access

		// If all that worked with an exception, mark this Save object as saved
		saved = p; // Record that we saved our file, and where
		return p;  // Return where we saved it
	}

	/** The Path to the finished saved file. */
	public Path saved;
	
	// -------- Have this Save object clean up its disk resources --------
	
	/**
	 * Close this Save object's disk resources.
	 * If this Save object has a temporary file like "Hello 56nu24pz.db", deletes it.
	 * If this Save object has a saved file open for upload, closes it.
	 */
	public void close() {
		try {
			if (file != null) {                   // We have a file open
				if (saved == null) file.delete(); // It's the fill file, close and delete it
				else               file.close();  // It's the saved file, just close it
			}
		} catch (IOException e) {} // Ignore an exception because we're already closing anyway
	}
	
	// -------- Inside parts --------
	
	/** The Path to the folder where this Save object will make files and folders. */
	private Path folder;

	/** Return path + name that's safe to save, looking at the disk to avoid things in the way. */
	private static Path avoid(Path path, PathName name) {
		Path p = path;
		for (Name folder : name.folders()) // Loop for each folder
			p = avoid(p, folder, true);    // If a file is in the way, name it like "folder (2)"
		p = avoid(p, name.name(), false);  // If a file is in the way, name it like "name (2).ext"
		return p;
	}

	/**
	 * Return path + name that's safe to save, looking at the disk to avoid something in the way.
	 * @param folder true if name is for a folder, chooses a Path that's available or already a folder.
	 *               false if name is for a file, chooses a Path that's available.
	 */
	private static Path avoid(Path path, Name name, boolean folder) {
		int number = 1;                                           // The first number, 1, won't show up in the name
		while (true) {                                            // Loop until we find a path that works
			Path p = path.add(name.save(number));                 // Make name safe to save, and give it our current number
			if (folder ? !p.existsFile() : !p.exists()) return p; // If p works, return it
			number++;                                             // Loop again to try the next number
		}
	}
}
