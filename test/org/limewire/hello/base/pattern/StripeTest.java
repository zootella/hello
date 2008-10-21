package org.limewire.hello.base.pattern;

import static org.junit.Assert.*;

import org.junit.Test;
import org.limewire.hello.base.size.Stripe;

public class StripeTest {
	
	@Test public void testAnd() throws Exception {
		
		checkAnd(new Stripe(3, 4), new Stripe(0, 2), null);
		checkAnd(new Stripe(3, 4), new Stripe(1, 2), null);
		checkAnd(new Stripe(3, 4), new Stripe(2, 2), new Stripe(3, 1));
		checkAnd(new Stripe(3, 4), new Stripe(3, 2), new Stripe(3, 2));
		checkAnd(new Stripe(3, 4), new Stripe(4, 2), new Stripe(4, 2));
		checkAnd(new Stripe(3, 4), new Stripe(5, 2), new Stripe(5, 2));
		checkAnd(new Stripe(3, 4), new Stripe(6, 2), new Stripe(6, 1));
		checkAnd(new Stripe(3, 4), new Stripe(7, 2), null);
		checkAnd(new Stripe(3, 4), new Stripe(8, 2), null);
	}
	
	public void checkAnd(Stripe s1, Stripe s2, Stripe and) {
		Stripe and1 = s1.and(s2);
		Stripe and2 = s2.and(s1);
		assertEquals(and, and1);
		assertEquals(and, and2);
	}
	
	@Test public void testAfter() throws Exception {
		
		checkAfter(new Stripe(3, 4), new Stripe(3, 1), new Stripe(4, 3));
		checkAfter(new Stripe(3, 4), new Stripe(3, 4), null);
	}
	
	public void checkAfter(Stripe s1, Stripe s2, Stripe after) {
		Stripe after1 = s1.after(s2);
		assertEquals(after, after1);
	}
}
