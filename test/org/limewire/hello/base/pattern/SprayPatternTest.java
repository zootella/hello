package org.limewire.hello.base.pattern;

import static org.junit.Assert.*;

import org.junit.Test;
import org.limewire.hello.all.Main;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.encode.Encode;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.size.SprayPattern;

public class SprayPatternTest {
	
	@Test
	public void testRoundTrip() throws Exception {
		
		testRoundTrip("0");
		testRoundTrip("1");
		testRoundTrip("00");
		testRoundTrip("01");
		testRoundTrip("10");
		testRoundTrip("11");
		testRoundTrip("00000000");
		testRoundTrip("000000000");
		testRoundTrip("11111111");
		testRoundTrip("111111111");
		testRoundTrip("00000001");
		testRoundTrip("000000001");
		testRoundTrip("11111110");
		testRoundTrip("111111110");
		testRoundTrip("10000000");
		testRoundTrip("100000000");
		testRoundTrip("01111111");
		testRoundTrip("011111111");
	}
	
	public void testRoundTrip(String s) throws Exception {
			
		SprayPattern p = new SprayPattern(s);
		Data d = p.data();
		SprayPattern p2 = new SprayPattern(d, p.size());
		if (!p.equals(p2)) Main.report("mismatch on " + s);
	}
	
	@Test
	public void testFromData() throws ChopException, MessageException {
			
		SprayPattern p;
		
		p = new SprayPattern(Encode.data("00"), 1);
		assertTrue(p.toString().equals("0"));
		
		p = new SprayPattern(Encode.data("ff"), 1);
		assertTrue(p.toString().equals("1"));
		
		p = new SprayPattern(Encode.data("80"), 1);
		assertTrue(p.toString().equals("1"));
		
		p = new SprayPattern(Encode.data("80"), 2);
		assertTrue(p.toString().equals("10"));
		
		p = new SprayPattern(Encode.data("40"), 2);
		assertTrue(p.toString().equals("01"));
	}
	
	@Test
	public void testToData() {
		
		SprayPattern p;
		
		p = new SprayPattern("0");
		assertTrue(p.data().base16().equals("00"));
		
		p = new SprayPattern("00000000");
		assertTrue(p.data().base16().equals("00"));

		p = new SprayPattern("000000000");
		assertTrue(p.data().base16().equals("0000"));
		
		p = new SprayPattern("1");
		assertTrue(p.data().base16().equals("80"));
		
		p = new SprayPattern("11111111");
		assertTrue(p.data().base16().equals("ff"));
		
		p = new SprayPattern("111111111");
		assertTrue(p.data().base16().equals("ff80"));
		
		p = new SprayPattern("10");
		assertTrue(p.data().base16().equals("80"));

		p = new SprayPattern("00000001");
		assertTrue(p.data().base16().equals("01"));
		
		p = new SprayPattern("000000010");
		assertTrue(p.data().base16().equals("0100"));
		
		p = new SprayPattern("000000011");
		assertTrue(p.data().base16().equals("0180"));
	}
	
	@Test
	public void testShorten() {
		
		SprayPattern p;
		
		p = new SprayPattern("00000");
		p.size(4);
		assertTrue(p.toString().equals("0000"));
		
		p = new SprayPattern("11111");
		p.size(4);
		assertTrue(p.toString().equals("1111"));
		
		p = new SprayPattern("00001");
		p.size(4);
		assertTrue(p.toString().equals("0000"));
		
		p = new SprayPattern("11110");
		p.size(4);
		assertTrue(p.toString().equals("1111"));
	}
	
	@Test
	public void testLoop() {
		
		testLoopRun("0");
		testLoopRun("1");
		
		testLoopRun("00");
		testLoopRun("01");
		testLoopRun("10");
		testLoopRun("11");
		
		testLoopRun("000");
		testLoopRun("001");
		testLoopRun("010");
		testLoopRun("011");
		testLoopRun("100");
		testLoopRun("101");
		testLoopRun("110");
		testLoopRun("111");
		
		testLoopRun("000000000");
		testLoopRun("111111111");
		testLoopRun("010101010");
		testLoopRun("101010101");
		testLoopRun("0011001100");
		testLoopRun("1100110011");
	}
	
	public void testLoopRun(String s) {
			
		Main.report("");
		Main.report(s);
		
		SprayPattern p = new SprayPattern(s);
		
		SprayPattern.Step step = p.step(false);
		while (step.next()) {
			Main.report("false " + step.i());
		}
		
		step = p.step(true);
		while (step.next()) {
			Main.report("true " + step.i());
		}
	}
	
	@Test
	public void testEdit() {
		
		SprayPattern p = new SprayPattern(5);
		assertTrue(p.toString().equals("00000"));
		assertTrue(p.count(false) == 5);
		assertTrue(p.count(true) == 0);
		
		p.set(true);
		assertTrue(p.toString().equals("11111"));
		assertTrue(p.count(false) == 0);
		assertTrue(p.count(true) == 5);
		
		p.set(1, false);
		p.set(2, false);
		assertTrue(p.toString().equals("10011"));
		assertTrue(p.count(false) == 2);
		assertTrue(p.count(true) == 3);
		assertTrue(p.get(0) == true);
		assertTrue(p.get(1) == false);
		
		p = new SprayPattern(2);
		p.set(0, true);
		assertTrue(p.toString().equals("10"));
	}
	
	@Test
	public void testMath() {
		
		testMathRun("0", "0", "1", "0", "0", "0", "0");
		testMathRun("0", "1", "1", "1", "0", "0", "1");
		testMathRun("1", "0", "0", "1", "0", "1", "1");
		testMathRun("1", "1", "0", "1", "1", "0", "0");
		
		testMathRun("0000", "0101", "1111", "0101", "0000", "0000", "0101");
		testMathRun("1111", "0101", "0000", "1111", "0101", "1010", "1010");
		testMathRun("0011", "0000", "1100", "0011", "0000", "0011", "0011");
		testMathRun("0011", "1111", "1100", "1111", "0011", "0000", "1100");
	}
	
	public void testMathRun(String sth, String sp, String snot, String sor, String sand, String sandnot, String sxor) {
			
		SprayPattern th     = new SprayPattern(sth);
		SprayPattern p      = new SprayPattern(sp);
		SprayPattern not    = new SprayPattern(snot);
		SprayPattern or     = new SprayPattern(sor);
		SprayPattern and    = new SprayPattern(sand);
		SprayPattern andnot = new SprayPattern(sandnot);
		SprayPattern xor    = new SprayPattern(sxor);
		
		assertTrue(th.copy().not().equals(not));
		assertTrue(th.copy().or(p).equals(or));
		assertTrue(th.copy().and(p).equals(and));
		assertTrue(th.copy().andNot(p).equals(andnot));
		assertTrue(th.copy().xor(p).equals(xor));
		
		// commutative
		assertTrue(p.copy().or(th).equals(or));
		assertTrue(p.copy().and(th).equals(and));
		assertTrue(p.copy().xor(th).equals(xor));
		
		// and not in two steps
		SprayPattern notp = p.copy().not();
		assertTrue(th.copy().and(notp).equals(andnot));
	}
}
