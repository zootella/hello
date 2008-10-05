package org.limewire.hello.download.user;

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
import org.limewire.hello.download.Download;

/** The Download Properties dialog box on the screen that shows the user the properties of a download. */
public class DownloadPropertiesDialog extends Close {
	
	// -------- Dialog --------

	/** The Download this dialog box is showing the properties of. */
	private Download download;

	/** Show the properties of Download in a dialog box. */
	public DownloadPropertiesDialog(Download download) {

		// Save the given link
		this.download = download;

		// Make our inner View object and connect the Download object's model to it
		view = new MyView();
		download.model.add(view); // When the Download Model changes, it will call our view.refresh() method

		// Text areas
		status  = new SelectTextArea(download.model.status()); // Get text from the Download's Model
		name    = new SelectTextArea(download.model.name());
		size    = new SelectTextArea(download.model.size());
		type    = new SelectTextArea(download.model.type());
		address = new SelectTextArea(download.model.address());
		savedTo = new SelectTextArea(download.model.savedTo());

		// Buttons
		Panel bar = Panel.row();
		bar.add(Cell.wrap(new JButton(new CloseAction())));

		// Lay out controls
		Panel panel = new Panel();
		panel.border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("Status")));
		panel.place(0, 1, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Name")));
		panel.place(0, 2, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Size")));
		panel.place(0, 3, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Type")));
		panel.place(0, 4, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Address")));
		panel.place(0, 5, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Saved To")));
		panel.place(1, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(status).fillWide());
		panel.place(1, 1, 1, 1, 1, 1, 0, 0, Cell.wrap(name).fillWide());
		panel.place(1, 2, 1, 1, 1, 1, 0, 0, Cell.wrap(size).fillWide());
		panel.place(1, 3, 1, 1, 1, 1, 0, 0, Cell.wrap(type).fillWide());
		panel.place(1, 4, 1, 1, 1, 1, 0, 0, Cell.wrap(address).fillWide());
		panel.place(1, 5, 1, 1, 1, 1, 0, 0, Cell.wrap(savedTo).fillWide());
		panel.place(1, 6, 1, 1, 1, 1, 0, 0, Cell.wrap(bar.jpanel).grow().lowerLeft());

		// Make the dialog box and show it on the screen
		dialog = Dialog.make("Download Properties");
		dialog.setContentPane(panel.jpanel); // Put everything we layed out in the dialog box
		Dialog.show(dialog, 500, 300);
	}
	
	/** The Java JDialog object which is the dialog box on the screen. */
	private JDialog dialog;

	/** The text areas in the dialog that hold information that changes. */
	private SelectTextArea status, name, size, type, address, savedTo;

	// View

	// When our Model underneath changes, it calls these methods
	private final View view;
	private class MyView implements View {

		// The Download Model changed, we need to update our text for the user
		public void refresh() {
			Refresh.text(status,  download.model.status()); // Get text from the Download's Model
			Refresh.text(name,    download.model.name());
			Refresh.text(size,    download.model.size());
			Refresh.text(type,    download.model.type());
			Refresh.text(address, download.model.address());
			Refresh.text(savedTo, download.model.savedTo());
		}

		// The Model beneath closed, take this View off the screen
		public void vanish() { me().close(); }
	}
	
	// -------- Buttons --------

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
		download.model.remove(view); // Disconnect us from the Model below
		dialog.dispose(); // Close the dialog box
	}
	
	/** Give inner classes a link to this outer DownloadPropertiesDialog object. */
	private DownloadPropertiesDialog me() { return this; }
}
