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

public class OptionsDialog {

	// -------- The BitTorrent Options dialog box, and its parts --------
	
	/** A link up to the DownloadTab these options are for. */
	private BitTorrentTab tab;
	/** The Java JDialog object which is the dialog box on the screen. */
	public JDialog dialog;
	/** The editable text field for the folder path. */
	private JTextField folder;

	/** Show the user the BitTorrent Options dialog box. */
	public OptionsDialog(BitTorrentTab tab) {

		// Save the given link back up to the BitTorrentTab
		this.tab = tab;

		// Make Java Swing objects for the dialog box, and all the controls inside
		dialog = Dialog.modal("BitTorrent Options");
		folder = new JTextField(); // Editable text boxes
		new TextMenu(folder); // Give the text box a right-click menus that has Cut, Copy, and Paste
		folder.setText(tab.bitTorrent.folderSetting.toString()); // Load value from settings
		JButton browse = new JButton("Browse...");
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		ActionListener listener = new MyActionListener(); // Tell Java what to call when the user clicks the buttons
		browse.addActionListener(listener);
		ok.addActionListener(listener);
		cancel.addActionListener(listener);
		
		// Lay out the text boxes and buttons in the dialog box
		Panel column = Panel.column().border(); // The column of the whole dialog box
		column.add(Cell.wrap(new JLabel("Choose the folder where the program saves files")));
		column.add(Cell.wrap(folder).fillWide());
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
				Dialog.chooseFolder(dialog, folder);

			// The user clicked the OK button
			} else if (e.getActionCommand().equals("OK")) {

				// Save the settings
				tab.bitTorrent.folderSetting.set(folder.getText()); // Only changes if text is a Path

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
