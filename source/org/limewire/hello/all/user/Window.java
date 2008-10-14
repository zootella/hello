package org.limewire.hello.all.user;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.limewire.hello.all.Program;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.bittorrent.user.BitTorrentTab;
import org.limewire.hello.download.user.DownloadTab;
import org.limewire.hello.feed.user.FeedTab;

public class Window {

	// -------- The program's Window, and its parts --------

	/**
	 * Make the Window object, which is the program's window on the screen.
	 * Calling this constructor shows the window on the screen.
	 * 
	 * @param program A link up to the Program object this new Window object is a part of
	 */
	public Window(Program program) {
		
		// Save the link back up to the Program object
		this.program = program;

		// Make the objects that represent the tabs in the window
    	feed = new FeedTab(this, program.feed);
		status = new StatusTab();
		download = new DownloadTab(program.download, this, program.web);
		bittorrent = new BitTorrentTab(this, program.bitTorrent);

		// Make a row of tabs, and add the tabs to it
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Feed", feed.component());
		tabs.addTab("Status", status.component());
		tabs.addTab("Download", download.component());
		tabs.addTab("BitTorrent", bittorrent.component());

		// Choose how big the window will be, and where it will appear on the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();          // Screen resolution in pixels
		Dimension size = new Dimension(screen.width * 3 / 4, screen.height / 2); // Window size
		Point location = Dialog.position(size);                                  // Random location

		// Make the program's window, configure it, and show it
		frame = new JFrame();                                    // Make the Swing JFrame object which is the program's main window on the screen
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Make closing the window close the program
		frame.addWindowListener(new MyWindowListener());         // Have Java tell us when the user closes the window
		frame.setTitle(Program.title);                           // Set the text in the window's title bar
		frame.setSize(size);                                     // Set the window's size and location from what we calculated above
		frame.setLocation(location);
		frame.setContentPane(tabs);                              // Put the tabs in the window
		frame.setVisible(true);                                  // Show the window on the screen
	}

	/** A link up to the Program object, which represents the whole running program. */
	private Program program;

	/** The Swing JFrame object which is the main window on the screen. */
	public JFrame frame;
	/** The Status tab in the window. */
	public StatusTab status;
	/** The Download tab in the window. */
	public DownloadTab download;
	
	public BitTorrentTab bittorrent;
	public FeedTab feed;

	// -------- Close --------

	// When the user clicks the main window's corner X, Java closes the program and calls this windowClosing() method
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {

			// Close all the objects in the program that need to put things away before the program closes
			program.close();
		}
	}

	/** Have the tabs save their data to the Store object's Outline. */
	public void close() {
		download.close(); // Have the DownloadTab save its download list
	}
	
	//TODO Move this to Dialog
	// -------- Factory settings --------

	/** 8 pixels, the space between buttons and the width of the margin at the edge. */
	public static final int space = 8;
	/** 80 pixels, the width and height of a big button. */
	public static final int button = 80;
}
