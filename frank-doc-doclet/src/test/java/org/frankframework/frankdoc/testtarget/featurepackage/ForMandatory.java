package org.frankframework.frankdoc.testtarget.featurepackage;

import nl.nn.adapterframework.doc.Mandatory;

public class ForMandatory {
	public void notMandatory() {
	}

	@Mandatory(ignoreInCompatibilityMode = true)
	public void mandatoryByAnnotationIgnore() {
	}

	/**
	 * @ff.mandatory ignoreInCompatibilityMode
	 */
	public void mandatoryByTagIgnore() {
	}

	@Mandatory
	public void mandatoryByAnnotationNoIgnore() {
	}

	/**
	 * @ff.mandatory
	 */
	public void mandatoryByTagNoIgnore() {
	}
}
