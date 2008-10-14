package org.limewire.hello.base.web;

import java.io.IOException;

import org.limewire.hello.all.Program;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.data.Number;
import org.limewire.hello.base.data.Split;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.CodeException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.OldFile;
import org.limewire.hello.base.internet.name.Ip;
import org.limewire.hello.base.internet.name.IpPort;
import org.limewire.hello.base.internet.old.OldInternet;
import org.limewire.hello.base.internet.old.OldTube;
import org.limewire.hello.base.state.old.OldState;
import org.limewire.hello.base.time.OldTime;


// A Get object performs one HTTP GET or HEAD request to a Web server
// It uses a Tube for the TCP socket connection
public class OldGet {
	
	// -------- Make a HTTP GET request to a Web server --------

	/**
	 * Make a new Get object to perform one HTTP GET request to a Web server.
	 * 
	 * @param internet A link to the program's Internet object
	 * @param url      The Web URL to get
	 * @param ip       The IP address of the Web server
	 * @param file     The file to save the downloaded data to
	 */
	public OldGet(OldInternet internet, Url url, Ip ip, OldFile file) {

		// Save a reference to the file we'll put data in
		this.file = file;
		
		// Connect a new TCP socket to the given IP address and port number
		tube = internet.connect(new IpPort(ip, url.port));
		
		/*
		 * We ask for the file by sending the Web server a group of HTTP request headers like this:
		 * 
		 * GET /folder/file.ext?parameters HTTP/1.1
		 * Site: www.site.com
		 * User-Agent: Hello/1.0
		 * Connection: close
		 * 
		 * By saying "Connection: close", we tell the server we're only going to use this TCP socket connection to ask for this one file.
		 */
		
		// Compose our HTTP request headers, and give them to the Tube to upload
		request = new HeaderGroup();
		request.greeting = Header.get + " " + url.get + " " + Header.http11;
		request.add(Header.host, url.site);
		request.add(Header.userAgent, Program.name + "/" + Program.versionText);
		request.add(Header.connection, Header.close); // We won't ask for a second file in this connection
		tube.upload.add(request.data());
	}

	/** The file this Get object was given to save data into. */
	private OldFile file;
	
	/**
	 * Our Tube that we talk to the Web server through.
	 * A Get object always has a Tube, so you can use this reference without making sure it's not null first
	 */
	private OldTube tube;

	/** The group of HTTP request headers we sent the Web server to request the file. */
	private HeaderGroup request;
	
	/** The group of HTTP response headers the Web server gave us before the file data. */
	public HeaderGroup response;

	// -------- Periodically pulse this object to move things forward --------

	/**
	 * See what's happened and move things to the next step.
	 * The program periodically calls pulse() on this object.
	 */
	public void pulse() {
		
		// If we're closed, there is nothing more for a pulse to do, don't let one change us
		if (state().isClosed()) return;
		
		try {

			// We're still looking for the Web server's group of HTTP response headers
			if (response == null) downloadResponseHeaders();

			// We just got, or previously got, the response headers
			if (response != null) {

				// The "Content-Length" header tells us how big the file is, download it
				if (response.number(Header.contentLength) != -1) {
					downloadContentLength();

				// The Web server has broken the file into chunks with a size before each one, download them
				} else if (response.header(Header.transferEncoding, Header.chunked)) {
					while (downloadChunkedTransfer()); // Keep calling downloadChunkedTransfer() until it returns false or throws a State

				// Neither of those, we can't download the file because we don't know how big it is
				} else {
					throw new OldState(OldState.couldNot, "Size Unknown");
				}
			}

			// After all that, if nothing else closed us, if our Tube is closed, we're closed for the same reason
			if (tube.state().isClosed()) throw tube.state(); // The state is cancelled or socketException

		// One of the methods we called realized we are done, close this Get object with the reason it found
		} catch (OldState state) { close(state); }
	}
	
	/**
	 * Download the group of HTTP response headers at the very start of the data the Web server sends us.
	 * Looks for the lines of ASCII text that end with a blank line, parsing them into the Header object named response.
	 * Makes sure the HTTP status code is 200.
	 */
	private void downloadResponseHeaders() throws OldState {
		
		// Parse the headers into a HeaderGroup object, and remove them from the Tube
		try {
			Data data = tube.download.data(); // Make a local copy to see much HeaderGroup(data) removes
			response = new HeaderGroup(data); // Returns the new HeaderGroup object, or throws a ChopException
			tube.download.remove(tube.download.data().size() - data.size());
		} catch (ChopException e) { return; } // The whole group hasn't arrived yet, a later pulse will come back here to look again
		
		// Make sure the status code is 200
		if (response.statusCode() != 200) {
			String s = response.statusMessage(); // Get text like "Not Found" from the first line like "404 Not Found"
			if (Text.isBlank(s)) s = "Bad Status Code"; // Make sure it's not blank
			throw new OldState(OldState.couldNot, s);
		}
	}

	/**
	 * The Web server sent HTTP response headers that told us how big the file is after them.
	 * Download the file.
	 */
	private void downloadContentLength() throws OldState {

		// Move all the data from the Tube to the file
		downloadFileData(tube.download.size());
		
		// If we've downloaded the whole content length, we're done
		if (response.number(Header.contentLength) == saved) throw new OldState(OldState.completed, "Done");
	}

