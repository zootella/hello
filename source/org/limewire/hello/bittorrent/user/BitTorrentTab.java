package org.limewire.hello.bittorrent.user;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.limewire.hello.all.user.Window;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.OldDescribe;
import org.limewire.hello.base.user.OldTable;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.TableRow;
import org.limewire.hello.bittorrent.BitTorrent;

public class BitTorrentTab {

	// -------- The BitTorrent tab, and its parts --------

	/**
	 * Make the program's BitTorrentTab object, which appears as the "BitTorrent" tab in the program window.
	 * 
	 * @param window A link back up to the program's Window object which is the window on the screen
	 * @param store  A link to the program's Store object that has the torrent list we'll restore
	 */
	public BitTorrentTab(Window window, BitTorrent bittorrent) {

		// Save the given links to objects in the program
		this.window = window;
		this.bitTorrent = bittorrent;

		// Make a new MyActionListener object for Java to call when the user clicks buttons on this tab
		MyActionListener listener = new MyActionListener();
		tab = this; // Save a link to this new DownloadTab object for MyActionListener to use
		
		// Make the toolbar buttons
		pause = new JToggleButton("Pause");
		pause.addItemListener(new MyItemListener());
		remove = new JButton("Remove");
		remove.addActionListener(listener);
		remove.setEnabled(false); // The Remove button isn't enabled until the user selects a row in the Table
		add = new JButton("Add");
		add.addActionListener(listener);
		make = new JButton("Make");
		make.addActionListener(listener);
		options = new JButton("Options");
		options.addActionListener(listener);

		// Make the Table and configure it
		table = new OldTable("Status", "Name", "Size", "Torrent", "Saved To"); // Column header text
		table.setColumnWidths(260, 160, 160, 270, 270);
		table.rightAlign(2); // Right-align the Size column, index 2
		table.setComparators( // When the user clicks a column header, Java will call the compare() methods in these objects
			new TorrentRow.CompareStatus(),
			new TorrentRow.CompareName(),
			new TorrentRow.CompareSize(),
			new TorrentRow.CompareTorrent(),
			new TorrentRow.CompareSavedTo());
		table.jtable.getSelectionModel().addListSelectionListener(new MyTableSelectionListener()); // Find out when the user selects a row
		table.jtable.addMouseListener(new MyTableMouseListener()); // Find out when the user right-clicks

		// Make the Status bar
		status = new JLabel(" "); // A space so the JLabel has height

		// Lay out the buttons and other controls in the tab
		panel = Panel.column().border();
		panel.add(Cell.wrap(Panel.row(Cell.wrap(pause), Cell.wrap(remove), Cell.wrap(add), Cell.wrap(make), Cell.wrap(options)).jpanel));
		panel.add(Cell.wrap(table.scroll).fill());
		panel.add(Cell.wrap(status));
		
		// Make the menu the user will get when he or she right-clicks a row in the Table
		shareItem = new JMenuItem("Share"); // Make the menu items
		pauseItem = new JMenuItem("Pause");
		removeItem = new JMenuItem("Remove");
		MyTableActionListener tableListener = new MyTableActionListener(); // Have Java call MyTableActionListener when the user clicks the menu
		shareItem.addActionListener(tableListener);
		pauseItem.addActionListener(tableListener);
		removeItem.addActionListener(tableListener);
		menu = new JPopupMenu(); // Make the menu, and add all the items
		menu.add(shareItem);
		menu.add(pauseItem);
		menu.add(removeItem);
		
		// Have settings press the Pause button
		pause.setSelected(bittorrent.pauseSetting.value());

		// Set the initial status bar text, like "0 files"
		update();
	}
	
	/** Get the Swing JComponent that is the user interface of this tab. */
	public JComponent component() {
		return panel.jpanel; // It's the JPanel in our Panel object, which is a JComponent
	}

