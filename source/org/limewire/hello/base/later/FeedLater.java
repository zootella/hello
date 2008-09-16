package org.limewire.hello.base.later;

import java.net.URL;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.web.Url;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedLater extends Later {
	
	// Make

	/** Download and parse the RSS feed at url. */
	public FeedLater(Update above, Url url) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.url = url;
		
		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}

	/** The web address of the RSS feed. */
	public final Url url;

	// Result
	
	/** The SyndFeed we downloaded and parsed, or throws the exception that made this give up. */
	public SyndFeed result() throws Exception { return (SyndFeed)check(feed); }
	private SyndFeed feed;

	// Inside
	
	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private SyndFeed workFeed;

		// A worker thread will call this method
		public Void doInBackground() {
			try {
				
				// Download and parse the RSS feed
				URL u = new URL(url.toString());
				SyndFeedInput input = new SyndFeedInput();
				workFeed = input.build(new XmlReader(u));

			} catch (Exception e) { workException = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception == null) { // No exception, save what worker did
				
				feed = workFeed;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
