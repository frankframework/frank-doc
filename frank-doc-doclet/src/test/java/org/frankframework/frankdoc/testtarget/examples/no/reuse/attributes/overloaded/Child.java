package org.frankframework.frankdoc.testtarget.examples.no.reuse.attributes.overloaded;

public class Child extends Parent {
	/**
	 * Because of this method, attribute "overloaded" is not part
	 * of ChildCumulativeChildGroup. Attribute cannot be reused though
	 * because the attribute name "overloaded" is reused in
	 * Parent2 and Child2.
	 *
	 * @ff.protected
	 */
	@Override
	public void setModified(String value) {
	}
}
