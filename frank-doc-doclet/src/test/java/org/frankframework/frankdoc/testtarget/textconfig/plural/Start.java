package org.frankframework.frankdoc.testtarget.textconfig.plural;

public class Start {
	/*
	 * This is a plural config child. We want to cover the FrankDocXsdFactory code
	 * for plural config children.
	 */
	public void addA(IChild1 child) {
	}

	public void addA(IChild2 child) {
	}

	// This TextConfigChild is parsed using the FrankDocXsdFactory code for plural config children.
	public void addP(String value) {
	}
}
