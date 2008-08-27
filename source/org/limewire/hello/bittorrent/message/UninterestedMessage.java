package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;

// SSSST
// SSSS is the size of the rest of the message, 1
// T is the type byte, 3
public class UninterestedMessage extends Message {

	// -------- Identify --------

	/** 0x03, the byte that identifies an Uninterested message. */
	public static final byte type = 0x03;
	
	// -------- Send --------
	
	/** Make a new UninterestedMessage to send to a peer. */
	public UninterestedMessage() {} // An UninterestedMessage doesn't carry any information
	
	/** Add the 5 bytes of data of this UninterestedMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 1); // The first 4 bytes are the payload size, 1
		bay.add(type);           // The payload is just the type byte
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent Uninterested message into this new UninterestedMessage object. */
	public UninterestedMessage(Data d) {} // An UninterestedMessage doesn't carry any information
}
