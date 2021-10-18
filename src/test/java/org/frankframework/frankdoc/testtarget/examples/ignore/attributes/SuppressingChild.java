package org.frankframework.frankdoc.testtarget.examples.ignore.attributes;

/** @ff.ignoreTypeMembership org.frankframework.frankdoc.testtarget.examples.ignore.attributes.IChild1 */
public class SuppressingChild extends Parent implements IChild2 {
	public void setChildAttribute(String value) {
	}

	/** @ff.noAttribute */
	public void setNotChildAttribute(String value) {
	}
}
