package org.limewire.hello.base.user;



/** A TableRow object represents a row in a Table object and on the screen. */
public class TableRow {

	// -------- A TableRow object, and its parts --------
	
	/**
	 * Make a new TableRow object.
	 * When you add it to a Table, it will show up as a row on the screen.
	 * 
	 * @param behind The object that is making this new TableRow, and will sit behind it
	 * @param cells  A String array with the text for each column
	 */
	public TableRow(OldTable table, Object behind, String... cells) {
		this.table = table;
		this.behind = behind; // Save everything in this new TableRow object
		this.cells = cells;
	}
	
	private final OldTable table;

	/** A link up to the object that made this TableRow, and is using it to show itself to the user. */
	public Object behind;

	/**
	 * The text in the cells in this row.
	 * For instance, cells[0] is the String in the first column, cells[1] is the next one, and so on.
	 */
	public String[] cells;

	/** Table.sort() records if this row was selected in the JTable, and restores its selection after the sort. */
	public boolean selected;
}
