package org.limewire.hello.base.web;

import java.io.IOException;

import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.file.Name;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.file.PathName;
import org.limewire.hello.base.file.Save;
import org.limewire.hello.base.internet.name.Ip;
import org.limewire.hello.base.internet.web.DomainTask;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.state.old.OldState;
import org.limewire.hello.base.time.OldTime;
import org.limewire.hello.base.time.Time;
import org.limewire.hello.base.user.OldDescribe;


// a relentless web download
// each DownloadRow object has a download object
// the program could also use this to just download a .torrent file
// this is the class that exposes the api of url, file path, and it does it

// closing a download will kill its temp file
// if it has a public non null saved path, that file is your responsibility

public class WebDownload {

	// -------- Make a new Download object that will download a file from the Web --------

	/**
	 * Make a new Download object that will download a file from the Web.
	 * The new Download object will start out paused, call get() on it to have it start downloading.
	 * 
	 * @param web    A link to the program's Web object
	 * @param url    The URL to download, must start "http://"
	 * @param folder The path to the folder where you want the file saved
	 */
	public WebDownload(Web web, Url url, Path folder, Update update) {

		// Save the given objects
		this.web = web;
		this.url = url;
		this.folder = folder;
		this.update = update;
		
		// Add this new Download object to the program's list of all of them
		web.list.add(this);
	}
	
	/** A link to the program's Web object. */
	private Web web;

	/** The "http://" URL this Download object is downloading. */
	private Url url;
	/** The IP address of the Web server, once we look it up using DNS. */
	private Ip ip;

	/** true once the user gives us permission to get started. */
	private boolean permission;
	/** The Dns object we are using to find out the Web server's IP address. */
	private DomainTask dns;
	/** The Get object we are using to request the file from the Web server. */
	private OldGet get;
	
	private Update update;

	// -------- Tell this Download object to do something --------

	/** Request the file from the Web server. */
	public void get() {
		
		// Don't do anything if we're closed
		if (state().isClosed()) return;

		// Record our permission to make a request, and call pulse() to do it
		permission = true;
		pulse();
	}
	
	// -------- The file on the disk --------
	
	/** The path to the folder we'll make a file in. */
	private Path folder;

	/** A Save object that has a temporary file we fill, and then saves it to an available name. */
	private Save save;
	
	/** The Path where we saved our done file, or null if we haven't downloaded it all yet. */
	public Path saved() {
		if (save == null) return null; // We don't even have our Save object yet
		return save.saved; // If our Save object hasn't saved its file, saved will be null
	}

	// -------- Periodically pulse this object to move things forward --------
	
	private Update hereup = new Update(new MyReceive());
	private class MyReceive implements Receive {
		public void receive() {
			
			pulse();
		}
	}
	
