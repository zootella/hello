package org.limewire.hello.spin.utility;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JFrame;

/** Show dialog boxes on the screen. */
public class Dialog {

	// Make

	/** Make a new non-modal JDialog object with the given title. */
	public static JDialog make(String title) {
		JFrame frame = null;
		return new JDialog(frame, title, false); // false to make a non-modal dialog
	}
	
	/** Make a new modal JDialog object with the given title. */
	public static JDialog modal(String title) {
		JFrame frame = null;
		return new JDialog(frame, title, true); // true to make a modal dialog
	}
	
	// Show
	
	/** Show the given dialog box on the screen with the given width and height. */
	public static void show(JDialog dialog, int width, int height) {
		dialog.setSize(width, height);                              // Set the dialog's size in pixels
		dialog.setLocation(position(new Dimension(width, height))); // Choose random location
		dialog.setVisible(true);                                    // Show the dialog box on the screen
	}

	// Position
	
	/** Given a window width and height, choose a random position for it on the screen. */
	public static Point position(Dimension window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // Screen resolution in pixels
		return new Point(position(screen.width, window.width), position(screen.height, window.height));
	}
	
	/** Given a screen and window width or height, choose a random position for the window. */
	private static int position(int screen, int window) {
		if (window > screen)                               // Window larger than screen
			return 0;                                      // Position on the left or top edge
		else if ((2 * window) > screen)                    // Window larger than screen half
			return random((screen - window));              // Position randomly on screen
		else                                               // Window screen half or smaller
			return (screen / 2) - window + random(window); // Position randomly in center
	}
	
	/** Generate a random number 0 through size, clustered around size / 2. */
	private static int random(int size) {

		// Make the random number generator if we don't already have it
		if (random == null) random = new Random();
		
		// Calculate the midpoint into size our results will cluster around
		int half = size / 2;
		
		// Calculate the upper bound
		int bound = half; // Start at half
		while (random.nextBoolean()) bound /= 2; // Make it smaller
		if (bound < 1) return half; // To small, reached center

		// Pick a random number within that bound
		int i = random.nextInt(bound);
		if (i > half) i = half; // Too big somehow

		// Go that distance to the left or right of the midpoint
		if (random.nextBoolean()) return half + i;
		else                      return half - i;
	}

	/** A random number generator random() makes the first time it runs. */
	private static Random random;
}
