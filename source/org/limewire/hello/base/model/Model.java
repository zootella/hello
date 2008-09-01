package org.limewire.hello.base.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.limewire.hello.base.state.Close;

/** An object has a Model that extends this one to connect to View objects above and keep them up to date. */
public abstract class Model extends Close {
	
	/** The outer object that made and contains this Model. */
	public abstract Object out();

	/** Setup the Model core of this new extended object. */
	public Model() { views = new HashSet<View>(); } // Make our list of views
	/** Our list of View objects above viewing us. */
	private Set<View> views;

	/** View tells Model to connect the two, afterwards Model will call View when things change. */
	public void add(View view) { views.add(view); } // Add the View to our list
	/** View tells Model to disconnect the two, Model won't call View anymore. */
	public void remove(View view) { views.remove(view); } // Remove the View from our list

	/** The object this Model is a part of has changed, have Model tell all the views above to update. */
	public void send() { for (View view : views) view.receive(); } // This Model has changed, tell all our views above
	/** The object this Model is a part of is closed, have Model tell all the views above to close. */
	public void close() {                     // This Model is closed, tell all our views above
		Set<View> copy = new HashSet<View>(); // Copy the Set so we can change the original
		copy.addAll(views);
		for (View view : copy)
			view.close();                     // This will remove the view from views
		views.clear();                        // It should be empty now, but clear it just to be sure
		count();                              // Count that we closed this object
	}

	/** View gets current text to show the user from Model. */
	public abstract Map<String, String> view();
}
