package org.limewire.hello.all.user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.limewire.hello.all.Snippet;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.TextMenu;

public class StatusTab {

	// -------- The Status tab, and its parts --------

	/** Make the program's StatusTab object, which appears as the "Status" tab in the program window. */
	public StatusTab() {

		// Make a MyActionListener object that has the methods Java will call when the user clicks
	    MyActionListener listener = new MyActionListener();

	    // Make the Snippet button
		JButton snippet = new JButton("Snippet");
		snippet.addActionListener(listener);

		// Make the output text area
		output = new JTextArea();
		output.setEditable(false); // Make it read-only
		new TextMenu(output); // Give it a right-click menu of clipboard commands
		JScrollPane scroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Make the command text box
		command = new JTextField();
		new TextMenu(command); // Give it a right-click menu of clipboard commands
		command.setActionCommand("Command"); // The user won't see the name "Command", but we need to set a name for actionPerformed() below
		command.addActionListener(listener);

		// Make the Enter text box
		JButton enter = new JButton("Enter");
		enter.addActionListener(listener);

		// Lay out the text boxes and button on a Panel, which will appear in the tab
		panel = new Panel();
		panel.border();
		panel.place(0, 0, 2, 1, 0, 0, 0, 0, Cell.wrap(snippet));
		panel.place(0, 1, 2, 1, 1, 0, 1, 0, Cell.wrap(scroll).fill());
		panel.place(0, 2, 1, 1, 0, 0, 0, 0, Cell.wrap(command).fillWide());
		panel.place(1, 2, 1, 1, 0, 1, 0, 0, Cell.wrap(enter));
	}
	
	/** Get the Swing JComponent that is the user interface of this tab. */
	public JComponent component() {
		return panel.jpanel; // It's the JPanel in our Panel object, which is a JComponent
	}
	
	/** The Swing JTextArea object which is the large text box in the center that prints the program's output. */
	private JTextArea output;
	/** The Swing JTextField object which is the single-line text box at the bottom where the user can type a command. */
	private JTextField command;
	/** A Panel object which contains the Swing JPanel that holds all the user interface components. */
	private Panel panel;

	// -------- Methods Java calls when the user clicks --------

	// When the user clicks something in the Status tab, Java calls this actionPerformed() method
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// The user clicked the Snippet button
			if (e.getActionCommand().equals("Snippet")) {
				Snippet.snippet(); // Call the program's snippet method to run the snippet of code there
				
			// The user hit the Enter key in the text box at the bottom, which we named "Command"
			} else if (e.getActionCommand().equals("Command")) {
				enter(); // Get and process the command the user typed

			// The user clicked the Enter button
			} else if (e.getActionCommand().equals("Enter")) {
				enter(); // Get and process the command the user typed
			}
		}
	}
	
	// -------- Process the user's command --------

	/** Process the text command the user typed in the command box at the bottom of the tab. */
    private void enter() {

    	// Select all the text and get it
    	command.selectAll();
    	String s = command.getText();
    	String response = s; // If code below doesn't change response, echo the command

    	// Look for the command "snippet"
    	if (s.equals("snippet")) {

    		// Run the snippet method, and set the response text
    		Snippet.snippet();
    		response = "ran snippet";
    	}

    	// Print out the command's response
    	report(response);
    }

	// -------- Report --------
    
    /** Print a line of text onto the Status tab. */
    public void report(String s) {
        output.append(s + "\n"); // Add the given text and a newline to the end of what's already in the output box
        output.setCaretPosition(output.getDocument().getLength()); // Scroll to the bottom
    }
}
