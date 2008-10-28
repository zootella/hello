package org.limewire.hello.base.internet.name;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.data.TextSplit;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;

public class IpPort implements Comparable<IpPort> {
	
	// -------- Parts --------

	/** The Ip address, like 1.2.3.4. */
	public final Ip ip;
	/** The port number, like 80. */
	public final int port;
	//TODO use the separate Port class
	
	/** Make a new IpPort object with the given Ip address and port number. */
	public IpPort(Ip ip, int port) { this.ip = ip; this.port = port; }

	// -------- Convert to and from a Java InetSocketAddress object, which contains an IP address and port number --------
	
	/** Convert this IpPort into a Java InetSocketAddress object. */
	public InetSocketAddress toInetSocketAddress() { return new InetSocketAddress(ip.toInetAddress(), port); }
	
	/** Make a new IpPort with the IP address and port number of the given Java InetSocketAddress object. */
	public IpPort(InetSocketAddress a) { ip = new Ip(a.getAddress()); port = a.getPort(); }

	// -------- Convert to and from a String like "1.2.3.4:5" --------
	
	/** Convert this IpPort into text like "1.2.3.4:5". */
	public String toString() { return ip.toString() + ":" + port; }

	/** Make a new IpPort from a String like "1.2.3.4:5". */
	public IpPort(String s) throws MessageException {
		TextSplit split = Text.split(s, ":");
		if (!split.found) throw new MessageException();
		ip = new Ip(split.before);
		port = Number.toInt(split.after, 0, 65535); // Make sure the port number is 0 through 65535
	}

	// -------- Convert to and from 6 bytes of data --------

	/**
	 * "123405", the default pattern that describes how the IP and port 1.2.3.4:5 are arranged in 6 bytes of data.
	 * There are 8 patterns you can use:
	 * 
	 * "123405" big endian IP, then big endian port, this default
	 * "123450" big endian IP, then little endian port
	 * "432105" little endian IP, then big endian port
	 * "432150" little endian IP, then little endian port
	 * "051234" big endian port, then big endian IP
	 * "054321" big endian port, then little endian IP
	 * "501234" little endian port, then big endian IP 
	 * "504321" little endian port, then little endian IP
	 */
	public static final String pattern = "123405";
	
	/** Convert this IpPort into 6 bytes of data, 1.2.3.4:5 becomes 01020304 0005. */
	public Data data() { return data(pattern); } // Use the default pattern
	/** Convert this IpPort into 6 bytes of data using a pattern like "123405". */
	public Data data(String pattern) { Bay bay = new Bay(); toBay(bay, pattern); return bay.data(); }
	
	/** Convert this IpPort into 6 bytes added to bay, 1.2.3.4:5 becomes 01020304 0005. */
	public void toBay(Bay bay) { toBay(bay, pattern); } // Use the default pattern
	/** Convert this IpPort into 6 bytes added to bay using a pattern like "123405". */
	public void toBay(Bay bay, String pattern) {
		if (pattern.charAt(0) == '1' || pattern.charAt(0) == '4') {         // IP first
			ip.toBay(bay, Text.start(pattern, 4));
			if (pattern.charAt(4) == '0') Number.toBay(bay, 2, port);
			else                          Number.toBayLittle(bay, 2, port);
		} else {                                                            // Port first
			if (pattern.charAt(0) == '0') Number.toBay(bay, 2, port);
			else                          Number.toBayLittle(bay, 2, port);
			ip.toBay(bay, Text.after(pattern, 2));
		}
	}

