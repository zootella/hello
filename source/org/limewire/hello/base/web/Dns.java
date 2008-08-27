package org.limewire.hello.base.web;


import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingWorker;

import org.limewire.hello.all.Main;
import org.limewire.hello.base.internet.Ip;
import org.limewire.hello.base.state.State;

// make a Dns object to resolve a host name to its IP address
// make the object, then check it to see when it's done
public class Dns {

	// -------- Make a new Dns object, and get the IP address it found --------
	
	/** The site name, like "www.site.com", this Dns object is looking up. */
	public final String site; // final to keep this reference from changing while the SwingWorker thread reads it
	
	/** The IP address the site name resolved to. */
	public Ip ip;

	/** Make a new Dns object to look up a site name like "www.site.com". */
	public Dns(String site) {
		
		// Save the given site name
		this.site = site;

		// Make and start our SwingWorker, which will do the DNS lookup in a worker thread
		worker = new MySwingWorker();
		worker.execute(); // Have Java call doInBackground() now
	}
	
	// -------- Our SwingWorker --------

	// A Dns object contains a MySwingWorker object named worker
	private MySwingWorker worker;
	
	// The MySwingWorker class is defined right here
	private class MySwingWorker extends SwingWorker<Ip, Void> { // Returns an Ip object, and doesn't report progress

		// Java will have a SwingWorker thread call this method
		public Ip doInBackground() throws Exception {
			
			// Communicate with the Domain Name System on the Internet to resolve the site name to its IP address
			InetAddress a = null;
			try {
				a = InetAddress.getByName(site); // This call might hold the SwingWorker thread here a long time
			} catch (UnknownHostException e) {   // Site name not found
				return null;                     // Return null to indicate what happened, leads to State.couldNot below
			}
			return new Ip(a);                    // We got the IP address, return it
		}

		// Once doInBackground() returns, the normal Swing thread calls this done() method
		public void done() {
			
			// Don't let anything change if we're already closed
			if (state().isClosed()) return;

			// Get the answer doInBackground() returned
			try { ip = get(); } catch (Exception e) {}

			// Mark our Dns object as closed
			if (ip == null) close(State.couldNot());  // We were unable to resolve the site name into an IP address
			else            close(State.completed()); // We found the site name and the IP address
			
			// Have the next pulse happen soon so the object that made us will know to notice we've changed
			Main.soon();
		}
	}
	
	// -------- Get this object's current state, and close it --------

	/**
	 * Find out what this Dns object's current state is.
	 * 
	 * Active operations:
	 * 
	 * doing      This Dns object is using DNS on the Internet to resolve the site name into an IP address.
	 * 
	 * Closed outcomes:
	 * 
	 * cancelled  The program closed this Dns object because it didn't need it anymore.
	 * completed  This Dns object got the IP address, finishing successfully.
	 * couldNot   The site name isn't registered, or our computer can't access DNS or the Internet, this Dns object gave up.
	 * 
	 * @return A State object that describes our state right now
	 */
	public State state() {

		// If we're closed, that's our state, return it
		if (closed != null) return closed;
		
		// Otherwise we must still be doing the DNS lookup
		return new State(State.doing);
	}

	/**
	 * Mark this Dns object as closed, and make it stop DNS communications on the Internet.
	 * 
	 * @param closed A State object that tells how and why we closed
	 */
	public void close(State closed) {
		
		// Only let us close once, and save the given final closed state
		if (state().isClosed()) return;
		this.closed = closed;

		// Cancel our SwingWorker
		worker.cancel(true); // true to interrupt its thread
	}

	/** Our final state that tells how and why we closed, or null if we're not closed yet. */
	private State closed;
}
