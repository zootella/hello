package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.bittorrent.meta.Meta;
import org.limewire.hello.bittorrent.meta.PieceStripe;


// on the wire, a Piece message is 13 bytes of header information followed by many more bytes of file data
// in the program, a PieceMessage object just holds the header information
// A PieceMessage object doesn't contain any file data, because that would make it too big
// this means when you send a PieceMessage, you have to upload the file data after it
// when you receive a PieceMessage, you have to download the file data after it

// SSSSTPPPPIIIIdatadatadatadata
// SSSS is the size of the rest of the message
// T is the type byte, 7
// PPPP is the piece number
// IIII is the offset
// after that is the file data
public class PieceMessage extends Message {

	// -------- Identify --------

	/** 0x07, the byte that identifies a Piece message. */
	public static final byte type = 0x07;

	/** 13 bytes, a Piece message has 13 bytes of header data before the file data. */
	public static final int headerSize = 13;
	
	// -------- Contents --------
	
	/** The piece number, offset, and size of the data this PieceMessage announces will follow. */
	public PieceStripe stripe;
	
	// -------- Send --------
	
	/**
	 * Make the start of a new PieceMessage to send to a peer, announcing stripe will follow.
	 * After sending it, send the file data.
	 */
	public PieceMessage(PieceStripe stripe) {
		this.stripe = stripe;
	}

	/** Add the data of this PieceMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 9 + stripe.stripeInPiece.size); // The first 4 bytes are the payload size
		bay.add(type);                                       // The payload starts with the type byte
		Number.toBay(bay, 4, stripe.piece.number);           // After that are the piece number, and index, each in 4 bytes
		Number.toBay(bay, 4, stripe.stripeInPiece.i);
	}
	
	// -------- Receive --------
	
	/**
	 * Parse the data of the start of a BitTorrent Piece message into this new PieceMessage object.
	 * After it will come the file data.
	 */
	public PieceMessage(Data d, Meta meta) throws ChopException, MessageException {
		
		// Get numbers from the data
		int p = Number.toInt(d.clip(5, 4), 0);
		int i = Number.toInt(d.clip(9, 4), 0);
		int s = Number.toInt(d.start(4), 10) - 9; // Calculate the data size after 9 bytes of type, piece, and i
		
		// Save them in our PieceStripe object
		stripe = new PieceStripe(p, i, s, meta);
	}
}
