package org.limewire.hello.feed.user;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.limewire.hello.all.user.Window;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.Row;
import org.limewire.hello.base.user.Table;
import org.limewire.hello.feed.Episode;
import org.limewire.hello.feed.Feed;
import org.limewire.hello.feed.FeedList;

/** The Feed tab in the Window that lists feed subscriptions and the episodes in each. */
public class FeedTab {
	
	// -------- Tab --------
	
	/** The program's main Window this tab is in. */
	public final Window window;
	/** The program's list of feeds. */
	public final FeedList list;

	/** Make the Feed tab. */
	public FeedTab(Window window, FeedList list) {

		// Save given links
		this.window = window;
		this.list = list;
		
		// Make buttons
		add = new JButton(new FeedAddAction());
		remove = new JButton(new FeedRemoveAction());
		remove.setEnabled(false); // The Remove button isn't enabled until the user selects a row in the Table
		refresh = new JButton(new FeedRefreshAllAction());

		// Make the feed Table and its context menu
		feeds = new Table("Status", "Name", "Description", "Address"); // Column header text
		feeds.setColumnWidths(100, 280, 400, 300);
		feeds.jtable.getSelectionModel().addListSelectionListener(new FeedTableSelectionListener()); // Find out when the user selects a row
		feeds.jtable.addMouseListener(new FeedTableMouseListener()); // Find out when the user right-clicks
		JMenuItem feedRemove = new JMenuItem(new FeedRemoveAction()); // Make the menu items
		JMenuItem feedRefresh = new JMenuItem(new FeedRefreshAction());
		JMenuItem feedProperties = new JMenuItem(new FeedPropertiesAction());
		feedMenu = new JPopupMenu(); // Make the menu, and add all the items
		feedMenu.add(feedRemove);
		feedMenu.add(feedRefresh);
		feedMenu.addSeparator();
		feedMenu.add(feedProperties);
		
		// Make the episode Table and its context menu
		episodes = new Table("Status", "Episode", "Time", "Date", "Description", "Address");
		episodes.setColumnWidths(100, 160, 60, 60, 400, 300);
		episodes.jtable.addMouseListener(new EpisodeTableMouseListener()); // Find out when the user right-clicks
		episodeMenu = new JPopupMenu(); // Make the menu, and add all the items
		episodeMenu.add(new JMenuItem(new EpisodeGetAction()));
		episodeMenu.add(new JMenuItem(new EpisodePauseAction()));
		episodeMenu.add(new JMenuItem(new EpisodeResetAction()));
		episodeMenu.addSeparator();
		episodeMenu.add(new JMenuItem(new EpisodeOpenAction()));
		episodeMenu.add(new JMenuItem(new EpisodeOpenSavedFileAction()));
		episodeMenu.add(new JMenuItem(new EpisodeOpenContainingFolderAction()));
		episodeMenu.addSeparator();
		episodeMenu.add(new JMenuItem(new EpisodePropertiesAction()));

		// Lay them out
		Panel bar = Panel.row(Cell.wrap(add), Cell.wrap(remove), Cell.wrap(refresh));
		Panel top = Panel.column(Cell.wrap(feeds.scroll).fill(), Cell.wrap(bar.jpanel)).border();
		Panel bottom = Panel.row(Cell.wrap(episodes.scroll).fill()).border();
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, top.jpanel, bottom.jpanel);
		split.setDividerLocation(160);
	}
	
	/** The Table in the tab that lists the feeds. */
	public final Table feeds;
	/** The Table in the tab that lists the episodes in a feed. */
	public final Table episodes;
	
	/** The feeds Table context menu. */
	private JPopupMenu feedMenu;
	/** The episodes Table context menu. */
	private JPopupMenu episodeMenu;

	/** The Add button that opens the Add Feed dialog box. */
	private JButton add;
	/** The Remove button that removes the selected feeds. */
	private JButton remove;
	/** The Refresh button that gets the feeds and recent episodes. */
	private JButton refresh;
	
	/** Splits the tab into top and bottom sections. */
	private JSplitPane split;
	
	/** Get the Swing JComponent that is the user interface of this tab. */
	public JComponent component() {
		return split; // It's our JSplitPane, which is a JComponent
	}
	
	/** Give inner classes a link to this outer FeedTab object. */
	private FeedTab me() { return this; }

	// -------- Listeners --------

	// The user selected or unselected a row in the feeds Table
	private class FeedTableSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			
			// Only enable the Remove button when a row is selected 
			remove.setEnabled(feeds.areRowsSelected());
			
			// Clear and refill the episodes Table with the episodes of the first selected feed
			episodes.clear();
			Row selected = feeds.getSelectedRow();
			if (selected != null) {
				for (Episode episode : ((Feed)selected.model.out()).episodes)
					episodes.add(episode.model);
			}
		}
	}

	// When the user clicks the mouse in the feeds Table, Java calls the methods here
	private class FeedTableMouseListener extends MouseAdapter {

		// The user pressed or released the mouse in the Table
		public void mousePressed(MouseEvent e) { show(e); }
		public void mouseReleased(MouseEvent e) { show(e); }
		private void show(MouseEvent e) {
			if (e.isPopupTrigger() && feeds.areRowsSelected()) { // Only do something when we get the correct event, and if rows are selected

				// Show the menu to the user
				feedMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	// When the user clicks the mouse in the episodes Table, Java calls the methods here
	private class EpisodeTableMouseListener extends MouseAdapter {

		// The user pressed or released the mouse in the Table
		public void mousePressed(MouseEvent e) { show(e); }
		public void mouseReleased(MouseEvent e) { show(e); }
		private void show(MouseEvent e) {
			if (e.isPopupTrigger() && episodes.areRowsSelected()) { // Only do something when we get the correct event, and if rows are selected

				// Show the menu to the user
				episodeMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	// -------- Actions --------

	// The user clicked the Add button
	private class FeedAddAction extends AbstractAction {
		public FeedAddAction() { super("Add..."); }
		public void actionPerformed(ActionEvent a) {
			
			// Show the dialog box
			new AddFeedDialog(me()); // Pass a link to this tab
		}
	}
	
	// The user clicked the Remove button or context menu item
	private class FeedRemoveAction extends AbstractAction {
		public FeedRemoveAction() { super("Remove"); }
		public void actionPerformed(ActionEvent a) {

			// Loop down the selected rows
			for (Row row : feeds.getSelectedRows())
				((Feed)row.model.out()).close(); // Close the Feed object
		}
	}
	
	// The user clicked the Refresh button
	private class FeedRefreshAllAction extends AbstractAction {
		public FeedRefreshAllAction() { super("Refresh"); }
		public void actionPerformed(ActionEvent a) {
			
			// Tell the list of feeds
			list.refresh();
		}
	}

	// The user clicked the Refresh context menu item
	private class FeedRefreshAction extends AbstractAction {
		public FeedRefreshAction() { super("Refresh"); }
		public void actionPerformed(ActionEvent a) {
			
			// Loop down the selected rows
			for (Row row : feeds.getSelectedRows())
				((Feed)row.model.out()).refresh(); // Refresh the Feed object
		}
	}
	
	// The user clicked the feed Properties context menu item
	private class FeedPropertiesAction extends AbstractAction {
		public FeedPropertiesAction() { super("Properties"); }
		public void actionPerformed(ActionEvent a) {

			// Show the dialog box for the first selected row
			new FeedPropertiesDialog(me(), (Feed)feeds.getSelectedRow().model.out());
		}
	}

	// The user right-clicked an episode and chose Get
	private class EpisodeGetAction extends AbstractAction {
		public EpisodeGetAction() { super("Get"); }
		public void actionPerformed(ActionEvent a) {
			
			// Have the Episode do it
			((Episode)episodes.getSelectedRow().model.out()).get();
		}
	}

	// The user right-clicked an episode and chose Pause
	private class EpisodePauseAction extends AbstractAction {
		public EpisodePauseAction() { super("Pause"); }
		public void actionPerformed(ActionEvent a) {
			
			// Have the Episode do it
			((Episode)episodes.getSelectedRow().model.out()).pause();
		}
	}

	// The user right-clicked an episode and chose Reset
	private class EpisodeResetAction extends AbstractAction {
		public EpisodeResetAction() { super("Reset"); }
		public void actionPerformed(ActionEvent a) {
			
			// Have the Episode do it
			((Episode)episodes.getSelectedRow().model.out()).reset();
		}
	}

	// The user right-clicked an episode and chose Open
	private class EpisodeOpenAction extends AbstractAction {
		public EpisodeOpenAction() { super("Open"); }
		public void actionPerformed(ActionEvent a) {
			
			// Have the Episode do it
			((Episode)episodes.getSelectedRow().model.out()).open();
		}
	}

	// The user right-clicked an episode and chose Open Saved File
	private class EpisodeOpenSavedFileAction extends AbstractAction {
		public EpisodeOpenSavedFileAction() { super("Open Saved File"); }
		public void actionPerformed(ActionEvent a) {
			
			// Have the Episode do it
			((Episode)episodes.getSelectedRow().model.out()).openSavedFile();
		}
	}

	// The user right-clicked an episode and chose Open Containing Folder
	private class EpisodeOpenContainingFolderAction extends AbstractAction {
		public EpisodeOpenContainingFolderAction() { super("Open Containing Folder"); }
		public void actionPerformed(ActionEvent a) {
			
			// Have the Episode do it
			((Episode)episodes.getSelectedRow().model.out()).openContainingFolder();
		}
	}

	// The user right-clicked an episode and chose Properties
	private class EpisodePropertiesAction extends AbstractAction {
		public EpisodePropertiesAction() { super("Properties"); }
		public void actionPerformed(ActionEvent a) {

			// Show the dialog box for the first selected row
			new EpisodePropertiesDialog((Episode)episodes.getSelectedRow().model.out());
		}
	}
}
