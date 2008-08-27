package org.limewire.hello.download.user;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.TextMenu;

public class DownloadOptions {

	// -------- The Download Options dialog box, and its parts --------
	
	/** The Java JDialog object which is the dialog box on the screen. */
	public JDialog dialog;
	/** The editable text field for the save path. */
	private JTextField folder;
	/** The editable text field for the number of files to get at once. */
	private JTextField get;

	/**
	 * Show the user the Download Options dialog box.
	 * 
	 * @param tab The DownloadTab in the main window
	 */
	public DownloadOptions() {

		// Make Java Swing objects for the dialog box, and all the controls inside
		dialog = Dialog.modal("Download Options");
		
		folder = new JTextField(); // Editable text boxes
		get = new JTextField();
		new TextMenu(folder); // Give the text boxes right-click menus that have Cut, Copy, and Paste
		new TextMenu(get);
		/*
		folder.setText(tab.folderSetting.toString()); // Load values from settings
		get.setText(tab.getSetting.toString());
		*/
		JButton browse = new JButton(new BrowseAction());
		JButton ok = new JButton(new OkAction());
		JButton cancel = new JButton(new CancelAction());
		
		// Lay out the text boxes and buttons in the dialog box
		Panel column = Panel.column().border(); // The column of the whole dialog box
		column.add(Cell.wrap(new JLabel("Choose the folder where the program saves files")));
		column.add(Cell.wrap(folder).fillWide());
		column.add(Cell.wrap(browse).upperRight());
		Panel row = Panel.row(); // The row that says "Get [ 3] files at once"
		row.add(Cell.wrap(new JLabel("Get")));
		row.add(Cell.wrap(get).width(30));
		row.add(Cell.wrap(new JLabel("files at once")));
		column.add(Cell.wrap(row.jpanel));
		row = Panel.row(); // The row that has the OK and Cancel buttons
		row.add(Cell.wrap(ok));
		row.add(Cell.wrap(cancel));
		column.add(Cell.wrap(row.jpanel).grow().lowerRight());
		dialog.setContentPane(column.jpanel); // Put everything we layed out in the dialog box

		// Show the dialog box to the user
		Dialog.show(dialog, 360, 210);
	}
	
	// -------- Methods Java calls when the user clicks --------

	// The user clicked the Browse button
	private class BrowseAction extends AbstractAction {
		public BrowseAction() { super("Browse..."); }
		public void actionPerformed(ActionEvent a) {

			// Show the choice box to the user, and set the path text
			Dialog.chooseFolder(dialog, folder);
		}
	}

	// The user clicked the OK button
	private class OkAction extends AbstractAction {
		public OkAction() { super("OK"); }
		public void actionPerformed(ActionEvent a) {

			// Save the settings
			/*
			tab.folderSetting.set(folder.getText()); // Only changes if text1 is a Path
			tab.getSetting.set(get.getText()); // Only changes if text2 is numerals like "1" or more
			*/

			// Remove the dialog box from the screen
			dialog.dispose();
		}
	}

	// The user clicked the Cancel button
	private class CancelAction extends AbstractAction {
		public CancelAction() { super("Cancel"); }
		public void actionPerformed(ActionEvent a) {
			
			// Remove the dialog box from the screen
			dialog.dispose();
		}
	}
}
