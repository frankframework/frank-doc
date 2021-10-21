package org.frankframework.frankdoc.testtarget.attributeDefault;

import nl.nn.adapterframework.doc.IbisDoc;

public class Master {
	// No default, because "null"
	@IbisDoc({"10", "Description setBooleanExplicitNull()", "null"})
	public void setBooleanExplicitNull(Boolean b) {
	}

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

	// Should produce a warning
	@IbisDoc({"20", "Description setExplicitNullOnPrimitive()", "null"})
	public void setExplicitNullOnPrimitive(short s) {
	}

	/**
	 * @ff.mandatory
	 */
	public void setMandatory(boolean b) {
	}
}
