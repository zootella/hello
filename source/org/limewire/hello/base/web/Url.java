package org.limewire.hello.base.web;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.limewire.hello.base.data.Convert;
import org.limewire.hello.base.data.Text;
import org.limewire.hello.base.data.TextSplit;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.exception.PlatformException;
import org.limewire.hello.base.file.Name;
import org.limewire.hello.base.file.PathName;

public class Url {

	// -------- Parts of the Url --------

	/*
	 * Here's how a Url object splits a HTTP URL address into different parts:
	 * 
	 * address   http://user:pass@www.site.com:99/folder/folder/file.ext?parameters#bookmark
	 * get                                       /folder/folder/file.ext?parameters#bookmark
	 * protocol  http
	 * user             user
	 * pass                  pass
	 * site                       www.site.com
	 * port                                    99
	 * path                                       folder/folder/file.ext
	 */

	/** The whole address. */
    public final String address;
    /** The HTTP GET text, like "/" or "/folder%20name/file.ext?parameters", with spaces replaced by "%20", never blank "/" instead. */
    public final String get;
    /** The URL protocol, like "http" or "https", in lower case. */
    public final String protocol;
    /** The user name written in the URL, like "user". */
	public final String user;
	/** The password written in the URL, like "pass". */
	public final String pass;
	/** The site name, like "www.site.com", in lower case. */
	public final String site;
	/** The port number written in the URL, or the default for the protocol like 80 or 443 if not written, -1 not written and protocol unknown. */
	public final int port;
	/** The folder and file path, like "folder/subfolder/file.ext", a blank PathName if there aren't any folders or file name. */
    public final PathName path;

	// -------- Parse text from the user into a new Url object --------

	/**
	 * Parse text from the user into a new Url object.
	 * Lets the user omit "http://" at the start, and type something with spaces.
	 */
	public Url(String s) throws MessageException {

		// Prepare the given text from the user
		s = s.trim();                             // Remove spaces from the start and end
		if (!Text.has(s, ":")) s = "http://" + s; // If there is no protocol, cap on "http://"
		s = encodeSpacesOnly(s);                  // Keep the URI constructor from choking on a space from the user
		
		// Parse the text into a Java URI object
		try {
			uri = new URI(s);
		} catch (URISyntaxException e) { throw new MessageException(); } // Invalid
		if (Text.isBlank(uri.toString()) || Text.isBlank(uri.getScheme()) || Text.isBlank(uri.getHost()))
			throw new MessageException(); // Missing required parts

		// Make local blank objects to change before setting the corresponding final members of this new Url object
		String address, get, protocol, user, pass, site;
		address = get = protocol = user = pass = site = "";
		int port = -1;
		PathName path = new PathName();
		
		// HTTP GET text, like "/" or "/folder%20name/file.ext?parameters"
		if (Text.hasText(uri.getPath()))
			get = uri.getPath();
		if (Text.isBlank(get))
			get = "/";
		if (Text.hasText(uri.getQuery()))
			get += "?" + uri.getQuery();
		get = encodeSpacesOnly(get); // Encode spaces to put it in the middle of the first line of HTTP GET request headers

		// Address, like "http://www.site.com/"
		if (Text.hasText(uri.toString()));
			address = decode(uri.toString()); // Decode for display
		if (Text.same(get, "/") && !Text.ends(address, "/")) // Append trailing slash
			address += "/";

		// Protocol, like "http" or "https"
		if (Text.hasText(uri.getScheme())) {
			protocol = uri.getScheme().toLowerCase(); // Lowercase protocol
			TextSplit split = Text.split(address, uri.getScheme()); // Lowercase it in the address also
			if (split.found) address = split.before + protocol + split.after;
		}

		// User name and password, like "user" and "pass"
		if (Text.hasText(uri.getUserInfo())) {
			TextSplit split = Text.split(uri.getUserInfo(), ":");
			user = split.before; // No ":", and it's all the user name before
			pass = split.after;
		}

		// Site, like "www.site.com"
		if (Text.hasText(uri.getHost())) {
			site = uri.getHost().toLowerCase(); // Lowercase site
			TextSplit split = Text.split(address, uri.getHost()); // Lowercase it in the address also
			if (split.found) address = split.before + site + split.after;
		}

		// Port number
		if (uri.getPort() != -1) // Use what's written in the URL
			port = uri.getPort();
		else if (Text.same(protocol, "http")) // Nothing written in the URL, HTTP default 80
			port = 80;
		else if (Text.same(protocol, "https")) // Nothing written in the URL, HTTPS default 443
			port = 443;
		
		// Path, like "folder/folder/file.ext"
		if (Text.hasText(uri.getPath()))
			path = new PathName(uri.getPath()); // Have the PathName constructor parse it

		// Save the information we parsed in the final members of this new Url object
		this.address = address;
		this.get = get;
		this.protocol = protocol;
		this.user = user;
		this.pass = pass;
		this.site = site;
		this.port = port;
		this.path = path;
	}
	
	// -------- Convert and compare --------

    /** The Java URI object that is this URL. */
	public final URI uri;
	
	/** The whole address. */
	public String toString() { return address; }
	
	/** Determine if this Url object is the same as a given one. */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Url)) return false;
		return uri.equals(((Url)o).uri); // Compare the Java URI objects
	}

	// -------- Methods to URL encode and decode text --------

	/** URL-encode text, turning "some text" into "some%20text". */
	public static String encode(String s) {
		try {
			return URLEncoder.encode(s, Convert.encoding);
		} catch (UnsupportedEncodingException e) { throw new PlatformException(); }
	}
	
	/** Encode spaces into "%20", leaving other characters like "/" alone. */
	public static String encodeSpacesOnly(String s) {
		return Text.replace(s, " ", "%20");
	}

	/** Decode URL-encoded text, turning "some%20text" back into "some text". */
	public static String decode(String s) {
		try {
			return URLDecoder.decode(s, Convert.encoding);
		} catch (UnsupportedEncodingException e) { throw new PlatformException(); }
	}

	// -------- File name for saving the file --------

	/** Compose a Name for the file at this address to use to save it to the disk. */
	public Name name() {
		Name n;
		if (path.name().hasText())
			n = path.name().safe().lower(); // The Url has a file name, use it
		else
			n = new Name("Index at " + site, "html"); // Just the site name, say it
		return n.safe().lower(); // Replace characters that can't be on the disk, and lowercase the extension
	}
}
