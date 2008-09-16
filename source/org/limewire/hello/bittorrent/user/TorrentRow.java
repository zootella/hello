package org.limewire.hello.bittorrent.user;


import java.util.Comparator;

import org.limewire.hello.base.time.OldTime;
import org.limewire.hello.base.user.TableRow;
import org.limewire.hello.bittorrent.Torrent;

public class TorrentRow {

	// -------- Make a new TorrentRow object, inside parts, and state --------

	/** Make a new TorrentRow object listed on the BitTorrent tab. */
	public TorrentRow(BitTorrentTab tab, Torrent torrent) {
		
		// Save the link to the DownloadTab
		this.tab = tab;

		// Save the URL we're going to try to download
		this.torrent = torrent;

		// Make a row for us in the Table
		row = describe();
		tab.table.add(row);
		tab.table.scroll(); // Scroll the table to the bottom so the user can see the new row there
	}

	/** A link to the BitTorrent tab in the program window. */
	private BitTorrentTab tab;
	/** Our row in the Table on the screen. */
	private TableRow row;
	/** The Torrent that we're showing to the user. */
	public Torrent torrent;

	// -------- The methods behind the items on the right-click menu --------

	/** true if our Torrent can share, false if it's already sharing. */
	public boolean canShare() { return torrent.canShare(); }
	/** true if our Torrent can pause, false if it's already paused. */
	public boolean canPause() { return torrent.canPause(); }
	/** Share our Torrent, have it contact the tracker and transfer data with peers. */
	public void share() { torrent.share(); }
	/** Pause our Torrent, have it disconnect from the tracker and peers. */
	public void pause() { torrent.pause(); }

	/** Remove this TorrentRow from the Table and from the program. */
	public void remove() {
		torrent.close();
		tab.table.remove(row); // Remove our row in the Table
	}
	
	/** The Time we last updated progress information in our row in the Table. */
	private OldTime update;

	// -------- Text and sorting for each cell of our row in the Table --------

	/** Update our row in the Table and the text in the status bar. */
	private void update() {
		tab.table.update(row, describe()); // Update our row in the Table
		tab.update(); // Update the text in the status bar
	}

	/** Make a TableRow object with the text that should go in each cell in our row in the Table. */
	public TableRow describe() {
		return new TableRow(tab.table, this, describeStatus(), describeName(), describeSize(), describeTorrent(), describeSavedTo());
	}

	// -------- Status --------
	
	/** Compose text for the Status column in our row in the Table. */
	private String describeStatus() {
		return torrent.describeStatus();
	}

	// Java will call this compare() method to sort one row above or beneath another based on the Status column
	public static class CompareStatus implements Comparator<TableRow> {
		public int compare(TableRow row1, TableRow row2) { // Return negative to sort row1 first
			return ((TorrentRow)row1.behind).torrent.describeStatusNumber() - ((TorrentRow)row2.behind).torrent.describeStatusNumber();
		}
	}
	
	// -------- Name --------
	
	/** Compose text for the Name column in our row in the Table. */
	private String describeName() {
		return torrent.describeName();
	}

	// Java will call this compare() method to sort one row above or beneath another based on the Name column
	public static class CompareName implements Comparator<TableRow> {
		public int compare(TableRow row1, TableRow row2) {
			return ((TorrentRow)row1.behind).describeName().compareTo(((TorrentRow)row2.behind).describeName()); // Compare the text in the cells
		}
	}
	
	// -------- Size --------
	
	/** Compose text for the Size column in our row in the Table. */
	private String describeSize() {
		return torrent.describeSize();
	}

	// Java will call this compare() method to sort one row above or beneath another based on the Size column
	public static class CompareSize implements Comparator<TableRow> {
		public int compare(TableRow row1, TableRow row2) {

			// Compare the sizes
			long l = ((TorrentRow)row1.behind).torrent.size() - ((TorrentRow)row2.behind).torrent.size(); // l could be too big to cast to an int
			if      (l > 0) return 1;
			else if (l < 0) return -1;
			else            return 0;
		}
	}
	
	// -------- Type --------
	
	/** Compose text for the Torrent column in our row in the Table. */
	private String describeTorrent() {
		return torrent.describeTorrent();
	}
	
	// Java will call this compare() method to sort one row above or beneath another based on the Torrent column
	public static class CompareTorrent implements Comparator<TableRow> {
		public int compare(TableRow row1, TableRow row2) {
			return ((TorrentRow)row1.behind).describeTorrent().compareTo(((TorrentRow)row2.behind).describeTorrent()); // Compare the text in the cells
		}
	}
	
	// -------- Saved To --------
	
	/** Compose text for the Saved To column in our row in the Table. */
	private String describeSavedTo() {
		return torrent.describeSavedTo();
	}
	
	// Java will call this compare() method to sort one row above or beneath another based on the Saved To column
	public static class CompareSavedTo implements Comparator<TableRow> {
		public int compare(TableRow row1, TableRow row2) {
			return ((TorrentRow)row1.behind).describeSavedTo().compareTo(((TorrentRow)row2.behind).describeSavedTo()); // Compare the text in the cells
		}
	}
}
