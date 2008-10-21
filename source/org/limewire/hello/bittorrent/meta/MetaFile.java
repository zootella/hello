package org.limewire.hello.bittorrent.meta;

import org.limewire.hello.base.file.PathName;
import org.limewire.hello.base.size.Stripe;


/** Information about one file listed in a .torrent file. */
public class MetaFile {

	// -------- Information about a file listed in a .torrent file --------
	
	/** A link up to the Meta object that represents the .torrent file this MetaFile holds information from. */
	public final Meta meta;
	
	/** A Stripe that shows where this file is in the torrent's combined data of all the files pushed together. */
	public final Stripe stripeInTorrent;
	/** A Stripe that shows this file, 0 to the file size. */
	public final Stripe stripeInFile;

	/** The folder and file name, like "name.ext" or "name/folder/file.ext". */
	public final PathName path;
	
	// -------- Group together a .torrent file's information about one file --------
	
	/** Make a new MetaFile object, grouping together the given information from a .torrent file about one file. */
	public MetaFile(Meta meta, Stripe stripeInTorrent, PathName path) {
		this.meta            = meta;
		this.stripeInTorrent = stripeInTorrent;
		this.stripeInFile    = new Stripe(0, stripeInTorrent.size); // Make a new Stripe that shows the file by itself
		this.path            = path;
	}
}
