package org.limewire.hello.spin.utility;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.limewire.hello.spin.InvokeSpin;
import org.limewire.hello.spin.RunSpin;
import org.limewire.hello.spin.WorkSpin;

/** The program's main window. */
public class MenuDialog extends Close {
	
	// Program

	// Run just this dialog box as the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	MenuDialog dialog = new MenuDialog();
        		dialog.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Make closing the window close the program
            }
        });
    }
    
    // Dialog

	public MenuDialog() {

		// Make dialog contents and lay them out
		Panel bar = Panel.row();
		bar.add(Cell.wrap(new JButton(new InvokeAction())));
		bar.add(Cell.wrap(new JButton(new RunAction())));
		bar.add(Cell.wrap(new JButton(new WorkAction())));
		Panel panel = new Panel();
		panel.border();
		panel.add(Cell.wrap(bar.jpanel).grow());

		// Make the dialog box and show it on the screen
		dialog = Dialog.make("Spin");
		dialog.setContentPane(panel.jpanel); // Put everything we layed out in the dialog box
		dialog.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		Dialog.show(dialog, 500, 100);
	}
	
	/** The dialog box on the screen. */
	private final JDialog dialog;

	/** Make this object put away resources and not change or work again. */
	public void close() {
		if (already()) return;
		dialog.dispose();
	}
	
	// When the user clicks the dialog's corner X, Java calls this windowClosing() method and then takes the dialog off the screen
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			close();
		}
	}
	
	// The user clicked a button
	private class InvokeAction extends AbstractAction {
		public InvokeAction() { super("Invoke"); }
		public void actionPerformed(ActionEvent a) { new SpinDialog(new InvokeSpin()); }
	}
	private class RunAction extends AbstractAction {
		public RunAction() { super("Run"); }
		public void actionPerformed(ActionEvent a) { new SpinDialog(new RunSpin()); }
	}
	private class WorkAction extends AbstractAction {
		public WorkAction() { super("Work"); }
		public void actionPerformed(ActionEvent a) { new SpinDialog(new WorkSpin()); }
	}
}
