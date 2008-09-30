package org.limewire.hello.base.state;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** An object has a Model that extends this class to keep View objects above up to date. */
public abstract class Model extends Close {

	// Required

	/** View gets current text to show the user from Model. */
	public abstract Map<String, String> view();

	/** The outer object that made and contains this Model. */
	public abstract Object out();

	// Core

	/** Setup the core of this new object that extends Model. */
	public Model() {
		delay = new Delay(new MyReceive());
		views = new HashSet<View>();
	}
	
	/** Our Delay that keeps us from updating the screen so frequently the whole program would slow down. */
	private final Delay delay;
	/** Our list of View objects above viewing us. */
	private final Set<View> views;
	
	/** The object this Model is a part of is closed, have Model tell all the views above to close. */
	public void close() {
		if (already()) return;
		delay.close();
		Set<View> copy = new HashSet<View>(); // Copy the Set so we can change the original
		copy.addAll(views);
		for (View view : copy)
			view.vanish(); // This will remove the view from views
		views.clear();     // It should be empty now, but clear it just to be sure
	}

	// Add and remove

	/** View tells Model to connect the two, afterwards Model will tell View when things change. */
	public void add(View view) { views.add(view); } // Add the View to our list
	/** View tells Model to disconnect the two, Model won't notify View anymore. */
	public void remove(View view) { views.remove(view); } // Remove the View from our list
	
	// Send and receive

	/** The object this Model is a part of has changed, have Model tell all the views above to update. */
	public void changed() { delay.send(); } // After the delay
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			for (View view : views)
				view.refresh(); // This Model has changed, tell all our views above
		}
	}
	
	// Help
	
	/** Turn o into a String, "" if null. */
	public static String toString(Object o) {
		if (o == null) return "";
		else return o.toString();
	}
}
