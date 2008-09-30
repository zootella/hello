package org.limewire.hello.base.user;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

public class Refresh {
	
	// Help

	/** Update the text of component to s if necessary. */
	public static void text(JTextComponent component, String s) {
		if (!component.getText().equals(s)) component.setText(s);
	}

	/** Update the text of label to s if necessary. */
	public static void text(JLabel label, String s) {
		if (!label.getText().equals(s)) label.setText(s);
	}
	
	/** Update the editable state of component to edit if necessary. */
	public static void edit(JTextComponent component, boolean edit) {
		if (component.isEditable() != edit) component.setEditable(edit);
	}

	/** Update the enabled state of action to can if necessary. */
	public static void can(Action action, boolean can) {
		if (action.isEnabled() != can) action.setEnabled(can);
	}

	/** Update the pressed state of button to down if necessary. */
	public static void press(JToggleButton button, boolean press) {
		if (button.isSelected() != press) button.setSelected(press);
	}
}
