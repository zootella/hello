package org.limewire.hello.base.file;

import org.junit.Test;
import org.junit.Assert;
import org.limewire.hello.base.exception.MessageException;

public class PathTest {

	@Test
	public void test() throws Exception {

		// good absolute paths, windows-style
		
		new Path("C:\\folder\\subfolder"); // escaped backslashes
		new Path("C:/folder/subfolder");   // forward slashes
		new Path("/C:/folder/subfolder");  // forward slashes including root slash
		
		new Path("C:\\folder");
		new Path("C:/folder");
		new Path("/C:/folder");
		
		new Path("C:\\");
		new Path("C:/");
		new Path("/C:/");
		
		new Path("C:"); // it took extra code to make these work
		new Path("C:");
		new Path("/C:");
		
		// bad because they are relative
		
		confirmBad("");
		confirmBad("hello");
		confirmBad("hello/you");
		
		confirmBad("./"); // here
		confirmBad("../"); // up one
		confirmBad("../../");
		
		confirmBad("./hello");
		confirmBad("../hello");
		confirmBad("../../hello");

		confirmBad("./hello/you");
		confirmBad("../hello/you");
		confirmBad("../../hello/you");
		
		// nonsense
		
		confirmBad(" ");
		confirmBad("*");
		confirmBad(":");
	}

	private void confirmBad(String s) {
		try {
			new Path(s);
			Assert.fail("expected message exception");
		} catch (MessageException e) {}
	}
}
