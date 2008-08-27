package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;

// SSSST
// SSSS is the size of the rest of the message, 1
// T is the type byte, 0
public class ChokeMessage extends Message {

	// -------- Identify --------

	/** 0x00, the byte that identifies a Choke message. */
	public static final byte type = 0x00;
	
	// -------- Send --------
	
	/** Make a new ChokeMessage to send to a peer. */
	public ChokeMessage() {} // A ChokeMessage doesn't carry any information

	/** Add the 5 bytes of data of this ChokeMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 1); // The first 4 bytes are the payload size, 1
		bay.add(type);           // The payload is just the type byte
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent Choke message into this new ChokeMessage object. */
	public ChokeMessage(Data d) {} // A ChokeMessage doesn't carry any information
}
