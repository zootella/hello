package org.limewire.hello.base.data;

import java.nio.ByteBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class BinTest {
	
	
	
	@Test public void test() {
		
		// actually, just confirming something with ByteBuffer
		ByteBuffer b = ByteBuffer.allocate(8);
		b.position(5);
		b.limit(5);
		
		assertEquals(5, b.position());
		assertEquals(5, b.limit());
		
		b.compact();
		
		assertEquals(0, b.position());
		assertEquals(8, b.limit());
		
		
	}
	

}
