package org.limewire.hello.base.encode;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.state.PlatformException;

/**
 * Compute the SHA1 hash of a lot of data by giving it to a Sha object in parts.
 * Or, call Sha.hash() to hash some data all at once.
 */
public class Sha {

	// -------- Make a Sha object to hash data in parts --------

	/** Make a new Sha object that can compute the SHA1 hash of some data. */
	public Sha() {
		try {
			digest = MessageDigest.getInstance("SHA"); // Ask for the SHA1 algorithm
		} catch (NoSuchAlgorithmException e) { throw new PlatformException(); }
	}

	/** A Sha object has a Java MessageDigest object that hashes more data based on what has come before. */
	private MessageDigest digest;

	/**
	 * Have this Sha object hash the next block of data.
	 * How you cut up the data won't affect the hash value done() gives you at the end.
	 * Just give add() all the pieces in order.
	 */
	public void add(Data d) {
		digest.update(d.toByteBuffer());
	}

	/**
	 * We're done giving this Sha object data to hash, get the hash value it computed.
	 * @return The 160 bit, 20 byte SHA1 hash value
	 */
	public Data done() {
		return new Data(digest.digest()); // Wrap a new Data object around the byte array
	}

	// -------- Use this method when you have all the data at once --------

	/**
	 * Compute the SHA1 hash of some data.
	 * @return The 160 bit, 20 byte SHA1 hash value
	 */
	public static Data hash(Data d) {
		Sha sha = new Sha(); // Make and use a Sha object
		sha.add(d);
		return sha.done();
	}
	
	
	
	
	/** 20 bytes, a SHA1 hash value is 20 bytes, which is 160 bits. */
	public static final int size = 20;
}
