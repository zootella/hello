package org.limewire.hello.spin.utility;

import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Lay out the contents of a dialog box or window on the screen. */
public class Panel {

	// Make
	
	/** Make a new Panel to arrange user interface components like buttons in a window. */
	public Panel() {
		jpanel = new JPanel(); // Make our JPanel object, and configure it for grid bag layout
		jpanel.setLayout(new GridBagLayout());
	}

	/** Make a new Panel to arrange user inteface components like buttons in a single horizontal row. */
	public static Panel row(Cell... cells) {
		Panel panel = new Panel();
		panel.horizontal = true;
		panel.add(cells); // If we were given any Cell objects with components inside, add them right from the start
		return panel;
	}

	/** Make a new Panel to arrange user inteface components like buttons in a single vertical column. */
	public static Panel column(Cell... cells) {
		Panel panel = new Panel();
		panel.horizontal = false;
		panel.add(cells);
		return panel;
	}

	/** Add a border around the inside edge of this Panel. */
	public Panel border() {
		jpanel.setBorder(BorderFactory.createEmptyBorder(space, space, space, space));
		return this; // Return a reference to this same Panel object so you can use border() in a chain
	}

	// Add

	/**
	 * Add another component, like a button, to this Panel.
	 * To add a JComponent like a JButton, first wrap it in a Cell object.
	 * You can give this method any number of Cell objects, like add(cell) or add(cell1, cell2, cell3).
	 * If this Panel is a row, it will add them on the right.
	 * If this Panel is a column, it will add them at the bottom.
	 */
	public void add(Cell... cells) {

		// Loop for each Cell object we've been given
		for (Cell cell : cells) {

			// Set the grid coordinate from the default of 0, 0
			if (horizontal) cell.constraints.gridx = n; // This Panel is a row, set gridx to 0, 1, 2, 3, and so on for each new component we add
			else            cell.constraints.gridy = n; // This Panel is a column, keep gridx 0 and count up gridy instead

			// Use an Inset to keep this new component from touching the last one we added
			if (n > 0 && horizontal)  cell.constraints.insets = new Insets(0, space, 0, 0); // To the left
			if (n > 0 && !horizontal) cell.constraints.insets = new Insets(space, 0, 0, 0); // Above

			// Add the given JComponent to our JPanel, positioning it with the GridBagConstraints it came with and that we adjusted
			jpanel.add(cell.component, cell.constraints);
			n++; // Count one more component to set gridx or gridy farther next time
		}
	}

	/**
	 * Place a component, like a button, in this Panel.
	 * x, y, w, and h are the grid bag layout coordinates to use, and width and height.
	 * t, l, b, and r are the top, left, bottom, and right insets in units of Window.space, 0 for no space, 1 for a single space.
	 * cell is a Cell object with the component inside.
	 */
	public void place(int x, int y, int w, int h, int t, int l, int b, int r, Cell cell) {
		
		// Load the given coordinates into the Cell object's GridBagConstraints.
		cell.constraints.gridx = x;
		cell.constraints.gridy = y;
		cell.constraints.gridwidth = w;
		cell.constraints.gridheight = h;
		cell.constraints.insets = new Insets(t * space, l * space, b * space, r * space);
		
		// Add the given JComponent to our JPanel, positioning it with the GridBagConstraints it came with and that we adjusted
		jpanel.add(cell.component, cell.constraints);
	}
	
	// Inside
	
	/** The Swing JPanel object that shows this Panel on the screen. */
	public JPanel jpanel;
	
	/** true if the components are in a horizontal row, false if they're in a vertical column. */
	private boolean horizontal;
	/** The number of components added to this Panel. */
	private int n;
	
	// Define
	
	/** 8 pixels, the space between buttons and the width of the margin at the edge. */
	public static final int space = 8;
}
