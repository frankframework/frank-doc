package org.frankframework.frankdoc.testtarget.examples.making.mandatory.reintroduces;

public class Child extends Parent {
    // Making the attribute mandatory is the only change. We test that it is nevertheless
	// more than a technical override.
	/**
	 * @ff.mandatory
	 */
	@Override
	public void setMyAttribute(String value) {
	}
}
