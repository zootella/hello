package org.limewire.hello.base.state.old;

// return a State object to communicate what you're doing, or how you ended
// all the information about your state is organized into a single convenient State object
// extends exception, so you can throw a state to get out of something to close an object with that closed outcome
public class OldState extends Exception { // Methods that throw State have to declare it

	// -------- Make and return a State object to report your state --------
	
	/** Make a new State object to report the given state. */
	public OldState(int state) {
		this.state = state;
		this.exception = null;
		this.user = null;
	}

	/** Make a new State object to report the given state, with a short message for the user about what happened. */
	public OldState(int state, String user) {
		this.state = state;
		this.exception = null;
		this.user = user;
	}

	/** Make a new State object to report the given state, with the exception Java threw us. */
	public OldState(int state, Exception exception) {
		this.state = state;
		this.exception = exception;
		this.user = null;
	}

	// -------- Parts of a State object --------

	/** The state the object is currently in. */
	public final int state;

	/** If Java threw the object an exception that closed it, here it is. */
	public final Exception exception;
	
	/** A short descriptive message for the user. */
	public final String user;
	
	// -------- An object can be in any one of these different states --------

	// Pending states
	
	/** The object is paused, listed but not doing anything, and the program will have to start it. */
	public static final int paused = 0;
	public static OldState paused() { return new OldState(paused); }
	public boolean isPaused() { return state == paused; }

	/** The object is waiting in line before attempting something, and will start all by itself as soon as it can. */
	public static final int queued = 1;
	public static OldState queued() { return new OldState(queued); }
	public boolean isQueued() { return state == queued; }

	/** Determine if the object hasn't started yet, and is waiting for the program to start it or waiting in line. */
	public boolean isPending() {
		if (state == OldState.paused ||
			state == OldState.queued) return true;
		else return false;
	}

	// Active operations
	
	/**
	 * The object is performing a task that it must complete before doing its main job.
	 * What the object is opening depends on what kind of object it is.
	 * For instance, it might be connecting a TCP socket to a remote IP address.
	 */
	public static final int opening = 2;
	public static OldState opening() { return new OldState(opening); }
	public boolean isOpening() { return state == opening; }
	
	/**
	 * The object is carrying out its main job.
	 * What the object is doing depends on what kind of object it is.
	 * For instance, it might be transferring data through a TCP socket connection.
	 * It might be saving data to disk, hashing data, or doing a DNS lookup.
	 */
	public static final int doing = 3;
	public static OldState doing() { return new OldState(doing); }
	public boolean isDoing() { return state == doing; }

	/** Determine if the object is performing some operation right now, making network and disk activity. */
	public boolean isActive() {
		if (state == OldState.opening ||
			state == OldState.doing) return true;
		else return false;
	}

	// Closed outcomes
	
	/** The program closed the object because it didn't need it anymore. */
	public static final int cancelled = 4;
	public static OldState cancelled() { return new OldState(cancelled); }
	public boolean isCancelled() { return state == cancelled; }
	
	/** The object closed itself because it successfully completed its task. */
	public static final int completed = 5;
	public static OldState completed() { return new OldState(completed); }
	public boolean isCompleted() { return state == completed; }
	
	/** The object closed itself because it determined that it could not complete successfully, and had to give up. */
	public static final int couldNot = 6;
	public static OldState couldNot() { return new OldState(couldNot); }
	public boolean isCouldNot() { return state == couldNot; }
	
	/** The object closed itself because it took too long without finishing or making any progress. */
	public static final int timedOut = 7;
	public static OldState timedOut() { return new OldState(timedOut); }
	public boolean isTimedOut() { return state == timedOut; }
	
	/** The object closed itself because Java threw it an exception when it tried to do something with a socket. */
	public static final int socketException = 8;
	public static OldState socketException() { return new OldState(socketException); }
	public static OldState socketException(Exception e) { return new OldState(socketException, e); }
	public boolean isSocketException() { return state == socketException; }
	//TODO rename netError
	
	/** The object closed itself because Java threw it an exception when it tried to do something with a file. */
	public static final int fileException = 9;
	public static OldState fileException() { return new OldState(fileException); }
	public static OldState fileException(Exception e) { return new OldState(fileException, e); }
	public boolean isFileException() { return state == fileException; }
	//TODO rename diskError

	/** Determine if the object is finished, isn't doing any network or disk activity, and won't change again. */
	public boolean isClosed() {
		if (state == OldState.cancelled ||
			state == OldState.completed ||
			state == OldState.couldNot ||
			state == OldState.timedOut ||
			state == OldState.socketException ||
			state == OldState.fileException) return true;
		else return false;
	}
	
	// -------- Methods you can call on a State object to read it easily --------
}
