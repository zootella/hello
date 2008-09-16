package org.limewire.hello.base.encode;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Split;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.exception.MessageException;


public class Cobs {
	
	/** COBS-encode some data, hiding all the 0s without making it much larger, and return the COBS-encoded data. */
	public static Data encode(Data d) { Bay bay = new Bay(); encode(bay, d); return bay.data(); }
	/** COBS-encode some data, hiding all the 0s without making it much larger, and add the COBS-encoded data to bay. */
	public static void encode(Bay bay, Data d) {
		try {

			// Loop encoding blocks of data
			while (true) {
				
				// Look for a 0 byte in the first 254 bytes of data
				Split split = d.begin(254).split((byte)0);
				
				// Make a block
				bay.add((byte)(split.before.size() + 1)); // The first byte is the size of the whole block
				bay.add(split.before);                    // Add the data
				
				// Point d at the data that remains for us to encode in the next loop
				d = d.after(split.before.size() + split.tag.size());
				
				// If we didn't find a 0, there's no data for next time, and we didn't just add a full block, we're done
				if (!split.found && d.isEmpty() && split.before.size() != 254) return;
			}
			
		} catch (ChopException e) { throw new CodeException(); } // There is a mistake in the code in this try block
	}

	/** Remove the COBS-encoding from d, restoring it to the way it was with 0s, and return the decoded data. */
	public static Data decode(Data d) throws ChopException, MessageException { Bay bay = new Bay(); decode(bay, d); return bay.data(); }
	/** Remove the COBS-encoding from d, restoring it to the way it was with 0s, and add the decoded data to bay. */
	public static void decode(Bay bay, Data d) throws ChopException, MessageException {

		// Loop decoding blocks of data
		while (true) {

			// Parse a block
			int size = Number.toInt(d.start(1), 1); // The first byte tells the block size, make sure it's 1 or more
			bay.add(d.clip(1, size - 1));           // Get the block's data, throw ChopException if d doesn't have it all
			
			// Move beyond it
			d = d.after(size);
			if (d.isEmpty()) return; // If we're out of data, we're done

			// Unless the size was 255, add a 0 byte between this block and the next one
			if (size != 255) bay.add((byte)0);
		}
	}
}
