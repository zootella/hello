package org.limewire.hello.download;

import java.util.LinkedHashMap;
import java.util.Map;

import org.limewire.hello.base.desktop.Open;
import org.limewire.hello.base.file.Here;
import org.limewire.hello.base.file.Name;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;
import org.limewire.hello.base.state.old.OldState;
import org.limewire.hello.base.time.OldTime;
import org.limewire.hello.base.user.OldDescribe;
import org.limewire.hello.base.web.Url;
import org.limewire.hello.base.web.Web;
import org.limewire.hello.base.web.WebDownload;

public class Download extends Close {

	// -------- Store to and restore from an Outline in Store.txt --------

	/** Make an Outline we can restore this DownloadRow object from the next time the program runs. *
	public Outline toOutline() {
		Outline o = new Outline("", url.toString()); // No name, value is the address
		if (name != null) o.add("name", name.toString()); // Add contents
		if (saved != null) o.add("saved", saved.toString());
		if (size != 0) o.add("size", size);
		if (cannot != null) o.add("cannot", cannot);
		return o;
	}
	*/

	/**
	 * Make a new DownloadRow object listed on the DownloadTab from an Outline.
	 * 
	 * @param tab A link up to the program's DownloadTab
	 * @param o   An Outline from Store.txt
	 *
	public Download(DownloadList list, Outline o) throws MessageException {

		// Save the link to the DownloadTab
		this.list = list;

		// Set values from the Outline
		url = new Url(o.getString());
		if (o.has("name")) name = new Name(o.o("name").getString());
		if (o.has("saved")) saved = new Path(o.o("saved").getString());
		if (o.has("size")) size = o.o("size").getNumber();
		if (o.has("cannot")) cannot = o.o("cannot").getString();

		// Make a row for us in the Table
		row = describe();
		tab.table.add(row);
	}
	*/

	// -------- Make a new DownloadRow object, inside parts, and state --------

	/**
	 * Make a new DownloadRow object listed on the DownloadTab from a Url the user entered.
	 * 
	 * @param tab A link to the program's list of downloads
	 * @param url The URL to download
	 */
	public Download(DownloadList list, Url url) {
		
		// Save the link to the DownloadTab
		this.list = list;

		// Save the URL we're going to try to download
		this.url = url;
		
		// Make our inner Model object to tells views above when we've changed
		model = new MyModel();
		
		update = new Update(new MyReceive());
	}
	
	private Update update;
	
	/** A link to the object that is the program's list of downloads. */
	private DownloadList list;
	
	/** The Download object that is performing the download for us. */
	private WebDownload download;

	/** The URL we're downloading. */
	public Url url;
	/** The name of the file we're downloading, from the url or the HTTP headers the Web server sent us. */
	private Name name;
	/** When the file is all downloaded, saved is the path we saved it to for the user on the disk. */
	private Path saved;
	/** The size of the file we saved. */
	private long size;
	/** If we can't download the file and have to give up, short text for the user like "Not Found" that describes why. */
	private String cannot;

	/**
	 * Find out what State this DownloadRow is in right now.
	 * 
	 * The possible states are:
	 * 
	 * paused     Pending, waiting for the user or the program to tell it to start getting.
	 * doing      Getting, opening connections to the Web server and downloading the file through them.
	 * completed  Done, finished downloading and saving its file, and is done.
	 * couldNot   Cannot, had to give up, for instance, the Web server said 404 Not Found.
	 * 
	 * @return A State object that tells what state this DownloadRow object is in.
	 */
	public OldState state() {
		
		// Figure out our state by looking at which internal objects we have
		if      (download != null) return OldState.doing();     // Getting
		else if (saved    != null) return OldState.completed(); // Done
		else if (cannot   != null) return OldState.couldNot();  // Cannot
		else                       return OldState.paused();    // Pending
	}

	// -------- The methods behind the items on the right-click menu --------
	
	/** Determine if we can Get right now. */
	public boolean canGet() {
		return state().state == OldState.paused; // Return true if we're pending
	}

