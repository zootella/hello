package org.limewire.hello.bittorrent.meta;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.size.Stripe;


/** A stripe of data in a single numbered piece of a torrent. */
public class PieceStripe {

	// -------- A Stripe of data in a single piece in a torrent --------
	
	/** The piece described in the .torrent file this stripe of data is within. */
	public final MetaPiece piece;
	
	/** This Stripe of data within the torrent's combined data, measured from the start of it. */
	public final Stripe stripeInTorrent;
	/** This Stripe of data within piece, measured from the start of piece. */
	public final Stripe stripeInPiece;
	
	/**
	 * Make a new PieceStripe to pick a numbered piece and define a stripe of data within it.
	 * 
	 * @param piece           The piece the data is in
	 * @param stripeInTorrent Where the data is, measured from the start of the torrent's combined data
	 */
	public PieceStripe(MetaPiece piece, Stripe stripeInTorrent) {
		
		// Save the given objects
		this.piece = piece;
		this.stripeInTorrent = stripeInTorrent;
		
		// Shift the given Stripe down to measure from the start of the piece
		this.stripeInPiece = stripeInTorrent.shift(-piece.stripeInTorrent.i);
	}
	
	// -------- Convert to and from numbers and data --------

	/**
	 * Make a new PieceStripe object to clip out some data in a piece of a torrent.
	 * 
	 * @param p    The number of the piece this PieceStripe is in, 0 is the first piece
	 * @param i    The distance in bytes from the start of that piece to the data
	 * @param s    The data size, in bytes
	 * @param meta The Meta object that represents our .torrent file
	 */
	public PieceStripe(int p, int i, int s, Meta meta) throws MessageException {
		try {

			// Look up meta's Piece object for piece number p
			piece = meta.pieces.get(p);
			
			// Make Stripe objects that show where the stripe is in our piece, and in our torrent
			stripeInPiece = new Stripe(i, s);
			if (stripeInPiece.end() > piece.stripeInTorrent.size) throw new MessageException();
			stripeInTorrent = stripeInPiece.shift(piece.stripeInTorrent.i);

		} catch (IndexOutOfBoundsException e) { throw new MessageException(); }
	}

	/** Parse 12 bytes like PPPPIIIISSSS into the piece number, index in that piece, and size of this new PieceStripe object. */
	public PieceStripe(Data d, Meta meta) throws ChopException, MessageException {
		this(                              // Call the first constructor
			Number.toInt(d.clip(0, 4), 0), // Read each number in 4 bytes
			Number.toInt(d.clip(4, 4), 0),
			Number.toInt(d.clip(8, 4), 1), // Make sure the size is 1 or more
			meta);
	}

	/** Turn this PieceStripe into 12 bytes of data like PPPPIIIISSSS with the piece number, index, and size. */
	public Data data() { Bay bay = new Bay(); toBay(bay); return bay.data(); }
	/** Add 12 bytes to bay like PPPPIIIISSSS with the piece number, index, and size. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, piece.number);
		Number.toBay(bay, 4, stripeInPiece.i);
		Number.toBay(bay, 4, stripeInPiece.size);
	}
}
