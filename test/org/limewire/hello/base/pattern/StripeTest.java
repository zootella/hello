package org.limewire.hello.base.pattern;

import static org.junit.Assert.*;

import org.junit.Test;

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
	
	@Test public void testMinus() throws Exception {
		
		checkMinus(new Stripe(3, 4), new Stripe(0, 2), new Stripe(3, 4));
		checkMinus(new Stripe(3, 4), new Stripe(1, 2), new Stripe(3, 4));
		checkMinus(new Stripe(3, 4), new Stripe(2, 2), new Stripe(4, 3));
		checkMinus(new Stripe(3, 4), new Stripe(3, 2), new Stripe(5, 2));
		try {
			checkMinus(new Stripe(3, 4), new Stripe(4, 2), null);
			fail();
		} catch (IndexOutOfBoundsException e) {}
		checkMinus(new Stripe(3, 4), new Stripe(5, 2), new Stripe(3, 2));
		checkMinus(new Stripe(3, 4), new Stripe(6, 2), new Stripe(3, 3));
		checkMinus(new Stripe(3, 4), new Stripe(7, 2), new Stripe(3, 4));
		checkMinus(new Stripe(3, 4), new Stripe(8, 2), new Stripe(3, 4));
		
		checkMinus(new Stripe(3, 4), new Stripe(3, 4), null);
	}
	
	public void checkMinus(Stripe s1, Stripe s2, Stripe minus) {
		Stripe minus1 = s1.minus(s2);
		assertEquals(minus, minus1);
	}
}
