package org.frankframework.frankdoc.testtarget.examples.compatibility.multiple;

public class Child1Impl implements IChild1 {
	public void addB(IGrandChild1 child) {
	}

	public void addB(IGrandChild2 child) {
	}

	public void addD(GrandChild3 child) {
	}

	public void setMyThirdAttribute(int value) {
	}

	public void setMyFourthAttribute(boolean value) {
	}
}
