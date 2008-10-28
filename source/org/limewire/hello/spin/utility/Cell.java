package org.limewire.hello.spin.utility;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;

/** Wrap a component into a Cell to add it to a Panel. */
public class Cell {

	// Wrap
	
	/**
	 * Wrap a component, like a button or a label, into a new Cell object.
	 * It will have default position and resizing settings.
	 * Once your component is inside a Cell, you can add it to a Panel.
	 * 
	 * @param component A JComponent object like a JButton or a JLabel
	 * @return          A new Cell object with the component inside
	 */
	public static Cell wrap(JComponent component) {
		
		// Make a new Cell object to return, and keep the given JComponent inside
		Cell cell = new Cell();
		cell.component = component;
		
		// Make a new GridBagConstraints object, and fill it with our defaults
		cell.constraints = new GridBagConstraints();
		cell.constraints.gridx = 0; // Panel.add() will change gridx or gridy to put us in the right place in the grid
		cell.constraints.gridy = 0;
		cell.constraints.gridwidth = 1; // Don't span grid lines, use Panel.place() if you want to
		cell.constraints.gridheight = 1;
		cell.constraints.weightx = 0.0; // Don't grow when the JPanel does
		cell.constraints.weighty = 0.0;
		cell.constraints.anchor = GridBagConstraints.NORTHWEST; // Anchor the component to the upper left corner
		cell.constraints.fill = GridBagConstraints.NONE; // Don't expand to fill this Cell
		return cell;
	}
	
	/** The Java Swing JComponent in this Cell, like a JButton or JLabel. */
	public JComponent component;
	
	/** Settings for how the component is positioned in this Cell, and how this Cell will act when we add it to a JPanel. */
	public GridBagConstraints constraints;
	
	// Stretch
	
	/** Have the component in this Cell grow wider when the Panel it's in gets wider. */
	public Cell fillWide() {
		growWide();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		return this; // Return a reference to this same object so you can use this method in a chain
	}
	
	/** Have the component in this Cell grow taller when the Panel it's in gets taller. */
	public Cell fillTall() {
		growTall();
		constraints.fill = GridBagConstraints.VERTICAL;
		return this;
	}
	
	/** Have the component in this Cell grow wider and taller when the Panel it's in gets wider and taller. */
	public Cell fill() {
		grow();
		constraints.fill = GridBagConstraints.BOTH;
		return this;
	}

	/** Have this Cell grow wider when the Panel it's in gets wider, but don't stretch the component. */
	public Cell growWide() {
		constraints.weightx = 1.0;
		return this;
	}

	/** Have this Cell grow taller when the Panel it's in gets taller, but don't stretch the component. */
	public Cell growTall() {
		constraints.weighty = 1.0;
		return this;
	}
	
	/** Have this Cell grow wider and taller when the Panel it's in gets wider and taller, but don't stretch the component. */
	public Cell grow() {
		growWide();
		growTall();
		return this;
	}
	
	/** Anchor the component in this Cell to the upper right corner. */
	public Cell upperRight() {
		constraints.anchor = GridBagConstraints.NORTHEAST;
		return this;
	}

	/** Anchor the component in this Cell to the lower left corner. */
	public Cell lowerLeft() {
		constraints.anchor = GridBagConstraints.SOUTHWEST;
		return this;
	}

	/** Anchor the component in this Cell to the lower right corner. */
	public Cell lowerRight() {
		constraints.anchor = GridBagConstraints.SOUTHEAST;
		return this;
	}

	/** Tell the component inside this Cell how wide it should try to be. */
	public Cell width(int w) {
		Dimension d = component.getPreferredSize(); // Find out how wide and tall it is already
		d.width = w;                                // Change the width and set it, leaving its height the same
		component.setPreferredSize(d);
		return this;
	}
}
