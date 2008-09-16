package org.limewire.hello.base.web;


import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.data.TextSplit;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.Name;

public class HeaderGroup {
	
	// -------- Inside parts --------

	/**
	 * The first line of this group of HTTP headers.
	 * A String like "GET /folder/file.ext?parameters HTTP/1.1" or "GNUTELLA CONNECT/0.6".
	 * Doesn't include the "\r\n" at the end.
	 */
	public String greeting;

	/**
	 * The header names and their values in this group of HTTP headers.
	 * A HTTP header in data is like "Header-Name: header value\r\n".
	 * Here, it's a Header object with the name "Header-Name" and value "header value" in this List.
	 */
	private List<Header> headers;

	// -------- Make a new empty HeaderGroup, add headers, and upload it --------
	
	/** Make a new empty HeaderGroup object to represent a group of HTTP headers we will upload. */
	public HeaderGroup() {
		headers = new ArrayList<Header>(); // Make headers a new empty ArrayList of Header objects
	}

	/** Add a header to this group, like add("Content-Length", "567"). */
	public void add(String name, String value) {
		headers.add(new Header(name, value)); // Make a new Header object and add it to our List
	}
	
	/** Convert this HeaderGroup into Data to upload to a peer. */
	public Data data() { return new Data(toString()); }
	/** Convert this HeaderGroup into a String to upload to a peer. */
	public String toString() {
		StringBuffer s = new StringBuffer(); // Make a StringBuffer we'll add text to
		s.append(greeting + "\r\n");         // Start with the greeting line
		for (Header header : headers)        // Add all our headers
			s.append(header.toString());
		s.append("\r\n");                    // A blank line terminates the group of headers
		return s.toString();
	}

	// -------- Parse a group of headers we've downloaded, and look at them --------
	
	/**
	 * Parse one group of HTTP headers at the start of d into a new HeaderGroup object.
	 * Removes the the data it parses from d.
	 * If the whole group of HTTP headers hasn't arrived yet, throws a ChopException and doesn't change d.
	 * A group of HTTP headers has lines of text separated by "\r\n", and ends with a blank line, like this:
	 * 
	 * GREETING TEXT\r\n
	 * Header-Name: header value\r\n
	 * Header-Name-X: a different header value\r\n
	 * Another-Header: another value, with a comma in it\r\n
	 * \r\n
	 */
	public HeaderGroup(Data d) throws ChopException {
		
		// Call the HeaderGroup() constructor to make headers a new empty ArrayList
		this();
		
		// Copy d so we can throw an exception without changing it
		Data data = d.copy();
		
		// Parse the first line, the greeting text
		greeting = Text.line(data).trim(); // If data doesn't have a "\n", throws a ChopException
		
		// Loop to parse each line until we reach the blank line that ends the group of headers
		while (true) {
			Header header = new Header(data);     // Parse the next line into a Header object
			if (Text.isBlank(header.name)) break; // If it's the blank line, we're done
			headers.add(header);                  // It's not blank, add it to our list
		}

		// Remove the data we parsed from d
		d.keep(data.size());
	}

	/** true if this HeaderGroup has the given header name with the given value, matches cases. */
	public boolean header(String name, String value) {
		return Text.same(header(name), value); // Matches cases in the header name and value text
	}
	
	/** The number value of the given header name, like "Content-Length", or -1 if the header isn't in this group. */
	public long number(String name) {
		try {
			return Number.toLong(header(name)); // Get the text and convert it into a number
		} catch (MessageException e) { return -1; } // If that didn't work, return -1
	}

	/**
	 * Parse the value of an attribute within the value of a given header name, "" if not found.
	 * For instance, given this header:
	 * 
	 * Header-Name: attribute1; attribute2="second attribute's value"
	 * 
	 * attribute("Header-Name", "attribute2") will return the String "second attribute's value".
	 */
	public String attribute(String name, String attribute) {
		String s = header(name);                                          // Get the value, "" if not found
		List<String> values = Text.words(s, ";");                         // Split by ";"
		for (String value : values) {                                     // Loop for each part between ";"
			TextSplit split = Text.split(value, "=");                     // Look before and after a "="
			if (split.found && Text.same(split.before.trim(), attribute)) // This is the attribute
				return Text.trim(split.after, "\"", "'");                 // Remove space, " and ' from the ends
		}
		return ""; // Not found
	}

	/** Look up the given header name in this HeaderGroup, and return the text value, or "" if not found. */
	public String header(String name) {
		for (Header header : headers)
			if (Text.same(header.name, name)) return header.value; // Header names are not case sensitive
		return ""; // Not found
	}

	// -------- Look at HTTP response headers --------

	/**
	 * Get the status code, like 200 or 503, from the first line this group of HTTP headers, or -1 if not found.
	 * Looks in the greeting line, like "HTTP/1.1 200 OK".
	 */
	public int statusCode() {
		List<String> words = Text.words(greeting);  // Make words like "HTTP/1.1", "200", "OK"
		if (words.size() < 3) return -1;            // Make sure we got at least 3 parts
		try {
			return Number.toInt(words.get(1), 0);   // Turn the 2nd one into a number, ane make sure it isn't negative
		} catch (MessageException e) { return -1; } // If that didn't work, return -1
	}

	/**
	 * Get the status message, like "OK", or "" if not there.
	 * Looks in the greeting line, like "HTTP/1.1 200 OK".
	 */
	public String statusMessage() {
		String s = greeting;           // greeting is like "HTTP/1.1 200 OK"
		s = Text.after(s, " ").trim(); // Make s like "200 OK"
		s = Text.after(s, " ").trim(); // Make s like "OK"
		return s;
	}

	/**
	 * Get the file name and extension from this group of HTTP headers.
	 * Looks for a header like:
	 * 
	 * Content-Disposition: attachment; filename="File Name.ext"
	 */
	public Name name() {
		return new Name(attribute(Header.contentDisposition, Header.filename));
	}

	/**
	 * Look for common content types in this group of HTTP headers to choose a file name extension like "html" or "jpg".
	 * Looks for a header like:
	 * 
	 * Content-Type: text/html; charset=utf-8
	 * 
	 * @return A String like "html", "gif", or "jpg", or "" if there is no content type header or it says some other type.
	 */
	public String extension() {
		String s = header(Header.contentType);
		s = Text.before(s, ";"); // It might have something after it, like "Content-Type: text/html; charset=utf-8"
		if      (Text.same(s, "text/html"))  return "html";
		else if (Text.same(s, "image/gif"))  return "gif";
		else if (Text.same(s, "image/jpeg")) return "jpg";
		else return "";
	}
}
