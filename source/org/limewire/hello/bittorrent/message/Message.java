package org.limewire.hello.bittorrent.message;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.exception.PeerException;
import org.limewire.hello.bittorrent.meta.Meta;


// as the program runs, there is never a Message object
// if you see a Message object in the code, it's actually something more specific, like a ChokeMessage or an InterestedMessage
public abstract class Message {
	
	// -------- Methods you can call without having an object that extends Message yet --------

	/**
	 * Remove the data of one BitTorrent message from the start of d, and parse it into a new object that extends Message.
	 * 
	 * The object parse() returns will extend Message to match the type of the message.
	 * For instance, if d starts with the data of a BitTorrent Choke message, you'll get a ChokeMessage object.
	 * 
	 * Takes a Data object with the data of a BitTorrent message at its start, and more data afterwards.
	 * Returns a new Message object and removes the data it parsed to make it from d.
	 * If a whole message hasn't arrived yet, doesn't change d and throws ChopException, try again later.
	 * If the message data has a mistake, removes it from d and throws MessageException, try the next message now.
	 * If the mistake is so severe we can't move beyond it, doesn't change d and throws PeerException, disconnect.
	 * 
	 * Takes a Meta object to check that the piece number and clip is valid for the .torrent file.
	 * For instance, d might have perfectly formed data of a Have message, announcing a peer has piece number 987.
	 * If meta says the torrent only has 950 pieces, parse() will throw a MessageException.
	 */
	public static Message parse(Data d, Meta meta) throws ChopException, MessageException, PeerException {
		
		// Slice 1 message from the start of the data in d
		byte type; // The byte in the message that tells what type it is
		Data m;    // We'll clip m around the data of the message at the start of d
		try {

			// Look at the first 5 bytes, or throw a ChopException if 5 bytes haven't arrived yet
			int size = 4 + Number.toInt(d.start(4), 0); // Find out how big the message is
			type = d.get(4);                            // Find out what type of message it is

			// If it's a Piece message, grab just the headers at the start, not the file data
			if (type == PieceMessage.type) size = PieceMessage.headerSize;
			
			// Clip m around the message data, and remove it from d
			m = d.cut(size);

		// If we can't slice the message, we must disconnect from the peer it came from
		} catch (MessageException e) { throw new PeerException(); }
		
		// Parse the data we slied
		try {
			
			// Parse the data we sliced into an object that represents the type of Message it is
			if      (type == ChokeMessage.type)        return new ChokeMessage(m);
			else if (type == UnchokeMessage.type)      return new UnchokeMessage(m);
			else if (type == InterestedMessage.type)   return new InterestedMessage(m);
			else if (type == UninterestedMessage.type) return new UninterestedMessage(m);
			else if (type == HaveMessage.type)         return new HaveMessage(m, meta); // Check contents against meta
			else if (type == BitFieldMessage.type)     return new BitFieldMessage(m, meta);
			else if (type == RequestMessage.type)      return new RequestMessage(m, meta);
			else if (type == PieceMessage.type)        return new PieceMessage(m, meta);
			else if (type == CancelMessage.type)       return new CancelMessage(m, meta);
			else throw new MessageException(); // We can't parse the mystery message, but we've removed its data from d

		// If the message we sliced is chopped off, it's a MessageException
		} catch (ChopException e) { throw new MessageException(); } // Trying again with more data won't help
	}

	// -------- Methods you can call on any object that extends Message --------

	/** Turn this object that extends Message into data. */
	public Data data() {
		Bay bay = new Bay();
		toBay(bay); // Calls the correct type-specific method, like ChokeMessage.toBay(bay) or InterestedMessage.toBay(bay)
		return bay.data();
	}

	// -------- Methods every class that extends Message must have code for --------
	
	// Add the data of this object that extends Message to the given Bay
	public abstract void toBay(Bay bay); // Each class that extends Message has this method with code that does it
}
