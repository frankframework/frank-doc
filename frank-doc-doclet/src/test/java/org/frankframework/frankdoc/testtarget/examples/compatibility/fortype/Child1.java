package org.frankframework.frankdoc.testtarget.examples.compatibility.fortype;

/*
 * No attributes. Attribute "className" should go into the Child1Type because
 * there is no attribute group.
 *
 * @author martijn
 *
 */
public class Child1 implements IChild {
	public void addA(FirstGrandChild grandChild) {
	}

	public void addB(SecondGrandChild grandChild) {
	}
}
