package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.size.SprayPattern;
import org.limewire.hello.bittorrent.meta.Meta;


// SSSSTbitfieldbitfieldbitfield
// SSSS is the size of the rest of the message
// T is the type byte, 5
// after that is the bitfield
public class BitFieldMessage extends Message {

	// -------- Identify --------

	/** 0x05, the byte that identifies a BitField message. */
	public static final byte type = 0x05;
	
	// -------- Contents --------
	
	/** The array of bits that show which pieces the peer that sent this BitFieldMessage says it has. */
	public SprayPattern pattern;
	
	// -------- Send --------
	
	/**
	 * Make a new BitFieldMessage to send to a peer.
	 * @param pattern A BitPattern that shows which pieces we have
	 */
	public BitFieldMessage(SprayPattern pattern) {
		this.pattern = pattern;
	}

	/** Add the data of this BitFieldMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 1 + pattern.dataSize()); // The first 4 bytes are the payload size
		bay.add(type);                                // The payload starts with the type byte
		pattern.toBay(bay);                           // After that is the bitfield
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent BitField message into this new BitFieldMessage object. */
	public BitFieldMessage(Data d, Meta meta) throws ChopException, MessageException {
		pattern = new SprayPattern(d.after(5), meta.pieces.size()); // One bit for each piece
	}
}
