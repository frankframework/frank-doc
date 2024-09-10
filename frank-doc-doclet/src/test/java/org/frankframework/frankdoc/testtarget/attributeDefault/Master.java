package org.frankframework.frankdoc.testtarget.attributeDefault;

import org.frankframework.doc.IbisDoc;

public class Master {

	// No default, because blank
	/**
	 * @ff.default
	 */
	public void setStringExplicitNull(String s) {
	}

	public void setIntegerNoDefault(Integer i) {
	}

	/**
	 * @ff.default 50
	 */
	public void setByteWithDefault(Byte b) {
	}


	/**
	 * @ff.mandatory
	 */
	public void setMandatory(boolean b) {
	}

	// Should produce a warning
	/**
	 * @ff.default something
	 * @ff.mandatory
	 * @param s
	 */
	public void setMandatoryWithDefault(String s) {
	}

	/**
	 * attributeWithJavaDocLink with {@link MyLink}.
	 * @param s
	 */
	public void setAttributeWithJavaDocLink(String s) {
	}
}
