package org.limewire.hello.base.file;

import java.io.File;

import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.exception.ProgramException;
import org.limewire.hello.base.web.Url;

public class Here {

	// -------- Find where this program is --------
	
	//TODO now you understand how the app will be packaged for windows, mac, and linux
	// have two method, inside() and outside()
	// have unjarred always return working for both
	// have jarred look for a package, and then have inside() be where the jar is and outside() be where the package is

	/**
	 * The Path to the folder this program is running in.
	 * 
	 * Run from Eclipse, it's the working directory set in Eclipse.
	 * Double-click a jar, it's the folder the jar is in.
	 * Run a Mac app with a jar inside, it's the path to the folder that has the Mac app.
	 * 
	 * @throws ProgramException Unable to find the Path
	 */
	public static Path folder() {

		// Text to look for
		final String file = "file:";
		final String jar = "jar:file:";
		final String mac = ".app/Contents/Resources/Java";

		// Find out the address where this code is running from right now
		String s = running();

		// If we're in Eclipse, just use the working directory
		if (Text.starts(s, file))        // file:/C:/Documents/program/hello/class/org/domain/hello/base/file/Here.class
			return working();

		// Parse the file system path from it
		if (Text.starts(s, jar)) {       // jar:file:/C:/Documents/program/hello/work/hello.jar!/org/domain/hello/base/file/Here.class
			s = Text.after(s, jar);      //          /C:/Documents/program/hello/work/hello.jar!/org/domain/hello/base/file/Here.class
			s = Text.before(s, "!");     //          /C:/Documents/program/hello/work/hello.jar
			s = Text.beforeLast(s, "/"); //          /C:/Documents/program/hello/work
		} else {
			throw new ProgramException();
		}

		// Remove Mac app bundle
		if (Text.ends(s, mac)) {         // /Users/User/Desktop/Folder/Hello.app/Contents/Resources/Java
			s = Text.beforeLast(s, mac); // /Users/User/Desktop/Folder/Hello
			s = Text.beforeLast(s, "/"); // /Users/User/Desktop/Folder
		}
		
		// If the jar is in the root, removing the last slash removed the first and only one
		if (Text.isBlank(s)) s = "/";

		// Return it in a Path object
		try {
			return new Path(s);
		} catch (MessageException e) { throw new ProgramException(); } // The Path constructor found s to be relative
	}

	/**
	 * The address of where this Java class is running right now.
	 * 
	 * Windows Eclipse:                                           file:/C:/Documents/program/hello/class/org/domain/hello/base/file/Here.class
	 * Windows Jar:                                             jar:file:/C:/Documents/Folder/hello.jar!/org/domain/hello/base/file/Here.class
	 * Mac App:         jar:file:/Users/User/Desktop/Folder/Hello.app/Contents/Resources/Java/hello.jar!/org/domain/hello/base/file/Here.class
	 * Linux Jar:                                          jar:file:/home/user/Desktop/Folder/hello.jar!/org/domain/hello/base/file/Here.class
	 */
	public static String running() {

		// Find out where this code that's running is
		String name = Here.class.getName();             // org.domain.hello.base.file.Here
		name = Text.replace(name, ".", "/") + ".class"; // org/domain/hello/base/file/Here.class
		String s = ClassLoader.getSystemResource(name).toString();
		return Url.decode(s); // Replace "%20" with spaces
	}
	
	/**
	 * The Path to the present working directory.
	 * 
	 * Run from Eclipse, it's whatever's set in Run, Open Run Dialog, Arguments, Working directory.
	 * Double-click a jar on Windows or Mac, it's the path of the folder the jar is in.
	 * Run a Mac App with a jar inside, it's the path of the folder the app is in.
	 * Run a jar in Linux Nautilus, and it's probably the user's home directory.
	 * 
	 * @throws ProgramException Unable to find the Path
	 */
	public static Path working() {
		try {
			return new Path((new File("")).getAbsoluteFile()); // Turn a blank relative path absolute
		} catch (MessageException e) { throw new ProgramException(); } // We just made it absolute
	}
}
