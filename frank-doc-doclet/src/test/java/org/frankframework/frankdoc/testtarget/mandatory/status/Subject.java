package org.frankframework.frankdoc.testtarget.mandatory.status;

import org.frankframework.doc.Mandatory;
import org.frankframework.doc.Optional;

public class Subject {
	public void setNotMandatory(String value) {
	}

	@Mandatory(ignoreInCompatibilityMode = true)
	public void setMandatoryIgnoreCompatibilityByAnnotation(String value) {
	}

	/**
	 * @ff.mandatory ignoreInCompatibilityMode
	 * @param value
	 */
	public void setMandatoryIgnoreCompatibilityByTag(String value) {
	}

	@Mandatory
	public void setSimplyMandatoryByAnnotation(String value) {
	}

	/**
	 * @ff.mandatory
	 * @param value
	 */
	public void setSimplyMandatoryByTag(String value) {
	}

	@Optional
	@Mandatory
	public void setOptional(String value) {
	}

	/**
	 * @ff.mandatory xxx
	 * @param value
	 */
	public void setInvalid(String value) {
	}
}
