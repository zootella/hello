package org.limewire.hello.base.state.old;

// return a State object to communicate what you're doing, or how you ended

// the simple states are
// doing
// closed
//   good
//   bad

//TODO you've made this much simpler state, see if you can use it in place of OldState everywhere
//TODO actually, now you think you don't need state at all, old or new, boolean closed and extra methods are enough

public class NewState {

	// -------- Make a new State to report the state an object is in --------
	
	/** Make a new State to report the object is doing its task, and hasn't finished yet. */
	public static NewState doing() { return new NewState(doing); }
	
	/** Make a new State to report the object finished its task successfully. */
	public static NewState good() { return new NewState(good); }
	
	/** Make a new State to report the object could not complete its task, and gave up. */
	public static NewState bad() { return new NewState(bad); }
	/** Make a new State to report the object could not complete its task because of the given Exception, and gave up. */
	public static NewState bad(Exception e) { return new NewState(bad, e); }

	// -------- See what this State reports --------
	
	/** The object is doing its task, and hasn't finished yet. */
	public boolean isDoing() { return state == doing; }
	/** The object is closed with success or failure, and won't change state again. */
	public boolean isClosed() { return state != doing; }

	/** The object finished its task successfully. */
	public boolean isGood() { return state == good; }

	/** The object could not successfully complete its task, and gave up. */
	public boolean isBad() { return state == bad; }

	// -------- Inside --------
	
	/** Make a new State object to report the given state. */
	private NewState(int state) {
		this.state = state;
		this.exception = null;
	}

	/** Make a new State object to report the given state, and include the given Exception. */
	private NewState(int state, Exception exception) {
		this.state = state;
		this.exception = exception;
	}

	/** The state the object is currently in. */
	private final int state;
	
	/** The exception Java threw the object that made it fail. */
	public final Exception exception;
	
	/** The object is doing its task, and hasn't finished yet. */
	private static final int doing = 0;
	/** The object finished its task successfully. */
	private static final int good = 1;
	/** The object failed at its task, and gave up. */
	private static final int bad = 2;
}
