package org.limewire.hello.all;

import javax.swing.SwingUtilities;

public class Main {

	// -------- The Main method and the Program object --------

	// When the program runs, Java calls this main() method
    public static void main(String[] args) {

    	// Have the normal Swing thread call this run() method
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

            	// Make the Program object which represents the whole program, and save it
            	program = new Program();
            }
        });
    }

    /** The Program object that represents the whole program. */
    public static Program program;

	// -------- Methods --------

    /** Record a line of text to document how the program is running. */
    public static void report(String s) {
    	System.out.println(s); // Display it in Eclipse
		if (program != null) program.window.status.report(s); // Also put it on the Status tab
    }

    //TODO this will go away when you kill pulse in place of update
    /** Make the next pulse happen soon, instead of after the normal delay. */
    public static void soon() {
    	if (program != null) program.pulse.soon(); // Make sure we have the Program object first
    }
}
