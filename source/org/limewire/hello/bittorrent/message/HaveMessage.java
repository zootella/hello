package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.bittorrent.meta.Meta;


// SSSSTPPPP
// SSSS is the size of the rest of the message, 5
// T is the type byte, 4
// PPPP is the piece number
public class HaveMessage extends Message {

	// -------- Identify --------

	/** 0x04, the byte that identifies a Have message. */
	public static final byte type = 0x04;
	
	// -------- Contents --------

	/** The piece number the peer that sent this HaveMessage says it has. */
	public int piece;
	
	// -------- Send --------
	
	/**
	 * Make a new HaveMessage to send to a peer.
	 * @param piece The piece number we say we have
	 */
	public HaveMessage(int piece) {
		this.piece = piece;
	}
	
	/** Add the data of this HaveMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 5);     // The first 4 bytes are the payload size, 5
		bay.add(type);               // The payload starts with the type byte
		Number.toBay(bay, 4, piece); // The next 4 bytes are the piece number
	}

	// -------- Receive --------

	/** Parse the data of a BitTorrent Have message into this new HaveMessage object. */
	public HaveMessage(Data d, Meta meta) throws ChopException, MessageException {
		piece = Number.toInt(d.clip(5, 4), 0, meta.pieces.size() - 1); // Check the piece number
	}
}
