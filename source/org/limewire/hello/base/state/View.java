package org.limewire.hello.base.state;

/** A View object shows a Model object to the user. */
public interface View {
	
	// Required
	
	/** Model tells View to update, Model has changed so View needs to refresh. */
	public void refresh();
	
	/** Model tells View to close, Model is closed so View should disappear. */
	public void vanish();
}
