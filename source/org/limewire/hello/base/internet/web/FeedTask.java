package org.limewire.hello.base.internet.web;

import java.net.URL;

import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.web.Url;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedTask extends TaskClose {
	
	// Make

	/** Download and parse the RSS feed at url. */
	public FeedTask(Update update, Url url) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.url = url;
		
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}

	/** The web address of the RSS feed. */
	public final Url url;

	// Result
	
	/** The SyndFeed we downloaded and parsed, or throws the exception that made this give up. */
	public SyndFeed result() throws Exception { return (SyndFeed)check(feed); }
	private SyndFeed feed;

	// Inside
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private SyndFeed taskFeed; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {
				
			// Download and parse the RSS feed
			URL u = new URL(url.toString());
			SyndFeedInput input = new SyndFeedInput();
			taskFeed = input.build(new XmlReader(u));
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				feed = taskFeed;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
