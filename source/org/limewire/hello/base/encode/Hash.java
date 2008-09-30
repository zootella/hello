package org.limewire.hello.base.encode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.exception.PlatformException;

/** Compute the SHA1 hash of some data. */
public class Hash {

	// Parts

	/** Make a new Hash object that can compute the SHA1 hash of some data. */
	public Hash() {
		try {
			digest = MessageDigest.getInstance("SHA"); // Ask for the SHA1 algorithm
		} catch (NoSuchAlgorithmException e) { throw new PlatformException(); }
	}

	/** Our MessageDigest object that hashes more data based on what has come before. */
	private final MessageDigest digest;

	/** Hash the next block of data, how you cut it up doesn't matter, just give add() the blocks in order. */
	public void add(Data data) {
		digest.update(data.toByteBuffer());
	}

	/** Get the 20 byte, 160 bit SHA1 hash value. */
	public Data done() {
		return new Data(digest.digest()); // Wrap a new Data object around the byte array
	}

	// All

	/** Compute the SHA1 hash of data. */
	public static Data hash(Data data) {
		Hash hash = new Hash(); // Use a Hash object
		hash.add(data);
		return hash.done();
	}

	// Define
	
	/** 20 bytes, a SHA1 hash value is 20 bytes, which is 160 bits. */
	public static final int size = 20;
}
