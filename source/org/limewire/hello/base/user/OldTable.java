package org.limewire.hello.base.user;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.Receive;

public class OldTable {

	// -------- Make a Table and set up its columns --------
	
	/**
	 * Make a new Table object with the specified columns.
	 * It will be a JTable on the screen with some added features.
	 * The Table object will keep the table's data, so you won't have to deal with TableModel events.
	 * 
	 * @param header The colum titles in a String array.
	 *               For instance, new Table("Name", "Color", "Location") makes a 3-column table.
	 */
	public OldTable(String... header) {

		// Save the given column titles
		this.header = header; // This also tells us how many columns we have

		// Make our TableModel and JTable objects
		model = new MyTableModel(); // An instance of the inner MyTableModel class, our implementation of Java's TableModel
		jtable = new JTable(model); // The Java Swing JTable that will show up on the screen
		
		// Customize the JTable to look less like a spreadsheet and more like a folder listing files
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable the weird automatic resizing that's on by default
		jtable.setShowGrid(false);                        // Eliminate the grid lines
		jtable.setIntercellSpacing(new Dimension(0, 0));

		// When the user clicks a column header, Java will call our MyHeaderMouseListener.mouseClicked() method
		jtable.getTableHeader().addMouseListener(new MyHeaderMouseListener());

		// Put our JTable in a JScrollPane so it will show its header and scroll bars
		scroll = new JScrollPane(jtable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
	/** The Swing JTable object that shows this Table on the screen. */
	public JTable jtable;
	
	/** The Swing JScrollPane that our JTable is inside, and that makes its scroll bars. */
	public JScrollPane scroll;
	
	/**
	 * Set the widths of the columns in this Table.
	 * For instance, table.setColumnWidths(100, 50, 200) sets those pixel widths on a 3-column Table.
	 */
	public void setColumnWidths(int... widths) {

		// Loop for each of the given widths, setting the column with index c to it
		for (int c = 0; c < widths.length; c++) {
			jtable.getColumnModel().getColumn(c).setPreferredWidth(widths[c]);
		}
	}

	/** Right-align the text in column c, 0 is the first column. */
	public void rightAlign(int c) {

		// Give the column a custom renderer, using our inner class that right-aligns everything
		jtable.getColumnModel().getColumn(c).setCellRenderer(new MyRightRenderer());
	}
	// A right-aligned column uses a MyRightRenderer object instead of its default renderer
	private class MyRightRenderer extends DefaultTableCellRenderer {
		public MyRightRenderer() {
			super(); // Call the DefaultTableCellRenderer constructor to setup this new object
			setHorizontalAlignment(JLabel.RIGHT); // Right-align the text
		}
	}

	// -------- Add, update, and remove rows in this Table --------
	
	/**
	 * Add a new row to this Table.
	 * @param behind The object that will sit behind the new row
	 * @param map    A String map with the text for each column
	 */
	public void add(Model behind) {
		
		
		Map<String, String> view = behind.view();
		
		TableRow row = new TableRow(this, behind);    // Make a TableRow object
		row.cells = new String[header.length];  // Prepare the array
		for (int i = 0; i < header.length; i++) // Loop for each column
			row.cells[i] = view.get(header[i]);  // Copy in the cell text
		add(row);                               // Call the next method
	}
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Add a new row to this Table.
	 * Before you call this method, make a new TableRow object and add the text to its cells.
	 * The row will show up at the bottom of the list.
	 */
	public void add(TableRow row) {
		int r = rows.size();               // Find out how many rows we already have
		rows.add(row);                     // Add the given TableRow object to our ArrayList of table data
		model.fireTableRowsInserted(r, r); // Tell our JTable we added a row at index r
	}

	/** Scroll this Table to the bottom. */
	public void scroll() {
		if (rows.isEmpty()) return; // No rows
        Rectangle rectangle = jtable.getCellRect(rows.size() - 1, 0, true); // Find where the last row appears
        jtable.scrollRectToVisible(rectangle); // Scroll to bring it into view
	}

	/**
	 * Update the text in a row in this Table.
	 * 
	 * After adding a TableRow, don't change its text.
	 * Instead, call this method passing it a copy of the TableRow with updated text.
	 * This method will copy the text of a changed cell into the added TableRow and onto the screen.
	 * 
	 * @param row    A TableRow object added to this Table
	 * @param update Updated text in a TableRow object that is not a part of this Table
	 */
	public void update(TableRow row, TableRow update) {

		// Find the row's current index in the JTable
		int r = find(row);
		if (r == -1) return; // Not found
		
		// Loop through the cells in that row
		for (int c = 0; c < header.length; c++) {

			// The text in this cell is different
			if (!row.cells[c].equals(update.cells[c])) {

				// Update our TableRow object and the cell on the screen
				row.cells[c] = update.cells[c];
				model.fireTableCellUpdated(r, c);
			}
		}
	}

	/** Remove a row from this Table. */
	public void remove(TableRow row) {
		int r = find(row);                // Find where the TableRow is in our JTable right now
		if (r == -1) return;              // Not found
		rows.remove(r);                   // Remove the TableRow object from our ArrayList of table data
		model.fireTableRowsDeleted(r, r); // Remove the row from the JTable's display
	}

	/**
	 * Given a TableRow object, find the index where it's row is currently located in our JTable.
	 * As the user sorts the Table, a TableRow's row index can change.
	 * 
	 * @param row A TableRow object with a row added to our JTable.
	 * @return    The row index where its row is located, the first 3 rows have indices 0, 1, and 2.
	 *            -1 if not found.
	 */
	private int find(TableRow row) {
		for (int i = 0; i < rows.size(); i++) { // rows, our ArrayList of TableRow objects, is in the same order as the rows on the screen
			if (rows.get(i) == row) return i;   // Compare the object references to find the row
		}
		return -1; // Not found
	}

	// -------- Find out if any rows are selected and loop through them --------
	
	/** Determine if this Table has any selected rows right now. */
	public boolean areRowsSelected() {
		return jtable.getSelectedRowCount() != 0; // Ask our JTable how many rows are selected
	}
	
	/** Get the rows that are selected in this Table. */
	public List<TableRow> getSelectedRows() {
		ArrayList<TableRow> l = new ArrayList<TableRow>(); // Make a new empty list to fill and return
		for (int r : jtable.getSelectedRows())             // Loop through the selected row indices
			l.add(rows.get(r));                            // Add the TableRow object at that row index to our list
		return l;                                          // Return the list we filled
	}
	
	/** Get the first or only row that is selected in this Table. */
	public TableRow getSelectedRow() {
		int r = jtable.getSelectedRow(); // Get the index of the first selected row
		if (r == -1) return null;
		return rows.get(r);              // Get the TableRow object beneath it
	}

	// -------- The Table's data, including the text in the header and cells --------

	/** The text in the column headers in a String array. */
	private String[] header;
	
	/**
	 * The data in this Table.
	 * To loop through all the rows in this Table, loop through the TableRow objects in this ArrayList.
	 * rows.size() is the number of rows this Table has.
	 * rows.get(0) is the first row, rows.get(1) is the second row, and so on.
	 * The order of the objects in this ArrayList matches the order of the rows on the screen.
	 */
	public ArrayList<TableRow> rows = new ArrayList<TableRow>();
	
	/** Our JTable will call methods on this TableModel object to find out what text to put in a cell on the screen. */
	private MyTableModel model;

	// Our JTable calls these methods to paint text in the header and cells
	private class MyTableModel extends AbstractTableModel {

		// Java wants to know how many colums we have
		public int getColumnCount() {
			return header.length; // It's the length of our column header text array
		}

		// Java wants to know the text to draw at the top of a column
		public String getColumnName(int c) {
			return header[c]; // Look it up in our column header text array
		}

		// Java wants to know how many rows we have
		public int getRowCount() {
			return rows.size(); // It's the number of TableRow objects in our rows ArrayList
		}

		// Java needs the text to put in a cell
		public Object getValueAt(int r, int c) { // r and c are the row and column indices
			return rows.get(r).cells[c]; // Look up the correct String in our rows ArrayList
		}
	}

	// -------- Sorting --------
	
	/**
	 * Specify the objects this Table will use to sort its rows by each column.
	 * For instance, table.setComparators(new Comparator1(), new Comparator2(), new Comparator3()) does it.
	 * Comparator1 is a class that implements the Comparator interface, meaning it has a compare(o1, o2) method.
	 * When this Table needs to sort by the first column, it will call comparator1.compare(row1, row2) to see which goes first.
	 */
	@SuppressWarnings("unchecked") // The variable argument feature of Java doesn't like Comparator<TableRow> 
	public void setComparators(Comparator... comparators) {
		this.comparators = comparators; // Save the given array
	}
	
	/**
	 * An array of Comparator objects, which are just objects that have a compare(o1, o2) method.
	 * Use the Comparator object stored in comparators[0] to sort the first column, comparators[1] to sort the second, and so on.
	 */
	private Comparator<TableRow>[] comparators;
	
	// When the user clicks a column header, Java will call the mouseClicked() method here
	private class MyHeaderMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			sort(e.getX()); // Sort our Table
		}
	}

