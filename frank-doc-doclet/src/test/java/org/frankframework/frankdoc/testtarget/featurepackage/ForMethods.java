package org.frankframework.frankdoc.testtarget.featurepackage;

import org.frankframework.doc.Optional;

public class ForMethods {
	@Optional
	public void withOptionalWithAnnotation() {
	}

	/**
	 * @ff.optional
	 */
	public void withOptionalByTag() {
	}

	public void withoutOptional() {
	}
}