	/**
	 * See what's happened and move things to the next step.
	 * The program periodically calls pulse() on this object.
	 */
	public void pulse() {
		
		//TODO assume we'll change, horrid, but ok for now
		update.send();

		// If we're closed, there is nothing more for a pulse to do, don't let one change us
		if (state().isClosed()) return;
		
		try {
			
			// We have permission to get started
			if (permission) {
				if (ip == null) {                                                     // We don't know the Web server's IP address yet
					if (dns == null) dns = new DomainTask(hereup, url.site);        // If we don't have our Dns object yet, make it
				} else {                                                              // We know the Web server's IP address
					if (save == null) save = new Save(folder);                        // If we don't have our temporary file yet, open it
					if (get == null) get = new OldGet(web.internet, url, ip, save.file); // If we don't have our Get request yet, make it
				}
			}
			
			// We have a Dns object trying to figure out the IP address
			if (dns != null && dns.closed()) {
				try {
					ip = dns.result();
					dns = null;
				} catch (Exception e) { throw new OldState(OldState.couldNot, "Server Not Found"); }
			}

			// We have a Get object downloading data from the Web server
			if (get != null) {
				
				// Pulse it and grab information from it
				get.pulse();
				if (get.response != null && response == null) response = get.response; // Grab response headers
				attempt = OldTime.recent(attempt, get.attemptTime()); // Get the time we attempted to connect to the Web server
				
				// Our Get object is closed
				if (get.state().isClosed()) {
					
					// Get its closed outcome state to find out how it closed
					OldState s = get.state();
					
					/*
					 * Here are the possible states our Get and this Download can be closed by:
					 * 
					 * Get              Download
					 * ---------------  ---------------
					 * closed           closed
					 * completed        completed
					 * couldNot         couldNot   
					 * socketException
					 * fileException
					 * 
					 * pulse doesn't need to look for closed because the program sets this outcome.
					 * completed means our Get finished downloading the file, we need to rename it and we're done too.
					 * couldNot, socketException, and fileException in the Get all lead to couldNot in this Download.
					 */
					
					// If our Get closed because of a socket or file exception, close this Download as couldNot with a user message about sockets or files
					if (s.state == OldState.socketException) throw new OldState(OldState.couldNot, "Cannot Connect");
					if (s.state == OldState.fileException)   throw new OldState(OldState.couldNot, "Cannot Save");
					
					// If our Get finished downloading the file, rename it to the saved to location
					if (s.state == OldState.completed) save.save(new PathName(name()), false); // false to not open the file for uploading
					
					// Close us with the same State outcome that closed our Get object
					throw s;
				}
			}

		// One of the methods we called realized we are done, close this Download object with the reason it found
		} catch (OldState state) {
			close(state);
		
		// Any file problem closes this Download, showing "Cannot Save" to the user
		} catch (IOException e) {
			close(new OldState(OldState.couldNot, "Cannot Save"));
		}
	}

	// -------- Get this Download object's current state, and close it --------

	/**
	 * Find out what this Download object's current state is.
	 * 
	 * Pending states:
	 * 
	 * paused        This Download is waiting for the program or the user to tell it to request the file from the Web server.
	 * 
	 * Active operations:
	 * 
	 * doing         This Download has an open socket connection and is downloading the file through it.
	 * 
	 * Closed outcomes:
	 * 
	 * cancelled     This Download stopped network activity and deleted its temporary file because the program doesn't need it anymore.
	 * completed     This Download finished downloading the whole file, renamed it to the saved to location, and stopped network activity.
	 * couldNot      This Download had to give up.
	 * 
	 * @return A State object that describes our state right now
	 */
	public OldState state() {

		// If we're closed, that's our final state, return it
		if (closed != null) return closed;

		// We're paused until the user gives us permission to start, then we're doing
		if (permission) return OldState.doing();
		else return OldState.paused();
	}

	/**
	 * Stop all network activity, delete our temporary file, and mark this Download object as closed.
	 * 
	 * @param closed The State to set as our closed outcome
	 */
	public void close(OldState closed) {
		
		// Only let us close once, and save the given final closed state
		if (state().isClosed()) return;
		this.closed = closed;
		
		// Close all our objects and resources
		if (save != null) save.close();                 // Close and delete our temporary file
		if (get  != null) get.close(OldState.cancelled()); // Cancel our Get object, making it disconnect its TCP socket connection
		if (dns  != null) dns.close(); // Cancel our Dns object, making it stop DNS Internet communications
		
		// Remove us from the program's list of Download objects
		web.list.remove(this);
	}

	/** Our final state that tells how and why we closed, or null if we're not closed yet. */
	private OldState closed;
	
	// -------- Progress and status information --------

	/** Compose the file name of the file we're downloading to display to the user in the program. */
	public Name name() {
		
		// Start with the file name written in the "Content-Disposition: filename" response header
		Name name = new Name();
		if (response != null) name = response.name();
		
		// If not found, compose the file name from the URL
		if (name.isBlank()) name = url.name();
		
		// If the Web server gave us a "Content-Type" header, set the file name extension from it
		if (response != null && Text.hasText(response.extension()))
			name = new Name(name.name, response.extension());
		
		// Make the extension lower case and return the name
		return name.lower();
	}

	/** The first group of HTTP response headers the Web server sent us. */
	private HeaderGroup response;

