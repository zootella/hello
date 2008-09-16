package org.limewire.hello.all;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.internet.Ip;
import org.limewire.hello.base.state.old.OldClose;
import org.limewire.hello.base.web.Url;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Snippet {

	// -------- Snippet --------
	
	
	
	public static void snippet() {
		
		
		
		ByteBuffer buffer = ByteBuffer.allocate(5);
		
		buffer.position(2);
		buffer.limit(2);
		
		
		
		

		
		
		
		/*
		try {
			URI uri = new URI("");
			
			String s = uri.getAuthority(); // "www.site.com"
			s = uri.getScheme(); // "http"
			
			Main.report(s);
			
			
			
		} catch (URISyntaxException e) {
		}
		
		
		
		
		
		
		/*

		URL feedUrl;
		try {
			
			feedUrl = new URL("http://www.1up.com/flat/Podcasts/vodcasts.xml");
			
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl)); // this blocks
			
			
			Main.report("Title: " + feed.getTitle());
			Main.report("Description: " + feed.getDescription());
			
			List<SyndEntry> entries = feed.getEntries();
			Main.report("Episodes: " + entries.size());
			for (SyndEntry entry : entries) {
				
				Main.report("");
				Main.report("    Title: " + entry.getTitle());
				Main.report("    Date: "  + entry.getPublishedDate().toString());
				Main.report("    Description: " + entry.getDescription().getValue());
				Main.report("    Link: " + entry.getLink());
			}
			
			
			
			
			
		} catch (MalformedURLException e) {
			Main.report("malformed url exception");
		} catch (FeedException e) {
			Main.report("feed exception");
		} catch (IOException e) {
			Main.report("io exception");
		}
		
		*/
		
		
		
		

		
		
		
		
		
		

		

	}
	
	
	
	
	
	
}
