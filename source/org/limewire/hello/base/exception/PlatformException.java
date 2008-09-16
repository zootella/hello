package org.limewire.hello.base.exception;

/**
 * Throw a PlatformException when something is wrong with the computer that the program can't fix.
 * When you catch a PlatformException, tell the user to upgrade Java and then close the program.
 */
public class PlatformException extends RuntimeException {} // Methods that throw PlatformException don't have to declare it