	/** Move size bytes of file data from our Tube to our file. */
	private void downloadFileData(int size) throws OldState {
		try {
			
			// Move data from the Tube to the file
			file.write(saved, tube.download.data().start(size)); // Take just size, not everything the Tube has
			tube.download.remove(size);
			
			// Record we saved that many more bytes to our file
			saved += size;

		} catch (IOException e) { throw new OldState(OldState.fileException); // There was a problem saving to disk
		} catch (ChopException e) { throw new CodeException(); } // There is a mistake in the code
	}

	/**
	 * The number of bytes we've saved to the file.
	 * This is also the distance into the file where we'll start writing next.
	 */
	public long saved;

	/**
	 * The Web server is using chunked transfer coding, sending the file in a series of chunks.
	 * Download the file.
	 * 
	 * @return false to call this method again later, when more data may have arrived.
	 *         true to call this method again right now to continue looking at the data that's already here.
	 */
	private boolean downloadChunkedTransfer() throws OldState {
		
		/*
		 * HTTP 1.1 chunked transfer coding looks like this:
		 * 
		 * HTTP/1.1 200 OK\r\n             Response headers
		 * Transfer-Encoding: chunked\r\n
		 * \r\n
		 * 9; note\r\n                     Chunk
		 * DATADATAD\r\n
		 * 1a; note\r\n                    Chunk, 1a is base 16 for 26
		 * DATADATADATADATADATADATADA\r\n
		 * 0; note\r\n                     Chunk
		 * \r\n
		 * 
		 * The response headers include "Transfer-Encoding: chunked" instead of "Content-Length: 12345".
		 * Each chunk begins with a line of ASCII text that ends "\r\n", like "9; note\r\n"
		 * The line starts with the size, in hexidecimal numerals, like "9" or "1a".
		 * After the size, there might be a note.
		 * After the size line is the chunk of file data.
		 * After the file data is another "\r\n".
		 * To tell us we have the whole file, the server sends a 0 size chunk that has no data at the end.
		 * There might be more HTTP headers after all the chunks.
		 */

		// We're between chunks right now
		if (chunk == 0) {
			
			// Look for the size line
			Split split = tube.download.data().split("\r\n");
			if (!split.found) return false; // The whole size line hasn't arrived yet, come back later
			
			// Get the size line as a String, and remove it from our Tube
			String s = split.before.toString();
			tube.download.remove(split.before.size() + split.tag.size());
			if (Text.isBlank(s)) return true; // We hit the blank line that ends a chunk, try again now that we're past it

			// Read the size number from it
			s = Text.before(s, ";").trim(); // Parse "1a" from "1a ; note", you have seen spaces before the semicolon
			int i = -1;
			try { i = Number.toIntBase16(s, 0); } catch (MessageException e) {}
			if (i == -1) throw new OldState(OldState.couldNot,  "Size Unknown"); // Unable to read the size number
			if (i ==  0) throw new OldState(OldState.completed, "Done");         // A 0 size chunk means we already have the whole file

			// Save the size and try again now to get the data
			chunk = i;
			return true;

		// We're in the middle of a chunk
		} else { // chunk isn't 0, we have to download that many more bytes to finish this chunk

			// Move data from the Tube to the file
			int take = Math.min(chunk, tube.download.size()); // Don't take more than the chunk or Tube have
			downloadFileData(take);
			chunk -= take;

			// If chunk is 0, we finished a chunk, return true to try to download the next one right now
			return chunk == 0;
		}
	}

	/** How much of the chunk we're on that we have left to download. */
	private int chunk;

	// -------- Get this Get object's current state, and close it --------

	/**
	 * Find out what this Get object's current state is.
	 * 
	 * Active operations:
	 * 
	 * opening          This Get has initiated a socket connection to the Web server, and is waiting for it to complete or fail.
	 * doing            This Get has an open socket connection, and sent HTTP request headers to download response headers and file data.
	 * 
	 * Closed outcomes:
	 * 
	 * cancelled        The program closed this Get because it doesn't need it anymore, and it closed its TCP socket connection.
	 * completed        This Get finished downloading the whole file from the Web server, and closed the socket.
	 * couldNot         The Web server responded with a status code other than 200 "OK", and this Get closed the socket.
	 * socketException  Java threw this Get a socket exception because it lost its TCP socket connection.
	 * fileException    Java threw this Get a file exception when it tried to save downloaded data to the file.
	 * 
	 * @return A State object that describes our state right now
	 */
	public OldState state() {

		// If we're closed, that's our state, return it
		if (closed != null) return closed;

		// If our Tube is opening, so are we
		if (tube.state().state == OldState.opening) return OldState.opening();
		
		// Otherwise, our Tube is either doing, or closed because of a socket exception
		return new OldState(OldState.doing); // Say we're doing, we'll notice our Tube's closed state on our next pulse
	}

	/**
	 * Close our Tube's TCP socket connection.
	 * 
	 * @param closed A State object that tells how and why we closed
	 */
	public void close(OldState closed) {
		
		// Only let us close once, and save the given final closed state
		if (state().isClosed()) return;
		this.closed = closed;

		// Cancel our Tube
		tube.close(OldState.cancelled()); // Have our Tube close its socket and remove itself from the program's list of Tube objects
	}

	/** Our final state that tells how and why we closed, or null if we're not closed yet. */
	private OldState closed;

	// -------- Speed and time information from our Tube --------
	
	/** Our current download speed, in bytes/second. */
	public int speed() {
		return tube.download.speed.speed();
	}

	/**
	 * The Time when we tried to connect to the Web server.
	 * How long we've been waiting for our connection to go through.
	 */
	public OldTime attemptTime() {
		return tube.attempt.copy();
	}
	
	/** The Time when we last heard from the Web server. */
	public OldTime responseTime() {
		return tube.response();
	}
}
