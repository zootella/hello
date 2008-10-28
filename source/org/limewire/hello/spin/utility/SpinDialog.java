package org.limewire.hello.spin.utility;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.Timer;

/** A dialog box that shows how fast something is spinning. */
public class SpinDialog extends Close {

	/** Make a dialog box that shows spin on the screen. */
	public SpinDialog(Spin spin) {
		
		// Save the given Spin object we're viewing
		this.spin = spin;
		
		// Make our Timer to update status text 5 times a second
		timer = new Timer(200, new MyActionListener());
		timer.setRepeats(true);
		timer.start();

		// Make dialog contents and lay them out
		status = new JTextArea(); // Status text
		status.setLineWrap(true);
		status.setOpaque(false);
		status.setBorder(null);
		status.setEditable(false);
		Panel panel = Panel.column().border();
		panel.add(Cell.wrap(status).fillWide().grow());

		// Make the dialog box and show it on the screen
		dialog = Dialog.make(spin.title());
		dialog.setContentPane(panel.jpanel); // Put the panel in the dialog box
		dialog.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		Dialog.show(dialog, 500, 100);
	}

	/** The dialog box on the screen. */
	private final JDialog dialog;
	/** The status text in the dialog. */
	private final JTextArea status;
	/** The Spin object below this dialog is viewing. */
	private final Spin spin;
	/** Our Timer that repeats 5 times a second to update our status text. */
	private Timer timer;

	/** Make this object put away resources and not change or work again. */
	public void close() {
		if (already()) return;
		dialog.dispose();
		timer.stop(); // Stop timer, keeping it running might prevent the program from closing
		spin.close(); // Close the Spin object beneath us
	}
	
	// When the user clicks the dialog's corner X, Java calls this windowClosing() method and then takes the dialog off the screen
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			close();
		}
	}

	// When timer goes off, Java calls this actionPerformed() method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (closed()) return;
			
			// Update text that tells the user how fast we're spinning right now
			String s = Describe.commas(spin.speed().speed()) + " per second";
			if (!status.getText().equals(s))
				status.setText(s);
		}
	}
}
