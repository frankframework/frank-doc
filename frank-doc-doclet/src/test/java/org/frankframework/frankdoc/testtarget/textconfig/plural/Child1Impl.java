package org.frankframework.frankdoc.testtarget.textconfig.plural;

public class Child1Impl implements IChild1 {
	/*
	 * A plural config child
	 */
	public void registerB(IGrandChild1 child) {
	}

	public void registerB(IGrandChild2 child) {
	}

	// This TextConfigChild is parsed by the FrankDocXsdFactory code that does plural config children.
	public void registerQ(String value) {
	}
}
