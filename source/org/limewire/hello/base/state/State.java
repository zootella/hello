package org.limewire.hello.base.state;

// return a State object to communicate what you're doing, or how you ended
// all the information about your state is organized into a single convenient State object
// extends exception, so you can throw a state to get out of something to close an object with that closed outcome
public class State extends Exception { // Methods that throw State have to declare it

	// -------- Make and return a State object to report your state --------
	
	/** Make a new State object to report the given state. */
	public State(int state) {
		this.state = state;
		this.exception = null;
		this.user = null;
	}

	/** Make a new State object to report the given state, with a short message for the user about what happened. */
	public State(int state, String user) {
		this.state = state;
		this.exception = null;
		this.user = user;
	}

	/** Make a new State object to report the given state, with the exception Java threw us. */
	public State(int state, Exception exception) {
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
	public static State paused() { return new State(paused); }

	/** The object is waiting in line before attempting something, and will start all by itself as soon as it can. */
	public static final int queued = 1;
	public static State queued() { return new State(queued); }

	/** Determine if the object hasn't started yet, and is waiting for the program to start it or waiting in line. */
	public boolean isPending() {
		if (state == State.paused ||
			state == State.queued) return true;
		else return false;
	}

	// Active operations
	
	/**
	 * The object is performing a task that it must complete before doing its main job.
	 * What the object is opening depends on what kind of object it is.
	 * For instance, it might be connecting a TCP socket to a remote IP address.
	 */
	public static final int opening = 2;
	public static State opening() { return new State(opening); }
	
	/**
	 * The object is carrying out its main job.
	 * What the object is doing depends on what kind of object it is.
	 * For instance, it might be transferring data through a TCP socket connection.
	 * It might be saving data to disk, hashing data, or doing a DNS lookup.
	 */
	public static final int doing = 3;
	public static State doing() { return new State(doing); }

	/** Determine if the object is performing some operation right now, making network and disk activity. */
	public boolean isActive() {
		if (state == State.opening ||
			state == State.doing) return true;
		else return false;
	}

	// Closed outcomes
	
	/** The program closed the object because it didn't need it anymore. */
	public static final int cancelled = 4;
	public static State cancelled() { return new State(cancelled); }
	
	/** The object closed itself because it successfully completed its task. */
	public static final int completed = 5;
	public static State completed() { return new State(completed); }
	
	/** The object closed itself because it determined that it could not complete successfully, and had to give up. */
	public static final int couldNot = 6;
	public static State couldNot() { return new State(couldNot); }
	
	/** The object closed itself because it took too long without finishing or making any progress. */
	public static final int timedOut = 7;
	public static State timedOut() { return new State(timedOut); }
	
	/** The object closed itself because Java threw it an exception when it tried to do something with a socket. */
	public static final int socketException = 8;
	public static State socketException() { return new State(socketException); }
	public static State socketException(Exception e) { return new State(socketException, e); }
	//TODO rename netError
	
	/** The object closed itself because Java threw it an exception when it tried to do something with a file. */
	public static final int fileException = 9;
	public static State fileException() { return new State(fileException); }
	//TODO rename diskError

	/** Determine if the object is finished, isn't doing any network or disk activity, and won't change again. */
	public boolean isClosed() {
		if (state == State.cancelled ||
			state == State.completed ||
			state == State.couldNot ||
			state == State.timedOut ||
			state == State.socketException ||
			state == State.fileException) return true;
		else return false;
	}
	
	// -------- Methods you can call on a State object to read it easily --------
}
