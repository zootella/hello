package org.limewire.hello.base.data;

/** The method Text.split() returns a TextSplit object to hold all the different parts of its answer. */
public class TextSplit {

	/** true if Text.split() found the tag in the text. */
	public boolean found;
	
	/**
	 * The text before the tag.
	 * If the tag was not found, before is all of the text.
	 */
	public String before;
	
	/**
	 * The text after the tag.
	 * If the tag was not found, after is "".
	 */
	public String after;
}
