package org.limewire.hello.base.download.resume;

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
import javax.swing.SwingUtilities;

import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.View;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.Refresh;
import org.limewire.hello.base.user.SelectTextArea;
import org.limewire.hello.base.user.TextMenu;

/** A Download dialog on the screen that views a DownloadMachine below. */
public class GetDialogAdvanced extends Close {
	
	// Program

	// Run just this dialog box as the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	GetDialogAdvanced dialog = new GetDialogAdvanced();
        		dialog.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Make closing the window close the program
            }
        });
    }
    
    // Dialog

    /** Show the Download dialog on the screen to let the user download a file. */
	public GetDialogAdvanced() {

		// Make dialog contents
		address = new JTextField(); // Path box
		new TextMenu(address);
		status = new SelectTextArea(); // Status text
		name = new SelectTextArea();
		size = new SelectTextArea();
		type = new SelectTextArea();
		savedTo = new SelectTextArea();
		enter = new EnterAction(); // Actions behind buttons
		get = new GetAction();
		pause = new PauseAction();
		open = new OpenAction();
		openSavedFile = new OpenSavedFileAction();
		openContainingFolder = new OpenContainingFolderAction();
		reset = new ResetAction();
		delete = new DeleteAction();
		remove = new RemoveAction();
		close = new CloseAction();

		// Lay them out
		Panel bar1 = Panel.row();
		bar1.add(Cell.wrap(address).fillWide());
		bar1.add(Cell.wrap(new JButton(enter)));
		Panel bar2 = Panel.row();
		bar2.add(Cell.wrap(new JButton(get)));
		bar2.add(Cell.wrap(new JButton(pause)));
		Panel bar3 = Panel.row();
		bar3.add(Cell.wrap(new JButton(open)));
		bar3.add(Cell.wrap(new JButton(openSavedFile)));
		bar3.add(Cell.wrap(new JButton(openContainingFolder)));
		Panel bar4 = Panel.row();
		bar4.add(Cell.wrap(new JButton(reset)));
		bar4.add(Cell.wrap(new JButton(delete)));
		bar4.add(Cell.wrap(new JButton(remove)));
		bar4.add(Cell.wrap(new JButton(close)));

		Panel panel = new Panel();
		panel.border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("Address")));
		panel.place(0, 1, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Status")));
		panel.place(0, 2, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Name")));
		panel.place(0, 3, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Size")));
		panel.place(0, 4, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Type")));
		panel.place(0, 5, 1, 1, 1, 0, 0, 0, Cell.wrap(new JLabel("Saved To")));
		panel.place(1, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(bar1.jpanel).fillWide());
		panel.place(1, 1, 1, 1, 1, 1, 0, 0, Cell.wrap(status).fillWide());
		panel.place(1, 2, 1, 1, 1, 1, 0, 0, Cell.wrap(name).fillWide());
		panel.place(1, 3, 1, 1, 1, 1, 0, 0, Cell.wrap(size).fillWide());
		panel.place(1, 4, 1, 1, 1, 1, 0, 0, Cell.wrap(type).fillWide());
		panel.place(1, 5, 1, 1, 1, 1, 0, 0, Cell.wrap(savedTo).fillWide());
		panel.place(1, 6, 1, 1, 1, 1, 0, 0, Cell.wrap(bar2.jpanel).lowerLeft().grow());
		panel.place(1, 7, 1, 1, 1, 1, 0, 0, Cell.wrap(bar3.jpanel));
		panel.place(1, 8, 1, 1, 1, 1, 0, 0, Cell.wrap(bar4.jpanel));

		// Make our DownloadMachine that will do what this dialog shows
		download = new GetMachineAdvanced(null);

		// Make our inner View object and connect the Model below to it
		view = new MyView();
		download.model.add(view); // When the Feed Model changes, it will call our view.refresh() method
		view.refresh();

		// Make the dialog box and show it on the screen
		dialog = Dialog.make("Download");
		dialog.setContentPane(panel.jpanel); // Put everything we layed out in the dialog box
		dialog.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		Dialog.show(dialog, 650, 320);
	}
	
	/** The object below with a Model this dialog is a View of. */
	private final GetMachineAdvanced download;

	private final JDialog dialog;
	private final JTextField address;
	private final SelectTextArea status, name, size, type, savedTo;
	private final Action enter, get, pause, open, openSavedFile, openContainingFolder, reset, delete, remove, close;

	/** Make this object put away resources and not change or work again. */
	public void close() {
		if (already()) return;
		dialog.dispose();
		download.close();
	}
	
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
		}
	}

	// The user clicked a button
	private class EnterAction extends AbstractAction {
		public EnterAction() { super("Enter"); }
		public void actionPerformed(ActionEvent a) { /*download.enter(address.getText());*/ }
	}
	private class GetAction extends AbstractAction {
		public GetAction() { super("Get"); }
		public void actionPerformed(ActionEvent a) { download.get(); }
	}
	private class PauseAction extends AbstractAction {
		public PauseAction() { super("Pause"); }
		public void actionPerformed(ActionEvent a) { download.pause(); }
	}
	private class OpenAction extends AbstractAction {
		public OpenAction() { super("Open"); }
		public void actionPerformed(ActionEvent a) { download.open(); }
	}
	private class OpenSavedFileAction extends AbstractAction {
		public OpenSavedFileAction() { super("Open Saved File"); }
		public void actionPerformed(ActionEvent a) { download.openSavedFile(); }
	}
	private class OpenContainingFolderAction extends AbstractAction {
		public OpenContainingFolderAction() { super("Open Containing Folder"); }
		public void actionPerformed(ActionEvent a) { download.openContainingFolder(); }
	}
	private class ResetAction extends AbstractAction {
		public ResetAction() { super("Reset"); }
		public void actionPerformed(ActionEvent a) { /*download.reset();*/ }
	}
	private class DeleteAction extends AbstractAction {
		public DeleteAction() { super("Delete"); }
		public void actionPerformed(ActionEvent a) { download.delete(); }
	}
	private class RemoveAction extends AbstractAction {
		public RemoveAction() { super("Remove"); }
		public void actionPerformed(ActionEvent a) { /*download.remove();*/ }
	}

	// View

	// When our Model underneath changes, it calls these methods
	private final View view;
	private class MyView implements View {

		// The Model beneath changed, we need to update what we show the user
		public void refresh() {
			Refresh.text(status, download.model.status());
			Refresh.text(name, download.model.name());
			Refresh.text(size, download.model.size());
			Refresh.text(type, download.model.type());
			Refresh.text(savedTo, download.model.savedTo());
			
			/*
			Refresh.edit(address, download.model.canEnter());
			Refresh.can(enter, download.model.canEnter());
			*/
			
			Refresh.can(get, download.model.canGet());
			Refresh.can(pause, download.model.canPause());
			Refresh.can(open, download.model.canOpen());
			Refresh.can(openSavedFile, download.model.canOpenSavedFile());
			Refresh.can(openContainingFolder, download.model.canOpenContainingFolder());
			/*
			Refresh.can(reset, download.model.canReset());
			*/
			Refresh.can(delete, download.model.canDelete());
		}

		// The Model beneath closed, take this View off the screen
		public void vanish() { me().close(); }
	}
	
	/** Give inner classes a link to this outer object. */
	private GetDialogAdvanced me() { return this; }
}
