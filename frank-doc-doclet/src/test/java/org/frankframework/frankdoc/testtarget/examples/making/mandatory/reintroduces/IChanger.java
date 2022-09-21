package org.frankframework.frankdoc.testtarget.examples.making.mandatory.reintroduces;

public interface IChanger {
	/**
	 * @ff.optional
	 * @param value
	 */
	void setMandatoryAttributeThatBecomesOptional(String value);

	/**
	 * @ff.optional
	 * @param child
	 */
	void registerB(ConfigChild child);
}
