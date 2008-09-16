package org.limewire.hello.feed.user;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.TextMenu;
import org.limewire.hello.base.web.Url;
import org.limewire.hello.feed.Feed;

/** The Add Feed dialog box on the screen that lets the user add a new feed to the list. */
public class AddFeedDialog {
	
	/** A link up to the tab this dialog box is for. */
	private FeedTab tab;
	
	/** The Java JDialog object which is the dialog box on the screen. */
	public JDialog dialog;
	/** The editable text field for the address. */
	private JTextField address;

	/**
	 * Show the Add Feed dialog box on the screen.
	 * @param tab A link back up to the Feed tab
	 */
	public AddFeedDialog(FeedTab tab) {

		// Save the given link back up to the FeedTab
		this.tab = tab;

		// Make dialog contents
		address = new JTextField(); // Editable text box
		new TextMenu(address); // Give it a right-click menu

		// Lay them out
		Panel bar = Panel.row(Cell.wrap(new JButton(new OkAction())), Cell.wrap(new JButton(new CancelAction())));
		Panel column = Panel.column().border();
		column.add(Cell.wrap(new JLabel("Enter the web address of an XML feed")));
		column.add(Cell.wrap(address).fillWide());
		column.add(Cell.wrap(bar.jpanel).grow().lowerRight());

		// Make the dialog box and show it on the screen
		dialog = Dialog.modal("Add Feed");
		dialog.setContentPane(column.jpanel); // Put everything we layed out in the dialog box
		Dialog.show(dialog, 500, 140);
	}
	
	// The user clicked the OK button
	private class OkAction extends AbstractAction {
		public OkAction() { super("OK"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

			// Get the URL the user entered
			Url url = null;
			try {
				url = new Url(address.getText());
			} catch (MessageException e) {
				JOptionPane.showMessageDialog(tab.window.frame, "Not a valid web address. Check the text and try again.");
				return; // Leave the dialog open to let the user try again
			}
			
			// Add the feed to the program's list
			Feed feed = tab.list.add(url);
			if (feed != null) // It was unique
				tab.feeds.add(feed.model); // List it in the Table

			// Remove the dialog box from the screen
			dialog.dispose();
		}
	}

	// The user clicked the Cancel button
	private class CancelAction extends AbstractAction {
		public CancelAction() { super("Cancel"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

			// Remove the dialog box from the screen
			dialog.dispose();
		}
	}
}
