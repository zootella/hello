package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;

// SSSST
// SSSS is the size of the rest of the message, 1
// T is the type byte, 2
public class InterestedMessage extends Message {

	// -------- Identify --------

	/** 0x02, the byte that identifies an Interested message. */
	public static final byte type = 0x02;
	
	// -------- Send --------
	
	/** Make a new InterestedMessage to send to a peer. */
	public InterestedMessage() {} // An InterestedMessage doesn't carry any information
	
	/** Add the 5 bytes of data of this InterestedMessage to the given Bay to send to a peer. */
	public void toBay(Bay bay) {
		Number.toBay(bay, 4, 1); // The first 4 bytes are the payload size, 1
		bay.add(type);           // The payload is just the type byte
	}
	
	// -------- Receive --------
	
	/** Parse the data of a BitTorrent Interested message into this new InterestedMessage object. */
	public InterestedMessage(Data d) {} // An InterestedMessage doesn't carry any information
}
