package org.limewire.hello.base.size;

import org.limewire.hello.base.time.Speed;

/** A Meter has done that grows in range. */
public class Meter {
	
	
	/** Make a Meter with a Range that starts at 0 and imposes no Limit. */
	public Meter() { this(new Range()); }

	/** Make a Meter with the given Range. */
	public Meter(Range range) {
		this.range = range;
		speed = new Speed();
	}
	
	
	
	

	/*
	public void limit(Limit limit) {
		if (done != 0) throw new IllegalStateException(); // Can't set limit after something is done
		
		// this gets called when http headers tell us how big a file is
		// have this only do something when we don't have a range with a limit, and the given range has one
		
		
		
	}
	*/
	
	
	
	
	
	// Look

	/** The whole Range this Meter is going over. */
	private final Range range;
	
	/** How much this Meter has done in its range. */
	public long done() { return done; }
	private long done;

	/** The Range that remains for us to do. */
	public Range remain() { return range.after(done); }
	/** true if this Meter must not do more. */
	public boolean isDone() { return remain().isDone(); }
	/** true if this Meter is empty of responsibility to do more. */
	public boolean isEmpty() { return remain().isEmpty(); }

	/** How fast done() is growing. */
	public final Speed speed;
	
	// Add
	
	
	public void add(long more) {
		if (more < 1) throw new IndexOutOfBoundsException();
		range.check(done + more);
		done += more;
	}
	
	
	
	
	
}
