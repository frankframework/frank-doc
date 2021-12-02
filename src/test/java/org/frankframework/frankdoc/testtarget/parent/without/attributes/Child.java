package org.frankframework.frankdoc.testtarget.parent.without.attributes;

public class Child extends ParentOnlyExcludedAttributes {
	public void setChildAttribute(String value) {
	}

	/** Not an attribute even though this is a non-technical override.
	 * The overridden method has annotation ProtectedAttribute, which
	 * is inherited.
	 */
	public void setNoAttribute(String value) {
	}
}
