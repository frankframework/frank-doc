package org.frankframework.frankdoc.testtarget.examples.inheritdoc;

/**
 * Description of child with Ignored tags.
 *
 * @ff.parameters All parameters
 * @ff.parameter Param1 This is a parameter
 *
 * {@inheritClassDoc}
 *
 * @author Unit Test
 */
public class Ignored extends Parent {

	/**
	 * An additional line of text.
	 * @param attribute
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setHasNoJavaDocAttribute(String attribute) {
	}

}
