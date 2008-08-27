package org.limewire.hello.base.data;

import org.junit.Test;
import org.junit.Assert;

public class TextTest {
	
	@Test
	public void character() {
		
		// good letters
		Assert.assertTrue(Text.isLetter('a'));
		Assert.assertTrue(Text.isLetter('k'));
		Assert.assertTrue(Text.isLetter('z'));
		
		Assert.assertTrue(Text.isLetter('A'));
		Assert.assertTrue(Text.isLetter('K'));
		Assert.assertTrue(Text.isLetter('Z'));

		// good digits
		Assert.assertTrue(Text.isNumber('0'));
		Assert.assertTrue(Text.isNumber('5'));
		Assert.assertTrue(Text.isNumber('9'));

		// bad for both
		Assert.assertFalse(Text.isLetter(' '));
		Assert.assertFalse(Text.isLetter('-'));
		Assert.assertFalse(Text.isLetter('\0'));
		
		Assert.assertFalse(Text.isNumber(' '));
		Assert.assertFalse(Text.isNumber('-'));
		Assert.assertFalse(Text.isNumber('\0'));
		
		// the other, not this one
		Assert.assertFalse(Text.isLetter('0'));
		Assert.assertFalse(Text.isNumber('a'));
	}
}
