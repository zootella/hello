package org.limewire.hello.base.web;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.data.TextSplit;
import org.limewire.hello.base.exception.ChopException;


public class Header {
	
	// -------- Inside parts --------
	
	/** The name of this Header, like "Header-Name". */
	public String name;
	/** The value of this Header, like "value" in "Header-Name: value\r\n". */
	public String value;
	
	// -------- Make a new Header object to upload --------
	
	/** Make a new Header object with the given name and value, like new Header("Content-Length", "456"). */
	public Header(String name, String value) {
		this.name = name; // Save the strings in this new object
		this.value = value;
	}
	
	/** Convert this HeaderGroup into a String to upload to a peer. */
	public String toString() {
		return name + ": " + value + "\r\n";
	}

	// -------- Parse a header we've downloaded --------
	
	/**
	 * Parse one line of text at the start of d into a new Header object, and remove it from d.
	 * If the whole line hasn't arrived yet, throws a ChopException and doesn't change d.
	 * A HTTP header is a line of text that has a name and a value and ends "\r\n", like this:
	 * 
	 * Header-Name: header value\r\n
	 */
	public Header(Data d) throws ChopException {
		String line = Text.line(d); // Throws a ChopException if it can't find "\n" to end the line
		TextSplit split = Text.split(line, ":"); // The header name and its value are separated by a ":"
		name = split.before.trim();
		value = split.after.trim(); // Remove the space in ": "
	}

	// -------- HTTP header names and values --------
	
	/** "GET", the HTTP command to download a file */
	public static final String get = "GET";
	/** "HTTP/1.1", the version of HTTP. */
	public static final String http11 = "HTTP/1.1";
	
	/** "User-Agent", the HTTP header that names the program. */
	public static final String userAgent = "User-Agent";
	
	/** "Host", the HTTP header that tells the domain name of the Web site. */
	public static final String host = "Host";
	
	/** "Connection", the HTTP header that tells if the peers should close the connection or keep it open. */
	public static final String connection = "Connection";
	/** "close", the value of the connection header that means the peers should close the connection. */
	public static final String close = "close";
	
	/** "Transfer-Encoding", the name of the HTTP header that tells how the data is encoded. */
	public static final String transferEncoding = "Transfer-Encoding";
	/** "chunked", the name of a kind of transfer encoding. */
	public static final String chunked = "chunked";

	/** "Content-Length", the name of the HTTP header that tells the file size. */
	public static final String contentLength = "Content-Length";
	/** "Content-Type", the name of the HTTP header that tells what kind of file it is. */
	public static final String contentType = "Content-Type";
	/** "Content-Disposition", the name of the HTTP header that includes the file name. */
	public static final String contentDisposition = "Content-Disposition";
	/** "filename", the name of the attribute in the content disposition header that tells the file name. */
	public static final String filename = "filename";
}
