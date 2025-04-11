package org.frankframework.frankdoc.testtarget.examples.inheritdoc;

/**
 * Description of child.
 * {@inheritClassDoc}
 */
public class Child extends Parent {

	/**
	 * {@inheritDoc}
	 *
	 * With a bit of additional text.
	 */
	@Override
	public void setInheritedAttribute(String attribute) {
	}

	@Override
	public void setNonInheritedAttribute(String attribute) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * An additional line of text.
	 * @param attribute
	 */
	@Override
	public void setHasNoJavaDocAttribute(String attribute) {
	}

}
