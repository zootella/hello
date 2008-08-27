package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;

// SSSST
// SSSS is the size of the rest of the message, 1
// T is the type byte, 1
public class UnchokeMessage extends Message {

	// -------- Identify --------

	/** 0x01, the byte that identifies an Unchoke message. */
	public static final byte type = 0x01;
	
	// -------- Send --------
	
	/** Make a new UnchokeMessage to send to a peer. */
	public UnchokeMessage() {} // An UnchokeMessage doesn't carry any information
	
	/** Add the 5 bytes of data of this UnchokeMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 1); // The first 4 bytes are the payload size, 1
		bay.add(type);           // The payload is just the type byte
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent Unchoke message into this new UnchokeMessage object. */
	public UnchokeMessage(Data d) {} // An UnchokeMessage doesn't carry any information
}
