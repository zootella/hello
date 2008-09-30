package org.limewire.hello.base.user;

import org.limewire.hello.base.state.Model;
import org.limewire.hello.base.state.View;

/** A Row represents a row in a Table and on the screen. */
public class Row {
	
	// -------- A TableRow object, and its parts --------
	
	/**
	 * Make a new Row to show in a Table.
	 * @param table The Table this Row will be in
	 * @param model The object that this Row displays
	 */
	public Row(Table table, Model model) {
		this.table = table;
		this.model = model;
		view = new MyView();
		cells = new String[table.header.length];
	}
	
	/** The Table this Row is in. */
	public final Table table;
	/** The object under this Row that we're displaying. */
	public final Model model;
	/** The text in the cells in this Row, cells[0] is the String in the first column. */
	public final String[] cells;
	
	

	
	
	
	
	
	// View

	// When our Model underneath changes, it calls these methods
	public final MyView view;
	private class MyView implements View {

		// The Model has changed, we need to update what we're showing the user
		public void refresh() {
			table.update(model); // Have the Table do it
		}

		// The Model beneath closed, take this View off the screen
		public void vanish() {
			table.remove(model); // Have the Table do it
		}
	}
	
	
	


	// -------- Flag --------

	/** Table.sort() records if this row was selected in the JTable, and restores its selection after the sort. */
	public boolean selected;
}
