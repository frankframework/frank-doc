package org.frankframework.frankdoc.testtarget.ibisdocref;

import org.frankframework.doc.ReferTo;

public class Referrer {

	/**
	 * @ff.ref org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget
	 *
	 */
	public void setFfReferInheritedDescription(String value) {
	}

	@ReferTo(ChildTarget.class)
	public void setReferToInheritedDescription(String value) {
	}

	@ReferTo(ChildTargetParameterized.class)
	public void setReferToParameterizedType(String value) {
	}

	@ReferTo(ChildTargetParameterized.class)
	public void doesNotExistsMethod(String value) {
	}
}