	/** The number of connections we currently have open and are trying to download the file through. */
	public int connections() {
		if (get != null) return 1; // It's 1 if we have a Get, 0 if we don't
		else return 0;
	}
	
	/** Our total download speed, in bytes/second. */
	public int speed() {
		if (get != null) return get.speed(); // Ask our Get, if we have one
		else return 0; // No Get, we're moving forward at 0 bytes/second
	}

	/**
	 * The Time when we last bothered the Web server.
	 * This is when we most recently made a connection attempt.
	 * Use this to keep from bothering the Web server too frequently.
	 */
	public OldTime attemptTime() { return attempt; }
	private OldTime attempt; // pulse() keeps this up to date

	/**
	 * The Time we last heard from the Web server.
	 * This is when any of our connections most recently connected or downloaded something.
	 * Use this to notice when our connections to the Web server have died.
	 */
	public OldTime responseTime() {
		if (get != null) return get.responseTime();
		else return new OldTime(); // No Get, return an unset Time
	}
	
	/** How big the file we're downloading is, in bytes, or -1 if we don't know. */
	public long size() {

		// Read the "Content-Length" header from the HTTP response headers the Web server sent us first
		long size = -1;
		if (response != null) size = response.number(Header.contentLength);
		
		// If that didn't work, and we saved the whole file, get its size
		if (size == -1 && state().state == OldState.completed && get != null) size = get.saved;
		return size;
	}

	/** How many bytes of the file we've saved to disk, 0 if we don't have any yet. */
	public long sizeSaved() {
		if (get != null) return get.saved; // Use our Get's file index
		else return 0;
	}
	
	/**
	 * The number of seconds we predict it will take to get the rest of a file.
	 * 
	 * @param speed Our current speed, in bytes/second, 0 if stopped or unknown.
	 * @return      The predicted arrival time in seconds, 1 or more seconds.
	 *              -1 if we can't predict.
	 */
	private int arrival(int speed) {
		arrival = OldDescribe.arrival(sizeSaved(), size(), speed, arrival); // Give and save our previous prediction
		return arrival;
	}
	/** The arrival time, in seconds, that we most recently predicted. */
	private int arrival;

	// -------- Progress and status text for the user --------

	/** Compose text for the user that describes the current status of this Download, like "Getting 2.34 KB/s" or "Not Found". */
	public String describeStatus() {
		
		// Get our current state, speed, and predicted arrival time
		OldState state = state();
		int speed = speed();
		int arrival = arrival(speed);

		// Pending
		String s = ""; // If we're pending, we'll return s blank
		
		// Getting
		if (state.state == OldState.doing) {

			// We're stuck waiting for a Web server that won't respond, say "No response for 5 sec"
			if (responseTime().expired(OldDescribe.wait * Time.second, false)) { // If responseTime() was never set, return false
				s = "No response for " + OldDescribe.timeMillisecondsCoarse(responseTime().expired());
				
			// We know our speed and can predict our arrival time, say "12 sec at 2.34 KB/s to get"
			} else if (speed != 0 && arrival != -1) {
				s = OldDescribe.timeSeconds(arrival) + " at " + OldDescribe.speed(speed) + " to get";

			// We know our speed, but can't predict our arrival time, say "Getting 2.34 KB/s"
			} else if (speed != 0) {
				s = "Getting " + OldDescribe.speed(speed);

			// We don't know our speed, but we're not stuck yet, just say "Getting"
			} else {
				s = "Getting";
			}

		// Done
		} else if (state.state == OldState.completed) {
			s = "Done";

		// Cannot
		} else if (state.state == OldState.couldNot) {
			
			// Get the text from the closed State
			s = state.user; // Like "Not Found" from the response headers or "Cannot Save" if our Get got a file exception
			if (Text.isBlank(s)) s = "Cannot"; // Don't let s be blank, as that would look like we're paused
		}

		// Return the text we composed
		return s;
	}

	/** Compose text for the user that describes the size and progress of this Download, like "12% 145 KB/1,154 KB". */
	public String describeSize() {
		return OldDescribe.sizePercent(sizeSaved(), size());
	}
}
