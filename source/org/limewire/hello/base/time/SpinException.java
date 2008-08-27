package org.limewire.hello.base.time;

/** An Update object throws a SpinException to stop an exchange of events from looping too quickly. */
public class SpinException extends RuntimeException {} // Methods that throw SpinException don't have to declare it