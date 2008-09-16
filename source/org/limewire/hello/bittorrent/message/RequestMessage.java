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
// T is the type byte, 6
// PPPP is the piece number
// IIII is the offset
// DDDD is the data size
public class RequestMessage extends Message {

	// -------- Identify --------

	/** 0x06, the byte that identifies a Request message. */
	public static final byte type = 0x06;
	
	// -------- Contents --------
	
	/** The piece number, offset, and size of the data this RequestMessage says it wants. */
	public PieceStripe stripe;
	
	// -------- Send --------
	
	/** Make a new RequestMessage to send to a peer, asking for stripe. */
	public RequestMessage(PieceStripe stripe) {
		this.stripe = stripe;
	}

	/** Add the data of this RequestMessage to bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 13); // The first 4 bytes are the payload size, 13
		bay.add(type);            // The payload starts with the type byte
		stripe.toBay(bay);        // After that are the piece number, index, and size, each in 4 bytes
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent Request message into this new RequestMessage object. */
	public RequestMessage(Data d, Meta meta) throws ChopException, MessageException {
		stripe = new PieceStripe(d.after(5), meta); // Clip d after the message payload size and type byte
	}
}
