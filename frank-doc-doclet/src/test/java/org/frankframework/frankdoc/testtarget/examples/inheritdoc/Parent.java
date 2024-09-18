package org.frankframework.frankdoc.testtarget.examples.inheritdoc;

/**
 * Description of parent.
 * {@inheritDoc}
 */
public class Parent extends ParentOfParent {

	/**
	 * Documentation of the inherited attribute from the parent.
	 */
	public void setInheritedAttribute(String attribute) {
	}

	/**
	 * Documentation of the non-inherited attribute from the parent.
	 */
	public void setNonInheritedAttribute(String attribute) {
	}

	public void setHasNoJavaDocAttribute(String attribute) {
	}

}
