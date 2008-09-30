package org.limewire.hello.download;

import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.web.Url;

// this is not the list of all the download objects in the program
// a little part of the program can still make a download off by itself
// rather, this is the list behidn the table on the downloads tab

/** The program's list of web downloads. */
public class DownloadList extends Close {

	/** The list of downloads. */
	private List<Download> downloads;
	
	/** Make the program's list of downloads. */
	public DownloadList() {
		downloads = new ArrayList<Download>(); // Make a new empty list

		/*
		// Restore the download list the program saved in Store.txt when it last closed
		try {
			for (Outline o : store.outline.o("download").list()) {
				try {
					new Download(this, o);
				} catch (MessageException e) {} // Outline o didn't parse into a DownloadRow, try the next one
			}
			store.outline.o("download").remove(""); // Remove the download list from the Outline
		} catch (MessageException e) {} // No download list, keep going
		*/
	}
	
	public void close() {
		if (already()) return;

		/*
		// Store the download list for the next time the program runs
		if (!table.rows.isEmpty()) { // Only do something if we have a download
			Outline o = store.outline.m("download"); // Make "download", which has the settings and the list
			for (Row row : table.rows) // Loop through all the rows in the Table
				o.add(((Download)row.model.out()).toOutline()); // Have the DownloadRow behind it compose an Outline
		}
		*/

	}
	
	
	
	/**
	 * Given address text from the user, add a new download to the list.
	 * @return The Download we made and added, null if invalid or duplicate
	 */
	public Download add(String s) {

		// Parse the given text from the user into a Url object
		Url url = null;
		try {
			url = new Url(s);
		} catch (MessageException e) { return null; } // Not a valid address
		
		// Check to see if we already have a Download with that Url
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
