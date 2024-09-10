package org.frankframework.frankdoc.testtarget.reflect;

import org.frankframework.doc.IbisDoc;

public class FrankAttributeTargetParent {
	@IbisDoc({"1000", "This one should not count as documenting the override in FrankAttributeTarget"})
	public void setAttributeOnlySetter(String value) {
	}

	/**
	 * JavaDoc of FrankAttributeTargetParent.setAttributeWithInheritedJavaDoc()
	 * @param value
	 */
	public void setAttributeWithInheritedJavaDoc(String value) {
	}

	/**
	 * @ff.default My inherited default value
	 */
	public void setAttributeWithInheritedJavaDocDefault(String value) {
	}
}
