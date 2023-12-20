package org.frankframework.frankdoc.testtarget.featurepackage;

import org.frankframework.doc.IbisDocRef;

public class Child extends Parent {
	@Override
	public void notReferenced() {
	}

	@IbisDocRef("org.frankframework.frankdoc.testtarget.featurepackage.RefTarget")
	@Override
	public void referencedByIbisDocRef() {
	}

	@IbisDocRef({"10", "org.frankframework.frankdoc.testtarget.featurepackage.RefTarget"})
	public void referencedByIbisDocRefWithDummyOrder() {
	}

	@IbisDocRef("org.frankframework.frankdoc.testtarget.featurepackage.RefTarget.otherMethod")
	@Override
	public void referencedByIbisDocRefOtherMethod() {
	}

	/**
	 * @ff.ref org.frankframework.frankdoc.testtarget.featurepackage.RefTarget
	 */
	@Override
	public void referenceByFfRef() {
	}
}