	/**
	 * Sort this Table's rows into a new order.
	 * 
	 * @param x The horizontal distance in the header the user clicked
	 */
	private void sort(int x) {
		
		// Only do something if we have sorting objects
		if (comparators == null) return;
		
		// Get the index of the column the user clicked
		int view = jtable.getColumnModel().getColumnIndexAtX(x);
		if (view == -1) return; // The user clicked to the right of all the colum headers
		int c = model(view);

		// Remove a sort column label we previously set
		if (sorted) jtable.getColumnModel().getColumn(view(sort)).setHeaderValue(header[sort]);

		// Determine if we'll sort ascending or descending, and record how we will sort the Table
		reverse = sorted && sort == c && !reverse; // If we're already sorted forward by column c, sort descending
		sorted = true;
		sort = c;

		// Add an arrow made of text to the sorted column header, like "Column Title  /\"
		jtable.getColumnModel().getColumn(view(sort)).setHeaderValue(header[sort] + (reverse ? "  \\/" : "  /\\"));
		
		// Save the row selections
		for (int r : jtable.getSelectedRows()) rows.get(r).selected = true; // Mark selected rows in their TableRow objects

		// Sort our rows ArrayList of TableRow objects into a new order
		Comparator<TableRow> comparator = comparators[sort]; // Use the Comparator object for the clicked column
		if (reverse) comparator = Collections.reverseOrder(comparator); // Sort into descending order
		Collections.sort(rows, comparator);

		// Redraw all the rows of the Table on the screen
		model.fireTableDataChanged(); // This kills the selection

		// Restore the row selections
		for (TableRow row : rows) {
			if (row.selected) {
				int r = find(row);
				jtable.addRowSelectionInterval(r, r); // Select the row with row index r
				row.selected = false; // Clear all the set flags for next time
			}
		}
	}

	/** true if the user clicked a column header, sorting this Table by it. */
	private boolean sorted;

	/** The index of the column this Table is sorted by. */
	private int sort;
	
	/** true if the user clicked the sort column a second time, sorting it into descending order. */
	private boolean reverse;

	/** Given the index of a column on the screen, find its original index before the user dragged columns into a different order. */
	private int model(int view) {
		return jtable.getColumnModel().getColumn(view).getModelIndex();
	}

	/** Given a column's original index, find its index on the screen after the user has dragged it into a different order. */
	private int view(int model) {
		for (int c = 0; c < jtable.getColumnCount(); c++) { // Loop through the columns on the screen looking for the original index number
			if (jtable.getColumnModel().getColumn(c).getModelIndex() == model) return c;
		}
		return -1;
	}
}
