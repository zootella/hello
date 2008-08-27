package org.limewire.hello.base.user;

import javax.swing.JTextArea;

/** A wrapping, read-only text area that lets the user select and copy. */
public class SelectTextArea extends JTextArea {
	
	public SelectTextArea() {
		this("");
	}

	public SelectTextArea(String s) {
		super(s);
		setLineWrap(true);
		setOpaque(false);
		setBorder(null);
		setEditable(false);
		new TextMenu(this);
	}
}
