package org.limewire.hello.base.user;

/** A View object shows a Model object to the user. */
public interface View {
	
	/** Model tells View to update, Model has changed so View needs to refresh. */
	public void update();
	/** Model tells View to close, Model is closed so View should disappear. */
	public void close();
}
