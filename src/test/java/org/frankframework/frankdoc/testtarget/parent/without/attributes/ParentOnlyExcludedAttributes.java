package org.frankframework.frankdoc.testtarget.parent.without.attributes;

import nl.nn.adapterframework.doc.ProtectedAttribute;

public class ParentOnlyExcludedAttributes implements IInterface {
	/**
	 * @param value
	 */
	@ProtectedAttribute
	public void setNoAttribute(String value) {
	}
}
