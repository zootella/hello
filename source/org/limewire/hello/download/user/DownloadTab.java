package org.limewire.hello.download.user;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.limewire.hello.all.user.Window;
import org.limewire.hello.base.data.Outline;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.desktop.Clipboard;
import org.limewire.hello.base.file.Here;
import org.limewire.hello.base.setting.BooleanSetting;
import org.limewire.hello.base.setting.NumberSetting;
import org.limewire.hello.base.setting.PathSetting;
import org.limewire.hello.base.setting.Store;
import org.limewire.hello.base.state.MessageException;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Describe;
import org.limewire.hello.base.user.OldTable;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.Table;
import org.limewire.hello.base.user.Row;
import org.limewire.hello.base.user.TextMenu;
import org.limewire.hello.base.web.Url;
import org.limewire.hello.base.web.Web;
import org.limewire.hello.download.Download;
import org.limewire.hello.feed.Episode;
import org.limewire.hello.feed.user.AddFeedDialog;
import org.limewire.hello.feed.user.EpisodePropertiesDialog;

public class DownloadTab {

	// -------- Settings --------

	/** true if the Pause button is pressed. */
	public BooleanSetting pauseSetting;
	/** The Path to the folder where the Download tab saves files. */
	public PathSetting folderSetting;
	/** How many files the Download tab will get at once. */
	public NumberSetting getSetting;

	// -------- The Download tab, and its parts --------

	/**
	 * Make the program's DownloadTab object, which appears as the "Download" tab in the program window.
	 * 
	 * @param window A link back up to the program's Window object which is the window on the screen
	 * @param web    A link to the program's Web object which can download files from the Web
	 * @param store  A link to the program's Store object that has the download list we'll restore
	 */
	public DownloadTab(Window window, Web web, Store store) {

		// Save the given links to objects in the program
		this.web = web;
		this.window = window;
		this.store = store;

		// Make the download settings, specifying default values
		pauseSetting = store.make("download.pause", false); // By default, not pressed
		folderSetting = store.make("download.folder", Here.folder().add("Shared")); // A folder named "Shared" next to where we're running
		getSetting = store.make("download.get", 3, 1); // 3 files at a time by default, must be 1 or more
		
		// Make the Pause and Remove buttons
		pause = new JToggleButton("Pause");
		pause.addItemListener(new MyItemListener());
		pause.setPreferredSize(new Dimension(Window.button, Window.button));
		pause.setSelected(pauseSetting.value()); // Press or not from settings
		remove = new JButton(new RemoveAction());
		remove.setPreferredSize(new Dimension(Window.button, Window.button));
		remove.setEnabled(false); // The Remove button isn't enabled until the user selects a row in the Table
		
		// Make the Addresses text box
		addresses = new JTextArea();
		new TextMenu(addresses); // Add a right-click menu with clipboard tools like Paste
		JScrollPane addressesScroll = new JScrollPane(addresses, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Make the Get, Paste, and Options buttons
		JButton get = new JButton(new EnterAction());
		JButton paste = new JButton(new PasteAction());
		JButton options = new JButton(new OptionsAction());

		// Make the Table and configure it
		table = new Table("Status", "Name", "Size", "Type", "Address", "Saved To"); // Column header text
		table.setColumnWidths(160, 160, 160, 100, 270, 270);
		table.rightAlign(2); // Right-align the Size column, index 2
		/*
		table.setComparators( // When the user clicks a column header, Java will call the compare() methods in these objects
			new Download.CompareStatus(),
			new Download.CompareName(),
			new Download.CompareSize(),
			new Download.CompareType(),
			new Download.CompareAddress(),
			new Download.CompareSavedTo());
			*/
		table.jtable.getSelectionModel().addListSelectionListener(new MyTableSelectionListener()); // Find out when the user selects a row
		table.jtable.addMouseListener(new MyTableMouseListener()); // Find out when the user right-clicks

		// Make the Status bar
		status = new JLabel(" "); // A space so the JLabel has height

		// Lay out the buttons and other controls in the tab
		Panel top = new Panel(); // The Panel we'll put in the top half of the JSplitPane
		top.border();
		top.place(0, 0, 1, 2, 0, 0, 0, 0, Cell.wrap(pause));
		top.place(1, 0, 1, 2, 0, 1, 0, 0, Cell.wrap(remove));
		top.place(2, 0, 1, 2, 0, 1, 0, 1, Cell.wrap(new JLabel("Addresses")));
		top.place(3, 0, 2, 1, 0, 0, 1, 1, Cell.wrap(addressesScroll).fill());
		top.place(5, 0, 1, 2, 0, 0, 0, 0, Cell.wrap(get));
		top.place(3, 1, 1, 1, 0, 0, 0, 0, Cell.wrap(paste));
		top.place(4, 1, 1, 1, 0, 1, 0, 0, Cell.wrap(options));
		Panel bottom = Panel.column().border(); // The Panel we'll put in the bottom half of the JSplitPane
		bottom.add(Cell.wrap(table.scroll).fill()); // Add the Table object's JScrollPane
		bottom.add(Cell.wrap(status));
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, top.jpanel, bottom.jpanel);
		split.setDividerLocation(132);

		//TODO do you actually need to keep the individual items?
		// yes, to show them turn unavailable while still on the menu
		// if that's the only feature you get, don't do it
		
		
		getAction = new GetAction();
		resetAction = new ResetAction();
		removeAction = new RemoveAction();
		openAction = new OpenAction();
		openSavedFileAction = new OpenSavedFileAction();
		openContainingFolderAction = new OpenContainingFolderAction();
		copyAction = new CopyAction();
		propertiesAction = new PropertiesAction();
		
		// Make the menu the user will get when he or she right-clicks a row in the Table
		menu = new JPopupMenu(); // Make the menu, and add all the items
		menu.add(new JMenuItem(getAction));
		menu.add(new JMenuItem(resetAction));
		menu.add(new JMenuItem(removeAction));
		menu.addSeparator();
		menu.add(new JMenuItem(openAction));
		menu.add(new JMenuItem(openSavedFileAction));
		menu.add(new JMenuItem(openContainingFolderAction));
		menu.addSeparator();
		menu.add(new JMenuItem(copyAction));
		menu.add(new JMenuItem(propertiesAction));

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

		// Set the initial status bar text, like "0 files"
		update();
	}

