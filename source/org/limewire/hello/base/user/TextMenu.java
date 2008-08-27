package org.limewire.hello.base.user;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.desktop.Clipboard;

public class TextMenu {

	// -------- Make a TextMenu object to add a right-click menu to a Swing text component --------
	
	/** Add a working right-click menu with Cut, Copy, Paste, Delete and Select All to the given text component. */
	public TextMenu(JTextComponent component) {

		// Save the text component we'll decorate with our right-click menu of clipboard text commands
		this.component = component;
		component.addMouseListener(new MyMouseListener()); // Find out when the user right-clicks in the text component

		// Make the right-click menu
		cutItem = new JMenuItem("Cut"); // Make the menu items
		copyItem = new JMenuItem("Copy");
		pasteItem = new JMenuItem("Paste");
		deleteItem = new JMenuItem("Delete");
		selectAllItem = new JMenuItem("Select All");
		menu = new JPopupMenu(); // Make the menu
		menu.add(cutItem); // Add the menu items to the menu
		menu.add(copyItem);
		menu.add(pasteItem);
		menu.add(deleteItem);
		menu.addSeparator();
		menu.add(selectAllItem);
		MyActionListener listener = new MyActionListener(); // Make the object Java will call when the user clicks
		cutItem.addActionListener(listener); // Register the menu items with the object
		copyItem.addActionListener(listener);
		pasteItem.addActionListener(listener);
		deleteItem.addActionListener(listener);
		selectAllItem.addActionListener(listener);
	}

	/** The Swing text component this TextMenu is on, an object that extends JTextComponent, like a JTextArea or JTextField. */
	private JTextComponent component;

	/** The right-click menu of clipboard text commands. */
	private JPopupMenu menu;
	/** The items on the menu. */
	private JMenuItem cutItem, copyItem, pasteItem, deleteItem, selectAllItem;

	// -------- Methods Java calls when the user clicks --------

	// When the user clicks the mouse in the text component, Java will call these methods
	private class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) { show(e); }
		public void mouseReleased(MouseEvent e) { show(e); }
		
		// When we get the trigger event, if some rows are selected, show the menu
		private void show(MouseEvent e) {
			if (e.isPopupTrigger()) { // Only do something if this is the correct event

				// Disable all the menu items, we'll enable those that can work next
				cutItem.setEnabled(false);
				copyItem.setEnabled(false);
				pasteItem.setEnabled(false);
				deleteItem.setEnabled(false);
				selectAllItem.setEnabled(false);

				// Find out if our text component is editable or read-only, and if it has some selected text
				boolean editable = component.isEditable();
				boolean selection = Text.hasText(component.getSelectedText());

				// Enable Cut and Delete if the text component is editable and has selected text
				if (editable && selection) {
					cutItem.setEnabled(true);
					deleteItem.setEnabled(true);
				}

				// Enable Copy if the text component has selected text
				if (selection) copyItem.setEnabled(true);

				// Enable Paste if the text component is editable and there's text on the clipboard
				if (editable && Clipboard.hasText()) pasteItem.setEnabled(true);

				// Enable Select All of the text component has text
				if (Text.hasText(component.getText())) selectAllItem.setEnabled(true);

				// Show the menu to the user
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	// When the user clicks an item in the menu, Java calls this actionPerformed() method
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Get the selected text, and the text before and after it
			String s = component.getText();
			int i = component.getSelectionStart(); // The index where the selection starts
			int size = component.getSelectionEnd() - i; // The number of selected characters
			String before = Text.start(s, i);
			String selected = Text.clip(s, i, size);
			String after = Text.after(s, i + size);

			// The user clicked "Cut" on the menu
			if (e.getActionCommand().equals("Cut")) {
				Clipboard.copy(selected);
				component.setText(before + after); // Remove the selected text
				component.setCaretPosition(i);

			// The user clicked "Copy" on the menu
			} else if (e.getActionCommand().equals("Copy")) {
				Clipboard.copy(selected);

			// The user clicked "Paste" on the menu
			} else if (e.getActionCommand().equals("Paste")) {
				String clipboard = Clipboard.paste(); // Insert text from the clipboard
				component.setText(before + clipboard + after);
				component.setCaretPosition(i + clipboard.length());

			// The user clicked "Delete" on the menu
			} else if (e.getActionCommand().equals("Delete")) {
				component.setText(before + after); // Remove the selected text
				component.setCaretPosition(i);

			// The user clicked "Select All" on the menu
			} else if (e.getActionCommand().equals("Select All")) {
				component.selectAll();
			}
		}
	}
}
