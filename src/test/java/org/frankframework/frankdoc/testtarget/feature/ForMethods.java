package org.frankframework.frankdoc.testtarget.feature;

import nl.nn.adapterframework.doc.DefaultValue;

public class ForMethods {
	@DefaultValue("myDefault")
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
