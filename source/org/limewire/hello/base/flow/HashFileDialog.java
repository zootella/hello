package org.limewire.hello.base.flow;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.SelectTextArea;
import org.limewire.hello.base.user.TextMenu;

/** The Add Feed dialog box on the screen that lets the user add a new feed to the list. */
public class HashFileDialog extends Close {

	
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	HashFileDialog dialog = new HashFileDialog();
        		dialog.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Make closing the window close the program
            }
        });
    }
	
	
	
	
	
	public HashFileDialog() {

		path = new JTextField();
		new TextMenu(path);
		
		status = new SelectTextArea();
		value = new SelectTextArea();
		
		browse = new BrowseAction();
		start = new StartAction();
		pause = new JToggleButton("Pause");
		reset = new ResetAction();
		close = new CloseAction();

		Panel bar1 = Panel.row();
		bar1.add(Cell.wrap(path).fillWide());
		bar1.add(Cell.wrap(new JButton(browse)));
		
		Panel bar2 = Panel.row();
		bar2.add(Cell.wrap(new JButton(start)));
		bar2.add(Cell.wrap(pause));
		bar2.add(Cell.wrap(new JButton(reset)));
		bar2.add(Cell.wrap(new JButton(close)));

		// Lay them out
		Panel panel = new Panel();
		panel.border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("Path")));
		panel.place(0, 1, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Status")));
		panel.place(0, 2, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Value")));
		panel.place(1, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(bar1.jpanel).fillWide());
		panel.place(1, 1, 1, 1, 1, 1, 0, 0, Cell.wrap(status).fillWide());
		panel.place(1, 2, 1, 1, 1, 1, 0, 0, Cell.wrap(value).fillWide());
		panel.place(1, 3, 1, 1, 1, 1, 0, 0, Cell.wrap(bar2.jpanel).lowerLeft().grow());

		// Make the dialog box and show it on the screen
		dialog = Dialog.make("Hash File");
		dialog.setContentPane(panel.jpanel); // Put everything we layed out in the dialog box
		Dialog.show(dialog, 600, 180);
		
		
		dialog.addWindowListener(new MyWindowListener());         // Have Java tell us when the user closes the window
		
		
		/*
		status.setText("hello status");
		value.setText("hello value");
		*/
		
	}

	private final JDialog dialog;
	private final JTextField path;
	private final SelectTextArea status;
	private final SelectTextArea value;
	private final Action browse;
	private final Action start;
	private final JToggleButton pause;
	private final Action reset;
	private final Action close;
	
	private HashFile hash;
	
	
	
	
	
	
	
	
	
	
	

	// The user clicked the Browse button
	private class BrowseAction extends AbstractAction {
		public BrowseAction() { super("Browse"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

		}
	}
	
	// The user clicked the Start button
	private class StartAction extends AbstractAction {
		public StartAction() { super("Start"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

		}
	}
	
	// The user clicked the Pause button
	private class PauseAction extends AbstractAction {
		public PauseAction() { super("Pause"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

		}
	}
	
	// The user clicked the Reset button
	private class ResetAction extends AbstractAction {
		public ResetAction() { super("Reset"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {

		}
	}
	
	

	// Close
	
	// When the user clicks the dialog's corner X, Java calls this windowClosing() method and then takes the dialog off the screen
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			close();
		}
	}
	
	// The user clicked the Close button
	private class CloseAction extends AbstractAction {
		public CloseAction() { super("Close"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			close();
			dialog.dispose();
		}
	}

	/** Make this object put away resources and not change or work again. */
	@Override public void close() {
		if (already()) return;
		Close.close(hash); // Close our hash object, if we have one
	}
}
