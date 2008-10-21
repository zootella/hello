package org.limewire.hello.base.time;

import org.junit.Test;
import org.junit.Assert;
import org.limewire.hello.base.size.Size;
import org.limewire.hello.base.time.Progress;

public class ProgressTest {
	
	@Test public void testSizeUnknown() throws Exception {
		
		// Size unknown
		Progress p = new Progress("test", "Testing", "Tested");

		// Nothing saved
		Assert.assertEquals("", p.describeSize());
		
		// 8 KB saved
		p.add(8 * Size.kilobyte);
		Assert.assertEquals("Tested 8 KB", p.describeSize());
	}
	
	@Test public void testSizeKnown() throws Exception {
		
		// Size known
		Progress p = new Progress("test", "Testing", "Tested");
		p.size(Size.gigabyte);
		long percent = Size.gigabyte / 100;
		
		// Nothing saved
		Assert.assertEquals(0, p.done());
		Assert.assertEquals(0, p.percent());
		Assert.assertEquals("1,048,576 KB", p.describeSize());

		// Half a percent saved
		p.done((long)(0.5 * percent));
		Assert.assertEquals(0, p.percent());
		Assert.assertEquals("5,243 KB/1,048,576 KB", p.describeSize());
		
		// 2.5 percent saved
		p.done((long)(2.5 * percent));
		Assert.assertEquals(2, p.percent());
		Assert.assertEquals("2% 26,215 KB/1,048,576 KB", p.describeSize());
		
		// 98.5 percent saved
		p.done((long)(98.5 * percent));
		Assert.assertEquals(98, p.percent());
		Assert.assertEquals("98% 1,032,848 KB/1,048,576 KB", p.describeSize());
		
		// 100 percent saved
		p.done(Size.gigabyte);
		Assert.assertEquals(100, p.percent());
		Assert.assertEquals("1,048,576 KB", p.describeSize());
	}
}
