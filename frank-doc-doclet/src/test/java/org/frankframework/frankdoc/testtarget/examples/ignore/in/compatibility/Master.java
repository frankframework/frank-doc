package org.frankframework.frankdoc.testtarget.examples.ignore.in.compatibility;

import org.frankframework.doc.Mandatory;

public class Master {
	@Mandatory(ignoreInCompatibilityMode = true)
	public void setBecomesMandatory(String value) {
	}

	/**
	 * @ff.mandatory ignoreInCompatibilityMode
	 * @param child
	 */
	public void addA(Nested child) {
	}
}