	/** Store the download list and delete temporary files. */
	public void close() {
		
		/*
		// Store the download list for the next time the program runs
		if (!table.rows.isEmpty()) { // Only do something if we have a download
			Outline o = store.outline.m("download"); // Make "download", which has the settings and the list
			for (Row row : table.rows) // Loop through all the rows in the Table
				o.add(((Download)row.model.out()).toOutline()); // Have the DownloadRow behind it compose an Outline
		}
		*/

		// Remove all our downloads so they close their connetions and delete their temporary files
		for (Row row : new ArrayList<Row>(table.rows)) // Loop through a copy of the list to change the original
			((Download)row.model.out()).remove(); // This will also remove the row from the table
	}

	/** Get the Swing JComponent that is the user interface of this tab. */
	public JComponent component() {
		return split; // It's our JSplitPane, which is a JComponent
	}

	/** A link up to the program's Window object, which is the window this DownloadTab is inside. */
	public Window window;
	/** A link to the program's Web object, which can download files from the Web. */
	public Web web;
	/** A link to the program's Store object, which keeps download settings and the download list from the last time the program ran. */
	public Store store;

	/** The Swing JSplitPane object which puts the sizing bar through the tab, dividing the top from the bottom. */
	private JSplitPane split;
	/** The Addresses text box. */
	private JTextArea addresses;
	/** The Pause button which can be pressed in or not pressed in. */
	private JToggleButton pause;
	/** The Remove button. */
	private JButton remove;
	/** The Table that lists the files to download. */
	public Table table;
	/** The menu the user gets when he or she right-clicks a row in the Table. */
	public JPopupMenu menu;
	/** The items in the Table's right-click menu. */
	private Action getAction, resetAction, removeAction, openAction, openSavedFileAction, openContainingFolderAction, copyAction, propertiesAction;
	/** The status bar at the bottom of the tab, which reports text like "0 files". */
	private JLabel status;

	// -------- Methods Java calls when the user clicks --------
	
