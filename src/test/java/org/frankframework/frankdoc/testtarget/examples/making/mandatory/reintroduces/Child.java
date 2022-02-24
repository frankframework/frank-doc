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

	/**
	 * @ff.optional
	 * @param value
	 */
	public void setMandatoryAttributeThatBecomesOptional(String value) {
	}

	/**
	 * This attribute is documented. It should be re-introduced with the child and it
	 * should inherit the @ff.mandatory from the parent.
	 * @param value
	 */
	public void setMandatoryAttributeThatRemainsMandatory(String value) {
	}
}
