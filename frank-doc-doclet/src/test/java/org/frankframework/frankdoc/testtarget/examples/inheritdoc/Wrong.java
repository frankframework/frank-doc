package org.frankframework.frankdoc.testtarget.examples.inheritdoc;

/**
 * Description of child with the wrong tag.
 * {@inheritDoc}
 *
 * @author Unit Test
 */
public class Wrong extends Parent {

	/**
	 * An additional line of text.
	 * {@inheritClassDoc}
	 *
	 * @param attribute
	 */
	@Override
	public void setHasNoJavaDocAttribute(String attribute) {
	}

}