	/** Make a new IpPort from 6 bytes at the start of d, 01020304 0005 becomes 1.2.3.4:5. */
	public IpPort(Data d) throws MessageException { this(d, pattern); } // Use the default pattern
	/** Make a new IpPort from 6 bytes at the start of d using a pattern like "123405". */
	public IpPort(Data d, String pattern) throws MessageException {
		try {
			if (pattern.charAt(0) == '1' || pattern.charAt(0) == '4') {                          // IP first
				ip = new Ip(d, Text.start(pattern, 4));
				if (pattern.charAt(4) == '0') port = Number.toInt(d.clip(4, 2), 0, 65535);
				else                          port = Number.toIntLittle(d.clip(4, 2), 0, 65535);
			} else {                                                                             // Port first
				if (pattern.charAt(0) == '0') port = Number.toInt(d.clip(0, 2), 0, 65535);
				else                          port = Number.toIntLittle(d.clip(0, 2), 0, 65535);
				ip = new Ip(d.after(2), Text.after(pattern, 2));
			}
		} catch (ChopException e) { throw new MessageException(); } // d isn't big enough
	}

	// -------- Compare --------

	/**
	 * Compare this IpPort to a given one to determine which should come first in sorted order.
	 * @return Negative to sort this first, positive if o is first, 0 if they're the same
	 */
	public int compareTo(IpPort o) {
		int sort = ip.compareTo(o.ip); // Compare the IP addresses
		if (sort != 0) return sort;    // They're different, sort based on that
		return port - o.port;          // The IP addresses are the same, sort based on the port numbers
	}

	/** true if the given IpPort has the same IP address and port number as this one. */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof IpPort)) return false;
		return ip.equals(((IpPort)o).ip) && port == ((IpPort)o).port;
	}
	
	// -------- Convert a List of IpPort objects to and from data and text --------

	/** Turn a List of IpPort objects into Data with each in 6 bytes, like "123405123405123405". */
	public static Data data(List<IpPort> list) { Bay bay = new Bay(); toBay(bay, list); return bay.data(); }
	/** Turn a List of IpPort objects into Data with each in 6 bytes, like "123405123405123405", added to bay. */
	public static void toBay(Bay bay, List<IpPort> list) {
		for (IpPort p : list) p.toBay(bay); // Turn each IpPort into 6 bytes of data, and add the bytes to bay
	}
	
	/** Parse data like "123405123405123405", with each IP address and port number in 6 bytes, into a List of IpPort objects. */
	public static List<IpPort> list(Data d) throws MessageException {
		try {
			Data data = d.copy(); // Make a copy to not change d
			List<IpPort> list = new ArrayList<IpPort>();
			while (data.hasData()) list.add(new IpPort(data.cut(6))); // Cut 6 bytes from the start of data until it runs out
			return list;
		} catch (ChopException e) { throw new MessageException(); } // The data didn't split into 6-byte pieces
	}

	/** Turn a List of IpPort objects into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5". */
	public static String toString(List<IpPort> list) { StringBuffer b = new StringBuffer(); toString(b, list); return b.toString(); }
	/** Turn a List of IpPort objects into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5", added to the given StringBuffer. */
	public static void toString(StringBuffer b, List<IpPort> list) {
		boolean separate = false;
		for (IpPort p : list) {
			if (separate) b.append(","); // Don't add a "," at the very start
			separate = true;
			b.append(p.toString());
		}
	}

	/** Parse text like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5" into a List of IpPort objects. */
	public static List<IpPort> list(String s) throws MessageException {
		List<String> words = Text.words(s, ",");
		List<IpPort> list = new ArrayList<IpPort>();
		for (String word : words) list.add(new IpPort(word)); // Each word is like "1.2.3.4:5"
		return list;
	}

	/** Turn a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5" into Data like "123405123405123405". */
	public static Data listToData(String s) throws MessageException { Bay bay = new Bay(); listToBay(bay, s); return bay.data(); }
	/** Turn a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5" into Data like "123405123405123405" added to bay. */
	public static void listToBay(Bay bay, String s) throws MessageException { toBay(bay, list(s)); } // Go through list(String)

	/** Turn data like "123405123405123405" into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5". */
	public static String listToString(Data d) throws MessageException { StringBuffer b = new StringBuffer(); listToString(b, d); return b.toString(); }
	/** Turn data like "123405123405123405" into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5", added to the given StringBuffer. */
	public static void listToString(StringBuffer b, Data d) throws MessageException { toString(b, list(d)); } // Go through list(Data)
}