	// The user clicked the Paste button
	private class PasteAction extends AbstractAction {
		public PasteAction() { super("Paste"); }
		public void actionPerformed(ActionEvent a) {
			
			// Get the text from the clipboard, and enter it into the download list
			enter(Clipboard.paste());
		}
	}

	// The user clicked the Options button
	private class OptionsAction extends AbstractAction {
		public OptionsAction() { super("Options..."); }
		public void actionPerformed(ActionEvent a) {

			// Show the Download Options dialog box
			new DownloadOptions(); // Pass a link to this DownloadTab object
		}
	}
	
	// The user clicked the Get button, which enters the text into the list
	private class EnterAction extends AbstractAction {
		public EnterAction() { super("Get"); }
		public void actionPerformed(ActionEvent a) {

			// Move the text from the Addresses box into the download list 
			String s = addresses.getText();
			addresses.setText("");
			enter(s);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	// When the user presses the Pause button in or pops it back out, Java calls this itemStateChanged() method
	private class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {

			// Save whether the Pause button is pressed or not in the program's settings
			pauseSetting.set(pause.isSelected());
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

		// The user clicked the mouse in the Table
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) { // It was a double-click
				((Download)table.getSelectedRow().model.out()).open(); // Open the saved file, or the Web address
			}
		}

		// The user pressed or released the mouse in the Table
		public void mousePressed(MouseEvent e) { show(e); }
		public void mouseReleased(MouseEvent e) { show(e); }
		private void show(MouseEvent e) {
			if (e.isPopupTrigger() && table.areRowsSelected()) { // Only do something when we get the correct event, and if rows are selected

				/*
				// Decide if the Get, Open Saved File, and Open Containing Folder menu items should be enabled or disabled
				getItem.setEnabled(false); // Start by disabling them, we'll enable those that can work next
				openSavedFileItem.setEnabled(false);
				openContainingFolderItem.setEnabled(false);
				for (Row row : table.getSelectedRows()) { // Loop through all the selected rows
					if (((Download)row.model.out()).canGet()) getItem.setEnabled(true); // Enable Get if we find a row that can
					if (((Download)row.model.out()).canOpenSavedFile()) { // We found a row that has a saved file on the disk
						openSavedFileItem.setEnabled(true);             // Enable Open Saved File and Open Containing Folder
						openContainingFolderItem.setEnabled(true);
					}
				}
				openItem.setEnabled(true); // If more than 1 row is selected, disable Open, Open Saved File, and Open Containing Folder
				if (table.jtable.getSelectedRowCount() > 1) {
					openItem.setEnabled(false);
					openSavedFileItem.setEnabled(false);
					openContainingFolderItem.setEnabled(false);
				}
				*/
				
				//TODO change these to hit the action, not the button or menu item
				/*
				EnterAction enter = new EnterAction();
				enter.setEnabled(true);
				*/

				// Show the menu to the user
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	// When the user clicks an item on the Table's right-click menu, Java calls this actionPerformed() method
	private class MyTableActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// If the user clicked Get, call get() on each DownloadRow object behind the TableRow objects, and so on
			if (e.getActionCommand().equals("Get"))                    for (Row row : table.getSelectedRows()) ((Download)row.model.out()).get();
			if (e.getActionCommand().equals("Reset"))                  for (Row row : table.getSelectedRows()) ((Download)row.model.out()).reset();
			if (e.getActionCommand().equals("Remove"))                 for (Row row : table.getSelectedRows()) ((Download)row.model.out()).remove();
			if (e.getActionCommand().equals("Open"))                   for (Row row : table.getSelectedRows()) ((Download)row.model.out()).openUrl();
			if (e.getActionCommand().equals("Open Saved File"))        for (Row row : table.getSelectedRows()) ((Download)row.model.out()).openSavedFile();
			if (e.getActionCommand().equals("Open Containing Folder")) for (Row row : table.getSelectedRows()) ((Download)row.model.out()).openContainingFolder();


			// Update the text in the status bar
			update();
		}
	}
	
	
	// The user clicked the Get context menu item
	private class GetAction extends AbstractAction {
		public GetAction() { super("Get"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling get() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).get();
		}
	}
	
