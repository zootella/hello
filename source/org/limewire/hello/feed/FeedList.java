package org.limewire.hello.feed;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.web.Url;

/** The program's list of RSS feeds that we're subscribed to. */
public class FeedList {

	/** The list of feeds. */
	private List<Feed> feeds;
	
	/** Make the program's list of RSS feed subscriptions. */
	public FeedList() {
		feeds = new ArrayList<Feed>(); // Make a new empty list
	}
	
	/**
	 * Add a new feed to this list.
	 * @param url The feed's web address
	 * @return    A new Feed object we made and added to this list, or null if we already have url
	 */
	public Feed add(Url url) {
		
		// Check to see if we already have a Feed with the given URL
		for (Feed f : feeds)
			if (f.url.equals(url))
				return null; // Already got it

		// It's unique, add it
		Feed feed = new Feed(this, url); // Make a new Feed object
		feeds.add(feed);                 // Add it to our list
		return feed;                     // Return it
	}
	
	/** Remove the given feed from this list. */
	public void remove(Feed feed) {
		feeds.remove(feed);
	}

	/** Refresh the list of feeds, get fresh XML and download recent episodes. */
	public void refresh() {

		//TODO
	}
}
