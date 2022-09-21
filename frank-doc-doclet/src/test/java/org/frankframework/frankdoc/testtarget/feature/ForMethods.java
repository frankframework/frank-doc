package org.frankframework.frankdoc.testtarget.feature;

import nl.nn.adapterframework.doc.Default;
import nl.nn.adapterframework.doc.Mandatory;

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

	public void notMandatory() {
	}

	@Mandatory(ignoreInCompatibilityMode = true)
	public void mandatoryByAnnotation() {
	}

	/**
	 * @ff.mandatory ignoreInCompatibilityMode
	 */
	public void mandatoryByTag() {
	}
}