	/** Have this DownloadRow open a new TCP socket connection to the Web server to try to download its file. */
	public void get() {
		
		// Make sure we can get
		if (!canGet()) return;
		
		// If we don't have a Download object yet, make it now
		if (download == null) download = Web.web.download(url, Here.folder(), update);
		//TODO don't use here folder

		// Give our new Download object permission to make a single request to the Web server
		download.get();
		
		// Update our row in the Table
		model.changed();
	}
	
	public void pause() {
		
	}

	/** Make this DownloadRow like we removed it and then added its URL back into the list. */
	public void reset() {
		
		// Discard our Download object if we have one
		if (download != null) {
			download.close(OldState.cancelled()); // Have it close connections and delete its temporary file
			download = null; // Release it for garbage collection
		}

		// Forget everything about the progress of our download
		saved = null;
		size = 0;
		name = null;
		cannot = null;

		// Update the views looking at us
		model.changed();
	}

	/** Remove this DownloadRow from the Table and from the program. */
	public void remove() {
		close();
	}
	public void close() {
		if (already()) return;
		reset(); // Discards our Download object, closing connections and deleting our temporary file
		model.close(); // Close the views looking at us
	}

	/** Determine if we saved our file, and it's still there on the disk for us to open. */
	public boolean canOpenSavedFile() {
		return
			saved != null && // Only return true if we saved a file, and
			saved.exists();  // The file is still there on the disk
	}

	/** Open our saved file, or if we can't, open our URL. */
	public void open() {
		if (canOpenSavedFile()) openSavedFile();
		else openUrl();
	}

	/** Open our URL. */
	public void openUrl() {
		Open.url(url);
	}

	/** Open our saved file. */
	public void openSavedFile() {
		if (canOpenSavedFile()) Open.file(saved);
	}

	/** Open the folder we saved our file in. */
	public void openContainingFolder() {
		if (canOpenSavedFile()) Open.file(saved.up());
	}

	
	
	
	
	
	// -------- Update --------

	// When a worker object we gave our Update has progressed or completed, it calls this receive() method
	private class MyReceive implements Receive {
		public void receive() {

			// We only need to look for changes if we have a Download object
			if (download == null) return;
			
			// It's finished changing
			if (download.state().isClosed()) {
				
				/*
				 * Here are the possible states our Download can be closed by:
				 * 
				 * closed
				 * completed
				 * couldNot   
				 * 
				 * We don't need to look for closed because we set that outcome.
				 */
				
				// If finished downloading the file successfully
				if (download.state().state == OldState.completed) {
					
					// Get information about the file it saved
					saved = download.saved();
					size = download.sizeSaved();
					name = download.name();
					
					// It had to give up
				} else if (download.state().state == OldState.couldNot) {
					
					// Save the explination why
					cannot = download.describeStatus();
				}
				
				// We took all the information we needed, release the Download for garbage collection
				download = null;
				
				// Update our row in the Table and any other views looking at our Model
				model.changed();
				
			// It's still downloading
			} else {
				
				// Update our row in the Table, careful to not flicker the text too quickly
				if (update2 == null) update2 = new OldTime();    // Make the update Time object when this code first runs
				if (update2.expired(OldDescribe.delay, true)) { // We've waited long enough, or true for this is the first time
					update2.set();                           // Record we changed what's on the screen now
					model.changed();                         // Show the user current information
				}
			}
		}
	}
	
	private OldTime update2;

	// -------- Model --------

	/** This Download object's Model gives View objects above what they need to show us to the user. */
	public final MyModel model;
	public class MyModel extends Model { // Remember to call model.close()
		
		// Status
		
		/** Compose text for the Status column in our row in the Table. */
		public String status() {
			if      (download != null) return download.describeStatus(); // Getting
			else if (saved    != null) return "Done";                    // Done
			else if (cannot   != null) return cannot;                    // Cannot
			else                       return "";                        // Pending
		}

		/*
		// Java will call this compare() method to sort one row above or beneath another based on the Status column
		public static class CompareStatus implements Comparator<TableRow> {
			public int compare(TableRow o1, TableRow o2) { // Return negative to sort o1 first
				Download d1 = (Download)((o1).behind);
				Download d2 = (Download)((o2).behind);
				
				// Put the rows in the order Cannot, Done, Getting, Pending
				return d1.statusNumber() - d2.statusNumber();
			}
		}
		*/
		
