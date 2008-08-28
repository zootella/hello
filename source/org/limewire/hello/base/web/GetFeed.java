package org.limewire.hello.base.web;

import java.net.URL;

import org.jdesktop.swingworker.SwingWorker;

import org.limewire.hello.base.state.State;
import org.limewire.hello.base.time.Update;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class GetFeed {

	// -------- Make a new GetFeed object to download and parse an XML feed --------
	
	/** The Update object above we'll tell when we've changed. */
	private final Update update;
	
	/** The feed web address this Feed object is downloading and parsing. */
	public final String address; // final to keep this reference from changing while the SwingWorker thread reads it
	
	/** The objects the feed downloaded and parsed into. */
	public SyndFeed feed;

	/**
	 * Make a new GetFeed object to download and parse an XML feed.
	 * @param update  An Update object above that we'll tell when we've changed
	 * @param address The web address of the XML feed
	 */
	public GetFeed(Update update, String address) {
		
		// Save the given objects
		this.update = update;
		this.address = address;

		// Make and start our SwingWorker, which will do the DNS lookup in a worker thread
		worker = new MySwingWorker();
		worker.execute(); // Have Java call doInBackground() now
	}
	
	// -------- Our SwingWorker --------

	// A GetFeed object contains a MySwingWorker object named worker
	private MySwingWorker worker;
	
	// The MySwingWorker class is defined right here
	private class MySwingWorker extends SwingWorker<SyndFeed, Void> { // Returns a SyndFeed object, and doesn't report progress

		// Java will have a SwingWorker thread call this method
		public SyndFeed doInBackground() throws Exception {

			// Have the Rome library download and parse the XML feed
			URL url = new URL(address); // This could block, too
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(url));
			return feed;
		}

		// Once doInBackground() returns, the normal Swing thread calls this done() method
		public void done() {
			
			// Don't let anything change if we're already closed
			if (state().isClosed()) return;

			// Get the answer doInBackground() returned
			feed = null;
			Exception exception = null;
			try {
				feed = get();
			} catch (Exception e) {
				exception = e;
			}

			// Mark our GetFeed object as closed
			if (exception != null) close(State.socketException(exception));
			else if (feed == null) close(State.couldNot());
			else                   close(State.completed());

			// Tell the Update object above us that we've changed
			update.send();
		}
	}
	
	// -------- Get this object's current state, and close it --------

	/**
	 * Find out what this GetFeed object's current state is.
	 * 
	 * Active operations:
	 * 
	 * doing      This GetFeed object is downloading the XML and parsing it.
	 * 
	 * Closed outcomes:
	 * 
	 * cancelled  The program closed this object because it didn't need it anymore.
	 * completed  This object got and parsed the feed, finishing successfully.
	 * couldNot   We didn't get an exception, but also didn't get the feed, this object gave up.
	 * 
	 * @return A State object that describes our state right now
	 */
	public State state() {
		if (closed != null) return closed; // If we're closed, that's our state, return it
		return State.doing(); // Otherwise we must still be getting the feed
	}

	/**
	 * Mark this object as closed, and make it stop communications on the Internet.
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
