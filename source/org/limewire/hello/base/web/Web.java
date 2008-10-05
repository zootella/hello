package org.limewire.hello.base.web;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.internet.old.OldInternet;
import org.limewire.hello.base.state.Update;

public class Web {
	
	//TODO very bad, just for demo
	public static Web web;

	// -------- Make and pulse the program's Web object --------
	
	/**
	 * Make the program's Web object.
	 * It keeps a link to the Internet object, and a list of all the Download objects.
	 * 
	 * @param internet A link to the program's Internet object
	 */
	public Web(OldInternet internet) {
		web = this;
		this.internet = internet;          // Save the link to the program's Internet object
		list = new LinkedList<WebDownload>(); // Make a new empty List to hold Download objects
	}
	
	/** A link to the program's Internet object. */
	public OldInternet internet;
	
	/** The program's List of all the Download objects. */
	public List<WebDownload> list;

	/** Pulse all the Download objects the program has made. */
	public void pulse() {
		for (WebDownload download : new ArrayList<WebDownload>(list)) download.pulse(); // Copy the list so a Download object can remove itself
	}
	
	// -------- Download a file from the Web and save it in a folder on the disk --------

	/**
	 * Make a new Download object that will download a file from the Web.
	 * The new Download object will start out paused, call get() on it to have it start downloading.
	 * 
	 * @param url    The URL to download, must start "http://"
	 * @param folder The path to the folder where you want the file saved
	 * @return       A new paused Download object
	 */
	public WebDownload download(Url url, Path folder, Update update) {
		return new WebDownload(this, url, folder, update); // Make and return a new Download object
	}
}