	// The user clicked the Pause context menu item
	private class PauseAction extends AbstractAction {
		public PauseAction() { super("Pause"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling pause() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).pause();
		}
	}
	
	// The user clicked the Reset context menu item
	private class ResetAction extends AbstractAction {
		public ResetAction() { super("Reset"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling reset() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).reset();
		}
	}
	
	// The user clicked the Remove button or context menu item
	private class RemoveAction extends AbstractAction {
		public RemoveAction() { super("Remove"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling remove() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).remove();
		}
	}

	// The user clicked the Open context menu item
	private class OpenAction extends AbstractAction {
		public OpenAction() { super("Open"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling openUrl() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).openUrl();
		}
	}
	
	// The user clicked the Open Saved File context menu item
	private class OpenSavedFileAction extends AbstractAction {
		public OpenSavedFileAction() { super("Open Saved File"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling openSavedFile() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).openSavedFile();
		}
	}
	
	// The user clicked the Open Containing Folder context menu item
	private class OpenContainingFolderAction extends AbstractAction {
		public OpenContainingFolderAction() { super("Open Containing Folder"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows, calling openContainingFolder() on the Download object behind each one
			for (Row row : table.getSelectedRows()) ((Download)(row.model.out())).openContainingFolder();
		}
	}
	
	// The user clicked the Copy context menu item
	private class CopyAction extends AbstractAction {
		public CopyAction() { super("Copy"); }
		public void actionPerformed(ActionEvent a) {

			// Copy the URLs of all the selected rows onto the clipboard
			StringBuffer s = new StringBuffer();
			for (Row row : table.getSelectedRows())
				s.append(((Download)row.model.out()).url.address + "\r\n");
			Clipboard.copy(s.toString());
		}
	}
	
	// The user clicked the Properties context menu item
	private class PropertiesAction extends AbstractAction {
		public PropertiesAction() { super("Properties"); }
		public void actionPerformed(ActionEvent a) {

			/*
			// Show the dialog box for the first selected row
			new DownloadPropertiesDialog((Download)table.getSelectedRow().model.out());
			*/
		}
	}

	// -------- Enter addresses into the list of files to download --------

	/** Enter text from the clipboard or Addresses box like "http://site.com\nhttp://site2.com" into the Table. */
	private void enter(String s) {
		/*
		for (String line : Text.lines(s)) {     // Loop for each line of text
			try {
				Url url = new Url(line);        // Try parsing the line of text into a Url object
				if (!listed(url))               // We don't already have that Url in the Table, then
					new Download(this, url); // Make a new DownloadRow for it
			} catch (MessageException e) {}     // That line didn't parse into a Url, try the next one
		}
		update(); // Update the text in the status bar
		*/
	}
	
	/** true if the given Url is already listed in the Table on the DownloadTab. */
	private boolean listed(Url url) {
		for (Row row : table.rows) // Loop through all the rows in the Table
			if (url.equals(((Download)row.model.out()).url)) // Compare the row's Url to the given one
				return true; // Already got it, don't add it
		return false; // Don't have it yet, add it
	}

	/**
	 * Update the text in the status bar, like "3 files  2 to get  1 selected".
	 * 
	 * You need to call this right after:
	 * -You add or remove a DownloadRow, listing it in the Table.
	 * -A DownloadRow's state changes, like it finishes downloading, for instance.
	 * -The user changes the selection of the rows in the Table, like he or she selects a row.
	 */
	public void update() {
		
		// Get current information for the status bar
		int files = table.rows.size(); // Find out how many rows there are
		int get = 0; // Count how many aren't done
		for (Row row : table.rows) if (!((Download)row.model.out()).state().isClosed()) get++;
		int selected = table.jtable.getSelectedRowCount(); // Find out how many are selected

		// Compose status bar text like "3 files  2 to get  1 selected", and show it to the user
		String s = Describe.number(files, "file");
		if (get      != 0) s += "  " + Describe.commas(get)      + " to get";
		if (selected != 0) s += "  " + Describe.commas(selected) + " selected";
		status.setText(s); // Have the JLabel which is our status bar say it
	}
	
	
	

	// -------- Actions --------

	
	
	
	
	
	
	
	
	
}
