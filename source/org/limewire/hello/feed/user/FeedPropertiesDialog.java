package org.limewire.hello.feed.user;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.View;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.OldDescribe;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.Refresh;
import org.limewire.hello.base.user.SelectTextArea;
import org.limewire.hello.feed.Feed;

/** The Feed Properties dialog box on the screen that shows the user the properties of a feed. */
public class FeedPropertiesDialog extends Close {
	
	// -------- Dialog --------

	/** The Feed this dialog box is showing the properties of. */
	private Feed feed;

	/** Show the properties of Feed in a dialog box. */
	public FeedPropertiesDialog(FeedTab tab, Feed feed) {

		// Save the given link
		this.feed = feed;

		// Make our inner View object and connect the Feed object's model to it
		view = new MyView();
		feed.model.add(view); // When the Feed Model changes, it will call our view.refresh() method

		// Text areas
		status      = new SelectTextArea(feed.model.status()); // Get text from the Feed's Model
		name        = new SelectTextArea(feed.model.name());
		description = new SelectTextArea(feed.model.description());
		address     = new SelectTextArea(feed.model.address());

		// Buttons
		Panel bar = Panel.row();
		bar.add(Cell.wrap(new JButton(new RemoveAction())));
		bar.add(Cell.wrap(new JButton(new RefreshAction())));
		bar.add(Cell.wrap(new JButton(new CloseAction())));

		// Lay out controls
		Panel panel = new Panel();
		panel.border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("Status")));
		panel.place(0, 1, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Name")));
		panel.place(0, 2, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Description")));
		panel.place(0, 3, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Address")));
		panel.place(1, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(status).fillWide());
		panel.place(1, 1, 1, 1, 1, 1, 0, 0, Cell.wrap(name).fillWide());
		panel.place(1, 2, 1, 1, 1, 1, 0, 0, Cell.wrap(description).fillWide());
		panel.place(1, 3, 1, 1, 1, 1, 0, 0, Cell.wrap(address).fillWide());
		panel.place(1, 4, 1, 1, 1, 1, 0, 0, Cell.wrap(bar.jpanel).grow().lowerLeft());

		// Make the dialog box and show it on the screen
		dialog = Dialog.make("Feed Properties");
		dialog.setContentPane(panel.jpanel); // Put everything we layed out in the dialog box
		Dialog.show(dialog, 500, 300);
	}
	
	/** The Java JDialog object which is the dialog box on the screen. */
	private JDialog dialog;

	/** The Status text area in the dialog. */
	private SelectTextArea status;
	/** The Name text area in the dialog. */
	private SelectTextArea name;
	/** The Description text are in the dialog. */
	private SelectTextArea description;
	/** The Address text area in the dialog. */
	private SelectTextArea address;

	// View

	// When our Model underneath changes, it calls these methods
	private final View view;
	private class MyView implements View {

		// The Feed Model changed, we need to update our text for the user
		public void refresh() {
			Refresh.text(status,      feed.model.status());
			Refresh.text(name,        feed.model.name());
			Refresh.text(description, feed.model.description());
			Refresh.text(address,     feed.model.address());
		}

		// The Model beneath closed, take this View off the screen
		public void vanish() { me().close(); }
	}
	
	// -------- Buttons --------

	// The user clicked the Remove button
	private class RemoveAction extends AbstractAction {
		public RemoveAction() { super("Remove"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			
			// Have the Feed object do it
			feed.close();
		}
	}
	
	// The user clicked the Refresh button
	private class RefreshAction extends AbstractAction {
		public RefreshAction() { super("Refresh"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

			// Have the Feed object do it
			feed.refresh();
		}
	}

	// The user clicked the Close button
	private class CloseAction extends AbstractAction {
		public CloseAction() { super("Close"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

			// Close the dialog box
			close();
		}
	}

	// -------- Methods --------

	/** Close this dialog box. */
	public void close() {
		if (already()) return;
		feed.model.remove(view); // Disconnect us from the Model below
		dialog.dispose(); // Close the dialog box
	}
	
	/** Give inner classes a link to this outer FeedPropertiesDialog object. */
	private FeedPropertiesDialog me() { return this; }
}
