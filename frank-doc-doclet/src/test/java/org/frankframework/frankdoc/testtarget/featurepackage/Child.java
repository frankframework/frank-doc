package org.frankframework.frankdoc.testtarget.featurepackage;

public class Child extends Parent {
	@Override
	public void notReferenced() {
	}

	/**
	 * @ff.ref org.frankframework.frankdoc.testtarget.featurepackage.RefTarget
	 */
	@Override
	public void referenceByFfRef() {
	}
}
