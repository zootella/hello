package org.limewire.hello.base.exception;

/**
 * Throw a ProgramException when something has gone wrong in the program that restarting the program might fix.
 * When you catch a ProgramException, restart the program.
 */
public class ProgramException extends RuntimeException {} // Methods that throw ProgramException don't have to declare it