	/** A link to this DownloadTab object for MyActionListner to use. */
	private BitTorrentTab tab;
	/** A link up to the program's Window object, which is the window this DownloadTab is inside. */
	public Window window;
	/** A link to the program's BitTorent object, which keeps the list of torrents. */
	public BitTorrent bitTorrent;

	/** A Panel object which contains the Swing JPanel that holds all the user interface components. */
	private Panel panel;
	
	/** The buttons in the toolbar. */
	private JButton make, add, remove, options;
	/** The Pause button which can be pressed in or not pressed in. */
	private JToggleButton pause;
	
	/** The Table that lists the files to download. */
	public OldTable table;
	/** The menu the user gets when he or she right-clicks a row in the Table. */
	public JPopupMenu menu;
	/** The items in the Table's right-click menu. */
	private JMenuItem shareItem, pauseItem, removeItem;
	/** The status bar at the bottom of the tab, which reports text like "0 files". */
	private JLabel status;

	// -------- Methods Java calls when the user clicks --------

	// When the user presses the Pause button in or pops it back out, Java calls this itemStateChanged() method
	private class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			
			// Find out whether the Pause button is pressed or not
			boolean b = pause.isSelected();

			// Save whether the Pause button is pressed or not in the program's settings
			bitTorrent.pauseSetting.set(b);
			
			// Pause or share all our torrents
			for (TableRow row : table.rows) {
				if (b) ((TorrentRow)row.behind).pause();
				else   ((TorrentRow)row.behind).share();
			}
		}
	}

	// When the user clicks a button on this tab, Java calls this actionPerformed() method
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			// The user clicked the Remove button
			if (e.getActionCommand().equals("Remove")) {
				for (TableRow row : table.getSelectedRows()) // Loop down the selected rows
					((TorrentRow)row.behind).remove();       // Call remove() on the TorrentRow object behind each one
				update();                                    // Update the text in the status bar
				
			// The user clicked the Add button
			} else if (e.getActionCommand().equals("Add")) {
				new AddDialog(tab);

			// The user clicked the Make button
			} else if (e.getActionCommand().equals("Make")) {
				new MakeDialog(tab); // Show the dialog box, pass a link to this BitTorrentTab object
				
			// The user clicked the Options button
			} else if (e.getActionCommand().equals("Options")) {
				new OptionsDialog(tab);
			}
		}
	}

	// When the user selects or unselects a row in the Table, Java calls this valueChanged() method
	private class MyTableSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			
			// Only enable the Remove button when a row is selected 
			remove.setEnabled(table.areRowsSelected());
			update();
		}
	}

	// When the user clicks the mouse in the Table, Java calls the methods here
	private class MyTableMouseListener extends MouseAdapter {

		// The user pressed or released the mouse in the Table
		public void mousePressed(MouseEvent e) { show(e); }
		public void mouseReleased(MouseEvent e) { show(e); }
		private void show(MouseEvent e) {
			if (e.isPopupTrigger() && table.areRowsSelected()) { // Only do something when we get the correct event, and if rows are selected

				// Show the menu to the user
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	// When the user clicks an item on the Table's right-click menu, Java calls this actionPerformed() method
	private class MyTableActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// If the user clicked Get, call get() on each DownloadRow object behind the TableRow objects, and so on
			if (e.getActionCommand().equals("Share"))  for (TableRow row : table.getSelectedRows()) ((TorrentRow)row.behind).share();
			if (e.getActionCommand().equals("Pause"))  for (TableRow row : table.getSelectedRows()) ((TorrentRow)row.behind).pause();
			if (e.getActionCommand().equals("Remove")) for (TableRow row : table.getSelectedRows()) ((TorrentRow)row.behind).remove();

			// Update the text in the status bar
			update();
		}
	}

	/** Update the text in the status bar, like "3 torrents". */
	public void update() {
		status.setText(OldDescribe.number(table.rows.size(), "torrent"));
	}
}
