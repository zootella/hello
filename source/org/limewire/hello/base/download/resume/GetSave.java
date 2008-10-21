package org.limewire.hello.base.download.resume;

import org.limewire.hello.base.file.Name;
import org.limewire.hello.base.file.Open;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.size.Range;
import org.limewire.hello.base.size.StripePattern;
import org.limewire.hello.base.web.Url;

public class GetSave {
	
	// Make

	/** Group information to save about the state of a web download into a new GetSave object. */
	public GetSave(Url url, Range range, Name name, Path path, StripePattern pattern, String cannot) {
		this.url = url;
		this.range = range;
		this.name = name;
		this.path = path;
		this.pattern = pattern;
		this.cannot = cannot;
	}

	// Look
	
	/** The Url we download. */
	public Url url;
	/** The total size of the file we download, -1 unknown. */
	public Range range;
	/** The file name and extension of the file we download, from the url or web server response headers. */
	public Name name;
	/** The Path to the file we save. */
	public Path path;
	/** A StripePattern that shows where data is in the file we save. */
	public StripePattern pattern;
	/** Text for the user that describes why we had to give up, null if no problem. */
	public String cannot;
	
	// Analyze

	/** true if this GetSave describes a web download successfully saved to disk. */
	public boolean saved() {
		return
			cannot == null   &&             // No reason why we had to give up
			path != null     &&             // path exists
			range.hasLimit() &&             // We know how big the file is
			pattern.isComplete(range.size); // The data is in a single big Stripe at the start that size
	}

	/** Make an Open object to open the file to write more downloaded data to it. */
	public Open open() {
		return null;
		
		
		
	}
	
	
	public String status() {
//		return "Get " + list.size();
		return "";
	}
	
	public String name() {
		return "";
	}
	public String size() {
		return "";
	}
	public String type() {
		return "";
	}
	
	public String savedTo() {
		return "";
	}
	
	

}
