package org.limewire.hello.base.state;

import org.limewire.hello.base.state.old.OldClose;


/** A View object shows a Model object to the user. */
public abstract class View extends OldClose {
	
	/** Model tells View to update, Model has changed so View needs to refresh. */
	public abstract void receive();
	/** Model tells View to close, Model is closed so View should disappear. */
	public abstract void close();
}
