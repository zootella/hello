package org.limewire.hello.bittorrent.user;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.TextMenu;

public class AddDialog {

	// -------- The Add Torrent dialog box, and its parts --------
	
	/** A link up to the BitTorrentTab this dialog box is for. */
	private BitTorrentTab tab;
	/** The Java JDialog object which is the dialog box on the screen. */
	public JDialog dialog;
	/** The editable text field for the path to add. */
	private JTextField torrent;

	/** Show the user the Add Torrent dialog box. */
	public AddDialog(BitTorrentTab tab) {

		// Save the given link back up to the BitTorrentTab
		this.tab = tab;

		// Make Java Swing objects for the dialog box, and all the controls inside
		dialog = Dialog.modal("Add Torrent");
		torrent = new JTextField(); // Editable text boxes
		new TextMenu(torrent); // Give the text box a right-click menus that has Cut, Copy, and Paste
		JButton browse = new JButton("Browse...");
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		ActionListener listener = new MyActionListener(); // Tell Java what to call when the user clicks the buttons
		browse.addActionListener(listener);
		ok.addActionListener(listener);
		cancel.addActionListener(listener);
		
		// Lay out the text boxes and buttons in the dialog box
		Panel column = Panel.column().border(); // The column of the whole dialog box
		column.add(Cell.wrap(new JLabel("Enter the disk path or Web address of a .torrent file")));
		column.add(Cell.wrap(torrent).fillWide());
		column.add(Cell.wrap(browse).upperRight());
		column.add(Cell.wrap((Panel.row(Cell.wrap(ok), Cell.wrap(cancel))).jpanel).grow().lowerRight());
		dialog.setContentPane(column.jpanel); // Put everything we layed out in the dialog box

		// Show the dialog box to the user
		Dialog.show(dialog, 360, 170);
	}
	
	// -------- Methods Java calls when the user clicks --------

	// When the user clicks the OK or Cancel buttons, Java will call this actionPerformed() method
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// The user clicked the Browse button
			if (e.getActionCommand().equals("Browse...")) {

				// Show the choice box to the user, and set the path text
				Dialog.chooseFile(dialog, torrent);
			
			// The user clicked the OK button
			} else if (e.getActionCommand().equals("OK")) {

				// Remove the dialog box from the screen
				dialog.dispose();

			// The user clicked the Cancel button
			} else if (e.getActionCommand().equals("Cancel")) {
				
				// Remove the dialog box from the screen
				dialog.dispose();
			}
		}
	}
}
