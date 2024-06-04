package org.frankframework.frankdoc.testtarget.examples.sameEnum.only.once;

public class Master {
	public void setFirstAttribute(MyEnum value) {
	}

	/**
	 * @ff.mandatory
	 */
	public void setSecondAttribute(MyEnum value) {
	}

	public void setThirdAttribute(MyEnum... varArgsValue) {
	}
}
