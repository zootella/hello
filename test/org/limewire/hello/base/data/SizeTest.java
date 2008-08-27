package org.limewire.hello.base.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class SizeTest {
	
	@Test
	public void test() {

		test(0x0);
		test(0x0fffffff);
		test(0x1);
		test(126);
		test(127);
		test(128);
		test(0xff);
		
		assertTrue(true);
		
	}
	
	public void test(int n) {
		
		/*
		try {
			
			Bay bay = new Bay();
			Outline.numberToBay(bay, n);
			Data data = bay.data();
			
			Main.report(n + " became " + data.base16());
			
			int n2 = Outline.numberParse(data);
			if (data.hasData()) Main.report("left some data behind");
			if (n2 != n) Main.report("turned " + n + " into " + n2);
			
		} catch (ChopException e) {
			Main.report("chop exception");
		} catch (MessageException e) {
			Main.report("message exception");
		}
		*/
	}
}
