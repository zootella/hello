package org.limewire.hello.base.web;

import org.junit.Assert;
import org.junit.Test;
import org.limewire.hello.base.exception.MessageException;

public class UrlTest {

	// Test Url parsing user addresses text
	@Test public void test() throws Exception {

		// Blank should throw MessageException
		Url url;
		try {
			url = new Url("");
			Assert.fail();
		} catch (MessageException e) {}
		
		// Minimal user input
		url = new Url("www.site.com"); // User doesn't have to type "http://"
		Assert.assertEquals("http://www.site.com/", url.address);
		Assert.assertEquals("/",                    url.get);
		Assert.assertEquals("http",                 url.protocol);
		Assert.assertEquals("",                     url.user);
		Assert.assertEquals("",                     url.pass);
		Assert.assertEquals("www.site.com",         url.site);
		Assert.assertEquals(80,                     url.port); // Make sure not specified goes to 80, not -1
		Assert.assertEquals("",                     url.path.toString());

		// All the features
		url = new Url("https://user:pass@www.site.com:99/folder/folder/file.ext?parameters#bookmark");
		Assert.assertEquals("https://user:pass@www.site.com:99/folder/folder/file.ext?parameters#bookmark", url.address);
		Assert.assertEquals("/folder/folder/file.ext?parameters", url.get);
		Assert.assertEquals("https",                              url.protocol);
		Assert.assertEquals("user",                               url.user);
		Assert.assertEquals("pass",                               url.pass);
		Assert.assertEquals("www.site.com",                       url.site);
		Assert.assertEquals(99,                                   url.port);
		Assert.assertEquals("folder/folder/file.ext",             url.path.toString());
		
		// The user who types everything in uppercase
		url = new Url("HTTPS://USER:PASS@WWW.SITE.COM:99/FOLDER/FOLDER/FILE.EXT?PARAMETERS#BOOKMARK");
		Assert.assertEquals("https://USER:PASS@www.site.com:99/FOLDER/FOLDER/FILE.EXT?PARAMETERS#BOOKMARK", url.address);
		Assert.assertEquals("/FOLDER/FOLDER/FILE.EXT?PARAMETERS", url.get);
		Assert.assertEquals("https",                              url.protocol);
		Assert.assertEquals("USER",                               url.user);
		Assert.assertEquals("PASS",                               url.pass);
		Assert.assertEquals("www.site.com",                       url.site);
		Assert.assertEquals(99,                                   url.port);
		Assert.assertEquals("FOLDER/FOLDER/FILE.EXT",             url.path.toString());

		// The trailing slash is optional and added
		url = new Url("http://www.site.com");                     // No trailing slash
		Assert.assertEquals("http://www.site.com/", url.address); // Added
		Assert.assertEquals(                   "/", url.get);     // Get is just slash
		url = new Url("http://www.site.com/");                    // Trailing slash
		Assert.assertEquals("http://www.site.com/", url.address); // Still there
		Assert.assertEquals(                   "/", url.get);     // Get is just slash
		
		// Don't change the slash after a folder or file name
		url = new Url("http://www.site.com/name");                     // No trailing slash
		Assert.assertEquals("http://www.site.com/name",  url.address); // Not added
		Assert.assertEquals(                   "/name",  url.get);
		url = new Url("http://www.site.com/name/");                    // Trailing slash
		Assert.assertEquals("http://www.site.com/name/", url.address); // Still there
		Assert.assertEquals(                   "/name/", url.get);

		// Takes encoded and decoded input
		url = new Url("http://www.site.com/folder name/file.ext");   // A space in a folder name
		Assert.assertEquals("http://www.site.com/folder name/file.ext",   url.address);
		Assert.assertEquals(                    "folder name/file.ext",   url.path.toString());
		Assert.assertEquals(                   "/folder%20name/file.ext", url.get);
		url = new Url("http://www.site.com/folder%20name/file.ext"); // A %20 in a folder name
		Assert.assertEquals("http://www.site.com/folder name/file.ext",   url.address);
		Assert.assertEquals(                    "folder name/file.ext",   url.path.toString());
		Assert.assertEquals(                   "/folder%20name/file.ext", url.get);
	}
	
	// Test Url composing descriptive save to disk file names from addresses
	@Test public void name() throws Exception {
		
		// Name and extension
		Url url = new Url("http://www.site.com/name.ext"); // No subfolders
		Assert.assertEquals("name.ext", url.name().toString());
		url = new Url("http://www.site.com/folder/subfolder/name.ext"); // With subfolders
		Assert.assertEquals("name.ext", url.name().toString());

		// Just domain
		url = new Url("http://www.site.com"); // No slash
		Assert.assertEquals("Index at www.site.com.html", url.name().toString());
		url = new Url("http://www.site.com/"); // Slash
		Assert.assertEquals("Index at www.site.com.html", url.name().toString());
		
		// Just folders
		url = new Url("http://www.site.com/folder/subfolder"); // No slash
		Assert.assertEquals("subfolder", url.name().toString());
		url = new Url("http://www.site.com/folder/subfolder/"); // Slash
		Assert.assertEquals("subfolder", url.name().toString());
		
		// Folders with extensions
		url = new Url("http://www.site.com/folder.ext"); // No slash
		Assert.assertEquals("folder.ext", url.name().toString());
		url = new Url("http://www.site.com/folder.ext/"); // Slash
		Assert.assertEquals("folder.ext", url.name().toString());
		url = new Url("http://www.site.com/folder.ext/subfolder"); // Subfolder afterwards
		Assert.assertEquals("subfolder", url.name().toString());
	}
}
