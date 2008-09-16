package org.limewire.hello.base.valve;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PairTest {

	@Test public void test0() throws Exception {
		List<Character> list = new ArrayList<Character>();
		
		List<Pair> pairs = Pair.pairs(list);
		assertEquals(0, pairs.size());
	}
	
	@Test public void test1() throws Exception {
		List<Character> list = new ArrayList<Character>();
		list.add(new Character('a'));
		
		List<Pair> pairs = Pair.pairs(list);
		assertEquals(0, pairs.size());
	}
	
	@Test public void test2() throws Exception {
		List<Character> list = new ArrayList<Character>();
		list.add(new Character('a'));
		list.add(new Character('b'));
		
		List<Pair> pairs = Pair.pairs(list);
		assertEquals(1, pairs.size());
		
		assertEquals('a', pairs.get(0).a);
		assertEquals('b', pairs.get(0).b);
	}
	
	@Test public void test3() throws Exception {
		List<Character> list = new ArrayList<Character>();
		list.add(new Character('a'));
		list.add(new Character('b'));
		list.add(new Character('c'));
		
		List<Pair> pairs = Pair.pairs(list);
		assertEquals(2, pairs.size());
		
		assertEquals('b', pairs.get(0).a);
		assertEquals('c', pairs.get(0).b);
		
		assertEquals('a', pairs.get(1).a);
		assertEquals('b', pairs.get(1).b);
	}
	
	@Test public void test4() throws Exception {
		List<Character> list = new ArrayList<Character>();
		list.add(new Character('a'));
		list.add(new Character('b'));
		list.add(new Character('c'));
		list.add(new Character('d'));
		
		List<Pair> pairs = Pair.pairs(list);
		assertEquals(3, pairs.size());
		
		assertEquals('c', pairs.get(0).a);
		assertEquals('d', pairs.get(0).b);
		
		assertEquals('b', pairs.get(1).a);
		assertEquals('c', pairs.get(1).b);
		
		assertEquals('a', pairs.get(2).a);
		assertEquals('b', pairs.get(2).b);
	}
	
	@Test public void test5() throws Exception {
		List<Character> list = new ArrayList<Character>();
		list.add(new Character('a'));
		list.add(new Character('b'));
		list.add(new Character('c'));
		list.add(new Character('d'));
		list.add(new Character('e'));
		
		List<Pair> pairs = Pair.pairs(list);
		assertEquals(4, pairs.size());
		
		assertEquals('d', pairs.get(0).a);
		assertEquals('e', pairs.get(0).b);
		
		assertEquals('c', pairs.get(1).a);
		assertEquals('d', pairs.get(1).b);
		
		assertEquals('b', pairs.get(2).a);
		assertEquals('c', pairs.get(2).b);
		
		assertEquals('a', pairs.get(3).a);
		assertEquals('b', pairs.get(3).b);
	}
}
