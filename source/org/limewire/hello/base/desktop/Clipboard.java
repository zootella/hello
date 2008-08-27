package org.limewire.hello.base.desktop;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class Clipboard {
	
	// -------- Copy text to and paste text from the platform's clipboard --------
	
	/** Copy the given String onto the clipboard. */
	public static void copy(String s) {
		StringSelection selection = new StringSelection(s); // Wrap the given String into a StringSelection object
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		} catch (Exception e) {}
	}
	
	/** true if the clipboard has some text we could get with paste(). */
	public static boolean hasText() {
		DataFlavor[] flavors = Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors();
		for (DataFlavor flavor : flavors) // Loop through all the DataFlavor objects, looking for one that's text
			if (flavor.isFlavorTextType())
				return true;
		return false; // No text type found
	}

	/** Get the text currently on the clipboard, returns "" if there is none. */
	public static String paste() {
		String s = null;
		try {
			s = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e) {}
		if (s == null) s = ""; // Return "", not null
		return s;
	}
}
