package org.limewire.hello.base.exception;

/**
 * Throw a ChopException when you're parsing data and run out because the end of the data has been chopped off.
 * When you catch a ChopException, try parsing into the data again later after more has arrived.
 */
public class ChopException extends Exception {} // Methods that throw ChopException have to declare it