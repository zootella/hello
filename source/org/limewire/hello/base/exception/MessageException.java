package org.limewire.hello.base.exception;

/**
 * Throw a MessageException when you're parsing a message and come to a mistake.
 * When you catch a MessageException, skip that message and try parsing the next one.
 */
public class MessageException extends Exception {} // Methods that throw MessageException have to declare it