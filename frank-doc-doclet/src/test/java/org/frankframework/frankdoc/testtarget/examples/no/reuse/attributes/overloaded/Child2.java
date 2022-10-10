package org.frankframework.frankdoc.testtarget.examples.no.reuse.attributes.overloaded;

public class Child2 extends Parent2 {
	/**
	 * Because of this method, attribute "overloaded" is not part
	 * of Child2CumulativeChildGroup. Attribute cannot be reused though
	 * because the attribute name "overloaded" is reused in
	 * Parent and Child.
	 *
	 * @ff.protected
	 */
	@Override
	public void setModified(String value) {
	}
}
