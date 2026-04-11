package io.github.rehtea.syncope.impl.util;

@FunctionalInterface
public interface FallibleRunnable<X extends Throwable> {
	void run() throws X;
}
