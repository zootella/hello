package org.limewire.hello.base.exception;

/**
 * Throw a PeerException when a remote peer you're communicating with says something impossible, a mistake.
 * When you catch a PeerException, disconnect from the remote peer.
 */
public class PeerException extends Exception {} // Methods that throw PeerException have to declare it