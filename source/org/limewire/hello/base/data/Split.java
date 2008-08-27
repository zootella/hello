package org.limewire.hello.base.data;

/** The method Data.split() returns a DataSplit object to hold all the different parts of its answer. */
public class Split {

	/** true if Data.split() found the tag in the data. */
	public boolean found;
	
	/**
	 * The data that is before the tag.
	 * If the tag was not found, before is all the data.
	 */
	public Data before;
	
	/**
	 * The tag in the data.
	 * If the tag was not found, tag is empty.
	 */
	public Data tag;

	/**
	 * The data that is after the tag.
	 * If the tag was not found, after is empty.
	 */
	public Data after;
}
