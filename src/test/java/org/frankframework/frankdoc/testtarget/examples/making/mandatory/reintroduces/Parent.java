package org.frankframework.frankdoc.testtarget.examples.making.mandatory.reintroduces;

public class Parent implements IInterface {
	/**
	 * This attribute is not mandatory here.
	 * @param value
	 */
	public void setMyAttribute(String value) {
	}

	/**
	 * @ff.mandatory
	 * @param value
	 */
	public void setMandatoryAttributeThatBecomesOptional(String value) {
	}

	/**
	 * @ff.mandatory
	 * @param value
	 */
	public void setMandatoryAttributeThatRemainsMandatory(String value) {
	}

	/**
	 * @ff.mandatory
	 * @param child
	 */
	public void registerB(ConfigChild child) {
	}

	/**
	 * @ff.mandatory
	 * @param child
	 */
	public void registerC(ConfigChild2 child) {
	}
}
