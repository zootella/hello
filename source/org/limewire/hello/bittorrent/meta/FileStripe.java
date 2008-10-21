package org.limewire.hello.bittorrent.meta;

import org.limewire.hello.base.size.Stripe;

/** A stripe of data in a single file in a torrent. */
public class FileStripe {

	// -------- A Stripe of data in a single file in a torrent --------
	
	/** The file described in the .torrent file this stripe of data is within. */
	public final MetaFile file;

	/** This Stripe of data within the torrent's combined data, measured from the start of it. */
	public final Stripe stripeInTorrent;
	/** This Stripe of data within file, measured from the start of file. */
	public final Stripe stripeInFile;
	
	/**
	 * Make a new FileStripe to pick a file in a torrent and define a stripe of data within it.
	 * 
	 * @param file            The file the data is in
	 * @param stripeInTorrent Where the data is, measured from the start of the torrent's combined data
	 */
	public FileStripe(MetaFile file, Stripe stripeInTorrent) {
		
		// Save the given objects
		this.file = file;
		this.stripeInTorrent = stripeInTorrent;
		
		// Shift the given Stripe down to measure from the start of the file
		this.stripeInFile = stripeInTorrent.shift(-file.stripeInTorrent.i);
	}
}
