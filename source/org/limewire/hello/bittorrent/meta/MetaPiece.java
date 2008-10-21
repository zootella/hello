package org.limewire.hello.bittorrent.meta;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.size.Stripe;


/** Information about one piece listed in a .torrent file. */
public class MetaPiece {

	// -------- Information about a piece listed in a .torrent file --------

	/** A link up to the Meta object that represents the .torrent file this MetaPiece holds information from. */
	public final Meta meta;

	/** This piece's number, 0 is the first piece. */
	public final int number;

	/** A Stripe that shows where this piece is in the torrent's combined data. */
	public final Stripe stripeInTorrent;
	/** A Stripe that shows this piece, 0 to the piece size. */
	public final Stripe stripeInPiece;

	/** The 20-byte SHA1 hash value of this piece of the torrent's combined file data. */
	public final Data hash;
	
	// -------- Group together a .torrent file's information about one piece --------
	
	/** Make a new MetaPiece object, grouping together the given information from a .torrent file about one piece. */
	public MetaPiece(Meta meta, int number, Stripe stripeInTorrent, Data hash) {
		this.meta            = meta;
		this.number          = number;
		this.stripeInTorrent = stripeInTorrent;
		this.stripeInPiece   = new Stripe(0, stripeInTorrent.size); // Make a new Stripe that shows the piece by itself
		this.hash            = hash;
	}
}
