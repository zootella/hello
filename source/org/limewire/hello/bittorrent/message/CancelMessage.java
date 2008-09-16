package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.bittorrent.meta.Meta;
import org.limewire.hello.bittorrent.meta.PieceStripe;


// SSSSTPPPPIIIIDDDD
// SSSS is the size of the rest of the message, 13
// T is the type byte, 8
// PPPP is the piece number
// IIII is the offset
// DDDD is the data size
public class CancelMessage extends Message {

	// -------- Identify --------

	/** 0x08, the byte that identifies a Cancel message. */
	public static final byte type = 0x08;

	// -------- Contents --------
	
	/** The piece number, offset, and size from the RequestMessage this CancelMessge is canceling. */
	public PieceStripe stripe;
	
	// -------- Send --------
	
	/** Make a new CancelMessage to send to a peer, cancelling a previous request for stripe. */
	public CancelMessage(PieceStripe stripe) {
		this.stripe = stripe;
	}

	/** Add the data of this CancelMessage to bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 13); // The first 4 bytes are the payload size, 13
		bay.add(type);            // The payload starts with the type byte
		stripe.toBay(bay);        // After that are the piece number, index, and size, each in 4 bytes
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent Cancel message into this new CancelMessage object. */
	public CancelMessage(Data d, Meta meta) throws ChopException, MessageException {
		stripe = new PieceStripe(d.after(5), meta); // Clip d after the message payload size and type byte
	}
}
