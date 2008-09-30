package org.limewire.hello.feed;

import java.util.LinkedHashMap;
import java.util.Map;

import org.limewire.hello.base.state.Model;

import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.synd.SyndEntry;

/** A single podcast episode listed in a Feed. */
public class Episode {

	// -------- Episode --------
	
	/**
	 * Make a new Episode object to represent a podcast episode
	 * @param feed  The Feed this Episode is a part of
	 * @param entry The SyndEntry object rome.jar parsed for this episode
	 */
	public Episode(Feed feed, SyndEntry entry) {
		
		// Save the given links
		this.feed = feed;
		this.entry = entry;
		
		// Make our Model to communicate with views above
		model = new MyModel();
		
		// Have the Rome iTunes module parse the SyndEntry object for the iTunes-specific tags
		info = (EntryInformation)entry.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd"); // Module URI must be lowercase
	}
	
	/** The Feed this Episode is a part of. */
	private Feed feed;
	/** The SyndEntry Rome parsed for this Episode. */
	private SyndEntry entry;
	/** The object Rome's iTunes module produced from entry. */
	private EntryInformation info;

	/** Put away screen, net, and disk resources. */
	public void close() {
		model.close(); // Tell all the View objects looking at us to vanish
	}

	// -------- Methods --------
	
	public void get() {}
	public void pause() {}
	public void reset() {}
	public void open() {}
	public void openSavedFile() {}
	public void openContainingFolder() {}

	// -------- Model --------

	/** This Episode object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model { // Remember to call model.close()
		
		/** The download status of this Episode. */
		public String status() {
			return "(status)";
		}

		/** Episode title. */
		public String episode() {
			if (entry.getTitle() != null)
				return entry.getTitle();
			else
				return "";
		}

		/** Episode iTunes duration time. */
		public String time() {
			if (info.getDuration() != null)
				return info.getDuration().toString();
			else
				return "";
		}

		/** Episode release date. */
		public String date() {
			if (entry.getPublishedDate() != null)
				return entry.getPublishedDate().toString();
			else
				return "";
		}

		/** Episode iTunes subtitle description. */
		public String description() {
			if (info.getSubtitle() != null)
				return info.getSubtitle();
			else
				return "";
		}

		/** Episode file download web address. */
		public String address() {
			if (entry.getLink() != null)
				return entry.getLink();
			else
				return "";
		}

		/** Compose text about the current state of this Episode object to show the user. */
		public Map<String, String> view() {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("Status",      status());
			map.put("Episode",     episode());
			map.put("Time",        time());
			map.put("Date",        date());
			map.put("Description", description());
			map.put("Address",     address());
			return map;
		}
		
		/** The Episode object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer Episode object. */
	private Episode me() { return this; }
}
