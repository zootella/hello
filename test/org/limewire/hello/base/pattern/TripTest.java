package org.limewire.hello.base.pattern;

import org.junit.Assert;
import org.junit.Test;

public class TripTest {

	@Test public void testSize() throws Exception {
		
		Trip p = new Trip(0, 0, 100);
		Assert.assertEquals(0, p.done);
		Assert.assertEquals(0, p.at());
		Assert.assertEquals(100, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(false, p.isEmpty());
		
		p = p.add(30);
		Assert.assertEquals(30, p.done);
		Assert.assertEquals(30, p.at());
		Assert.assertEquals(70, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(false, p.isEmpty());
		
		p = p.add(70);
		Assert.assertEquals(100, p.done);
		Assert.assertEquals(100, p.at());
		Assert.assertEquals(0, p.remain());
		Assert.assertEquals(true, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
	}

	@Test public void testSizeOffset() throws Exception {
		
		Trip p = new Trip(50, 0, 100);
		Assert.assertEquals(0, p.done);
		Assert.assertEquals(50, p.at());
		Assert.assertEquals(100, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(false, p.isEmpty());
		
		p = p.add(30);
		Assert.assertEquals(30, p.done);
		Assert.assertEquals(80, p.at());
		Assert.assertEquals(70, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(false, p.isEmpty());
		
		p = p.add(70);
		Assert.assertEquals(100, p.done);
		Assert.assertEquals(150, p.at());
		Assert.assertEquals(0, p.remain());
		Assert.assertEquals(true, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
	}

	@Test public void test() throws Exception {
		
		Trip p = new Trip(0, 0, -1);
		Assert.assertEquals(0, p.done);
		Assert.assertEquals(0, p.at());
		Assert.assertEquals(-1, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
		
		p = p.add(30);
		Assert.assertEquals(30, p.done);
		Assert.assertEquals(30, p.at());
		Assert.assertEquals(-1, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
		
		p = p.add(70);
		Assert.assertEquals(100, p.done);
		Assert.assertEquals(100, p.at());
		Assert.assertEquals(-1, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
	}

	@Test public void testOffset() throws Exception {
		
		Trip p = new Trip(50, 0, -1);
		Assert.assertEquals(0, p.done);
		Assert.assertEquals(50, p.at());
		Assert.assertEquals(-1, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
		
		p = p.add(30);
		Assert.assertEquals(30, p.done);
		Assert.assertEquals(80, p.at());
		Assert.assertEquals(-1, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
		
		p = p.add(70);
		Assert.assertEquals(100, p.done);
		Assert.assertEquals(150, p.at());
		Assert.assertEquals(-1, p.remain());
		Assert.assertEquals(false, p.isDone());
		Assert.assertEquals(true, p.isEmpty());
	}
}
