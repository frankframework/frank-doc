package org.frankframework.frankdoc.testtarget.feature;

import nl.nn.adapterframework.doc.Default;

public class ForMethods {
	@Default("myDefault")
	public void withDefaultWithAnnotation() {
	}

	/**
	 * @ff.default myDefault
	 */
	public void withDefaultByTag() {
	}

	public void withoutDefault() {
	}
}
