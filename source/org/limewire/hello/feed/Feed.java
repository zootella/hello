package org.limewire.hello.feed;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.limewire.hello.base.time.Once;
import org.limewire.hello.base.time.Update;
import org.limewire.hello.base.time.UpdateReceive;
import org.limewire.hello.base.user.Model;
import org.limewire.hello.base.web.GetFeed;
import org.limewire.hello.base.web.Url;

import com.sun.syndication.feed.synd.SyndEntry;

/** A RSS feed the program is subscribed to. */
public class Feed {

	// -------- Feed --------

	/**
	 * Make a new Feed object to represent an RSS feed.
	 * @param list A link to the program's FeedList, it will add us so we don't have to
	 * @param url  The URL to the RSS XML on the web we'll download and parse
	 */
	public Feed(FeedList list, Url url) {
		
		// Save links
		this.list = list; // Save the given link to the program's FeedList
		this.url = url;   // Save our URL
		
		// Empty list
		episodes = new ArrayList<Episode>(); // Make the empty episodes list
		
		// Update objects
		model = new MyModel();                      // Make our inner Model object to tells views above when we've changed
		update = new Update(new MyUpdateReceive()); // Make our inner Update object to find out when objects beneath have changed
		once = new Once();                          // Make a Once object to only do something one time
		
		// Tasks
		get = new GetFeed(update, url.toString()); // Make a GetFeed to download and parse the feed
	}
	
	/** Close disk and net resources and disconnect from other objects. */
	public void close() {
		model.close();                   // Tell all the View objects looking at us to vanish
		list.remove(this);               // Remove the Feed from the program's list of them
		for (Episode episode : episodes) // Close all our episodes
			episode.close();
		update.close();                  // Close our internal objects so they free their resources
	}

	/**
	 * A link to the program's list of Feed objects.
	 * We don't have to add ourselves to it, but are responsible for removing ourselves from it.
	 */
	private FeedList list;
	
	/** The web address of the XML RSS feed we download and parse. */
	public final Url url;
	
	/**
	 * A list of Episode objects we parsed from the feed.
	 * Empty before we download and parse the feed.
	 */
	public final List<Episode> episodes;
	
	/** Our Update which objects below use to tell us when they've finished things we've asked them to do. */
	public Update update;
	/** Only look at a parsed feed once. */
	private Once once;
	
	/** A GetFeed object downloads an RSS feed from the web and parses the XML. */
	private final GetFeed get;
	
	// -------- Methods --------
	
	/** Refresh this feed, getting fresh XML and downloading the most recent episodes. */
	public void refresh() {

		//TODO
	}
	
	// when you parse this again, the object needs a way to reset itself
	// it also needs a way to tell its views that its episode contents have changed
	// now, the tab only updates when the selection chagnes, not when the feed finishes parsing
	
	// -------- Update --------

	// When a worker object we gave our Update has progressed or completed, it calls this receive() method
	private class MyUpdateReceive implements UpdateReceive {
		public void receive() {
			
			// Our GetFeed object just finished
			if (get.state().isClosed() && once.once()) { // Only enter here once

				// It parsed something
				if (get.feed != null) {
					
					// Make an Entry in our list for each SyndEntry it found when parsing
					List<SyndEntry> entries = get.feed.getEntries();
					for (SyndEntry entry : entries)
						episodes.add(new Episode(me(), entry));
				}

				// Tell our Model we've changed
				model.update();
			}
		}
	}
	
	// -------- Model --------

	/** This Feed object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model { // Remember to call model.close()
		
		/** The Feed object that made and contains this Model. */
		public Object out() { return me(); }

		/** Compose text about the current state of this Feed object to show the user. */
		public Map<String, String> view() {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("Status",      status());
			map.put("Name",        name());
			map.put("Description", description());
			map.put("Address",     address());
			return map;
		}
		
		/** Status text. */
		public String status() {
			if (get.feed != null) return "Parsed";
			else if (!get.state().isClosed()) return "Getting";
			else return "Cannot";
		}

		/** Podcast title. */
		public String name() {
			if (get.feed == null) return "";
			else return get.feed.getTitle();
		}

		/** Podcast description. */
		public String description() {
			if (get.feed == null) return "";
			else return get.feed.getDescription();
		}

		/** Web address to the RSS feed. */
		public String address() {
			return url.toString();
		}
	}
	
	/** Give inner classes a link to this outer Feed object. */
	private Feed me() { return this; }
}
