package org.frankframework.frankdoc.testtarget.examples.pattern.violation;

public class B {
	// Config child that satisfies patter */b/c.
	public void addC(C child) {
	}

	// Violating config child, violates pattern */c/d.
	public void addD(D child) {
	}

	// Text config child.
	public void addI(String text) {
	}
}
