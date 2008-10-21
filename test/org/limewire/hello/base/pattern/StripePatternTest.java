package org.limewire.hello.base.pattern;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.limewire.hello.all.Main;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.size.Stripe;
import org.limewire.hello.base.size.StripePattern;

public class StripePatternTest {
	
	@Test
	public void test() throws MessageException {
		
		testOperations("",  "",  "",  "",  "0");
		testOperations("",  "0", "",  "0", "0");
		testOperations("0", "0", "0", "0", "");
		testOperations("",  "1", "",  "1", "0");
		testOperations("0", "1", "1", "0", "");
		testOperations("1", "",  "",  "1", "0 1");
		testOperations("1", "0", "1", "0", "0 1");
		testOperations("1", "1", "1", "1", "0 1");
		
		testOperations(
			"4 1 2 1 3 1 1 2 3 1 1 1 3 2 1 4 1",
			"1 1 1 3 1 2 1 2 2 2 1 5 1 2 3 1 1 3",
			"4 1 2 1 3 1 2 1 3 1 1 1 3 1 3 1 1 1 1 1",
			"1 1 1 3 1 2 1 2 1 3 1 5 1 3 1",
			"0 4 1 2 1 3 1 1 2 3 1 1 1 3 2 1 4 1");
		
		testOperations(
			"1 1 1 1 1 1 1 1",
			"0 1 1 1 1 1 1 1 1",
			"",
			"0",
			"0 1 1 1 1 1 1 1 1");
		
		testXor("0 2 3 2", "1 2 1 2 2", "0 1 1 1 1 1 1 1 1");
	}
	
	public void testOperations(String sa, String sb, String sand, String sor, String snot) throws MessageException {
		
		StripePattern a = new StripePattern(sa);
		StripePattern b = new StripePattern(sb);
		StripePattern and = new StripePattern(sand);
		StripePattern or = new StripePattern(sor);
		StripePattern not = new StripePattern(snot);
		
		/*
			Main.report("a:     " + a.toString());
			Main.report("b:     " + b.toString());
			Main.report("a & b: " + a.and(b).toString());
			Main.report("a | b: " + a.or(b).toString());
			Main.report("!a:    " + a.not().toString());
		 */
		
		assertTrue((a.and(b)).equals(and)); // mismatch on and
		assertTrue((a.or(b)).equals(or)); // mismatch on or
		assertTrue((a.not()).equals(not)); // mismatch on not
		assertTrue((a.and(b).equals(b.and(a)))); // order mattered for and
		assertTrue((a.or(b).equals(b.or(a)))); // order mattered for or
	}
	
	public void testXor(String sa, String sb, String sx) throws MessageException {

		StripePattern a = new StripePattern(sa);
		StripePattern b = new StripePattern(sb);
		StripePattern x = new StripePattern(sx);
		
		if (!(a.xor(b)).equals(x)) Main.report("mimatch on xor");
	}
	
	@Test
	public void testClip() throws MessageException {
			
		StripePattern a = new StripePattern("0");
		StripePattern b = a.clip(new Stripe(5, 5));
		if (!b.equals(new StripePattern("0 5"))) Main.report("unexpected");
		
		a = new StripePattern("5 3 1 3");
		b = a.clip(new Stripe(7, 3));
		if (!b.equals(new StripePattern("0 1 1 1"))) Main.report("unexpected");
	}
	
	@Test
	public void testStripes() throws MessageException {
		try {
			
			Stripe clip = new Stripe(0, 100);
			
			StripePattern a = new StripePattern("0 2 1 3 1 4 1 5");
			List<Stripe> stripes = a.stripes(clip);
			StripePattern b = new StripePattern();
			for (Stripe stripe : stripes) {
				b = b.add(stripe);
			}
			if (!a.equals(b)) Main.report("unexpected");
			
			List<Stripe> gaps = a.gaps(clip);
			if (gaps.size() != 4) Main.report("unexpected");
			if (!(gaps.get(0).equals(new Stripe(2, 1)))) Main.report("mismatch");
			if (!(gaps.get(1).equals(new Stripe(6, 1)))) Main.report("mismatch");
			if (!(gaps.get(2).equals(new Stripe(11, 1)))) Main.report("mismatch");
			if (!(gaps.get(3).equals(new Stripe(17, 83)))) Main.report("mismatch");
			
			Stripe biggestStripe = a.biggestStripe(clip);
			Stripe biggestGap = a.biggestGap(clip);
			if (!biggestStripe.equals(new Stripe(12, 5))) Main.report("mismatch");
			if (!biggestGap.equals(new Stripe(17, 83))) Main.report("mismatch");
			
			long sizeTrue = a.sizeTrue(clip);
			long sizeFalse = a.sizeFalse(clip);
			if (sizeTrue != 14) Main.report("mismatch");
			if (sizeFalse != 86) Main.report("mismatch");
			if (sizeTrue + sizeFalse != clip.size) Main.report("mismatch");
			
		} catch (MessageException e) {
			Main.report("message exception");
		}
	}
	
	@Test
	public void testAddAndRemove() {
		
		Stripe stripe = new Stripe(1, 2);
		
		StripePattern p = new StripePattern();
		assertTrue(p.is(false, stripe));
		p = p.add(stripe);
		assertTrue(p.is(true, stripe));
		p = p.remove(stripe);
		assertTrue(p.is(false, stripe));
	}
}
