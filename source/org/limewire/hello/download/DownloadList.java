package org.limewire.hello.download;

import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.web.Url;

/** The program's list of web downloads. */
public class DownloadList {

	/** The list of downloads. */
	private List<Download> downloads;
	
	/** Make the program's list of downloads. */
	public DownloadList() {
		downloads = new ArrayList<Download>(); // Make a new empty list
	}
	
	/**
	 * Add a new download to this list.
	 * @param url The download's web address
	 * @return    A new Download object we made and added to this list, or null if we already have url
	 */
	public Download add(Url url) {
		
		// Check to see if we already have a Download with the given URL
		for (Download d : downloads)
			if (d.url.equals(url))
				return null; // Already got it

		// It's unique, add it
		Download download = new Download(this, url); // Make a new Download object
		downloads.add(download);                     // Add it to our list
		return download;                             // Return it
	}
	
	/** Remove the given Download from this list. */
	public void remove(Download download) {
		downloads.remove(download);
	}
}
