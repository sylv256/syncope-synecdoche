package io.github.rehtea.syncope.impl.util;

/// An exception useful in exception hoisting, also known as "yeeting".
public class Yeet extends RuntimeException {
	public Yeet(Throwable cause) {
		super(cause);
	}
}
