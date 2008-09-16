package org.limewire.hello.base.file;

/**
 * Throw a FileException when something you tried to do on the disk didn't work.
 * When you catch a FileException, give up and tell the user it's the disk's fault.
 */
public class FileException extends Exception {} // Methods that throw FileException have to declare it

//TODO rename DiskException
//TODO why not just use IOException
