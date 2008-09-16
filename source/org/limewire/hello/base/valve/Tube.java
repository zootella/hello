package org.limewire.hello.base.valve;

import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import org.limewire.hello.base.data.Bay;
import org.limewire.hello.base.data.Bin;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.state.Receive;
import org.limewire.hello.base.state.Update;

/** A Tube is a series of Valve objects that data moves through. */
public class Tube extends Close {
	
	// Gallery
	
	public static Tube upload(Update above, SocketChannel socket) {
		Tube tube = new Tube(above);
		tube.add(new UploadValve(tube.update, socket));
		tube.added();
		return tube;
	}
	
	public static Tube compressUpload(Update above, SocketChannel socket) {
		Tube tube = new Tube(above);
		tube.add(new MoveValve(tube.update)); // TODO change to CompressValve
		tube.add(new UploadValve(tube.update, socket));
		tube.added();
		return tube;
	}
	
	
	public static Tube download(Update above, SocketChannel socket) {
		Tube tube = new Tube(above);
		tube.add(new DownloadValve(tube.update, socket));
		tube.added();
		return tube;
	}
	
	// get rid of MoveValve and MoveLater, you have other better simple examples, seriously
	// write ReadValve and HashValve, have a dialog box that hashes files, see how fast it goes
	
	
	
	
	// Make
	
	private Tube(Update above) {
		this.above = above;
		update = new Update(new MyReceive());
		list = new LinkedList<Valve>();
	}
	
	private void add(Valve valve) {
		list.add(valve);
	}
	
	private void added() {
		if (first().in() != null)
			in = new Bay();
		if (last().out() != null)
			out = Bin.medium();
	}
	
	private List<Valve> list;
	
	private final Update above;
	/** The list's Update object which all the valves in it notify when they've changed. */
	private final Update update;
	
	
	public void close() {
		if (already()) return;
		
		// Close our objects
		for (Valve valve : list)
			valve.close();
		
		above.send();
	}
	
	/** true when this object is closed and won't work anymore. */
	public boolean closed() { return closed; }
	private boolean closed;
	/** The exception that closed us, if any. */
	public Exception exception() { return exception; }
	private Exception exception;
	
	// Input and output
	

	// change so you don't have to remember to call go

	public boolean permission() {
		if (in == null || cap) throw new IllegalStateException();
		return in.size() < Bin.medium;
	}
	public void in(Data data) {
		if (in == null || cap) throw new IllegalStateException();
		in.add(data);
	}
	
	public Data out() {
		if (out == null) throw new IllegalStateException();
		return null;
	}
	public void remove(int size) {
		if (out == null) throw new IllegalStateException();
	}
	
	
	
	// how to drain a tube
	// first, call cap(), this will close the first() Valve if it's producing data, and prevent callers from adding more
	// then, call out() and remove() to drain all the data out, or just wait if we're uploading
	// call isEmpty(), when it returns true, you can take your socket and use it in a new Tube
	
	public void cap() {
		
	}
	public boolean isCapped() { return cap; }
	private boolean cap;
	

	/** true if this ValveList is completely empty of data. */
	public boolean isEmpty() {
		return false;
	}
	
	
	
	/** The list's original source of data. */
	private Bay in;
	/** The list's destination for the data it finishes processing. */
	private Bin out;
	
	private class MyReceive implements Receive {
		public void receive() {
			if (closed) return;
			try {
				
				// Stop each valve that doesn't have a later working on its bins
				for (Valve valve : list)
					valve.stop(); // Throws an exception if one stopped it
				
				// Move data down the list, end to start
				move(last().out(), out);           // Take from the last in the list
				for (Pair pair : Pair.pairs(list)) // Move data down the list
					move(((Valve)pair.a).out(), ((Valve)pair.b).in());
				move(in, first().in());            // Give to the first in the list
				
				// Start each valve that has data and space
				for (Valve valve : list)
					valve.start();

			} catch (Exception e) { exception = e; close(); }
		}
	}
	
	
	
	
	
	
	
	
	// Help

	/** The first Valve in list. */
	private Valve first() { return list.get(list.size() - 1); }
	/** The last Valve in list. */
	private Valve last() { return list.get(0); }

	/** Move data from source to destination, do nothing if either are null. */
	public static void move(Bay source, Bin destination) {
		if (source == null || destination == null) return;
		destination.add(source); // Move data from the Bay to the Bin
	}
	/** Move data from source to destination, do nothing if either are null. */
	public static void move(Bin source, Bin destination) {
		if (source == null || destination == null) return;
		destination.add(source); // Move data from the source Bin to the destination Bin
	}
	
	
	
	
	
	
	
	
	
}