		/** Turn our status into a number that CompareStatus.compare() can compare with another row. */
		public int statusNumber() {
			OldState state = state(); // Get our current state
			if      (state.state == OldState.couldNot)  return 0; // Cannot, order first
			else if (state.state == OldState.completed) return 1; // Done
			else if (state.state == OldState.doing)     return 2; // Getting
			else                                     return 3; // Pending, order last
		}
		
		// Name
		
		/** Compose text for the Name column in our row in the Table. */
		public String name() {
			if (download != null) return download.name().toString();
			else if (name != null) return name.toString();
			else return url.name().toString();
		}

		/*
		// Java will call this compare() method to sort one row above or beneath another based on the Name column
		public static class CompareName implements Comparator<TableRow> {
			public int compare(TableRow r1, TableRow r2) {
				Download d1 = (Download)((r1).behind);
				Download d2 = (Download)((r2).behind);
				return d1.describeName().compareTo(d2.describeName()); // Compare the text in the cells
			}
		}
		*/
		
		// Size
		
		/** Compose text for the Size column in our row in the Table. */
		public String size() {
			if (download != null) return download.describeSize();
			else if (saved != null) return OldDescribe.size(size);
			else return "";
		}

		/*
		// Java will call this compare() method to sort one row above or beneath another based on the Size column
		public static class CompareSize implements Comparator<TableRow> {
			public int compare(TableRow r1, TableRow r2) {
				Download d1 = (Download)((r1).behind);
				Download d2 = (Download)((r2).behind);

				// Compare the sizes
				long l = d1.size() - d2.size(); // l could be too big to cast to an int
				if      (l > 0) return 1;
				else if (l < 0) return -1;
				else            return 0;
			}
		}
		*/
		
		/** How big the file we're downloading is, in bytes, or -1 if we don't know. */
		public long sizeNumber() {
			if (download != null) return download.size();
			else if (saved != null) return size;
			else return -1;
		}
		
		// Type
		
		/** Compose text for the Type column in our row in the Table. */
		public String type() {
			if (download != null) return download.name().extension;
			else if (name != null) return name.extension;
			else return url.name().extension;
		}
		
		/*
		// Java will call this compare() method to sort one row above or beneath another based on the Type column
		public static class CompareType implements Comparator<TableRow> {
			public int compare(TableRow r1, TableRow r2) {
				Download d1 = (Download)((r1).behind);
				Download d2 = (Download)((r2).behind);
				return d1.describeType().compareTo(d2.describeType()); // Compare the text in the cells
			}
		}
		*/
		
		// Address
		
		/** Compose text for the Address column in our row in the Table. */
		public String address() {
			return url.address;
		}

		/*
		// Java will call this compare() method to sort one row above or beneath another based on the Address column
		public static class CompareAddress implements Comparator<TableRow> {
			public int compare(TableRow r1, TableRow r2) {
				Download d1 = (Download)((r1).behind);
				Download d2 = (Download)((r2).behind);
				return d1.describeAddress().compareTo(d2.describeAddress()); // Compare the text in the cells
			}
		}
		*/
		
		// Saved To
		
		/** Compose text for the Saved To column in our row in the Table. */
		public String savedTo() {
			if (saved != null) return saved.toString();
			else return "";
		}
		
		/*
		// Java will call this compare() method to sort one row above or beneath another based on the Saved To column
		public static class CompareSavedTo implements Comparator<TableRow> {
			public int compare(TableRow r1, TableRow r2) {
				Download d1 = (Download)((r1).behind);
				Download d2 = (Download)((r2).behind);
				return d1.describeSavedTo().compareTo(d2.describeSavedTo()); // Compare the text in the cells
			}
		}
		*/
		
		//TODO get sorting into Model also, how are you going to do that?

		/** Compose text about the current state of this Download object to show the user. */
		public Map<String, String> view() {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("Status",   status());
			map.put("Name",     name());
			map.put("Size",     size());
			map.put("Type",     type());
			map.put("Address",  address());
			map.put("Saved To", savedTo());
			return map;
		}
		
		/** The Download object that made and contains this Model. */
		public Object out() { return me(); }
	}
	
	/** Give inner classes a link to this outer Download object. */
	private Download me() { return this; }
}
