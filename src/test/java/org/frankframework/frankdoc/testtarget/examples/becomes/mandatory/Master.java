package org.frankframework.frankdoc.testtarget.examples.becomes.mandatory;

import nl.nn.adapterframework.doc.Mandatory;

public class Master {
	@Mandatory(ignoreInCompatibilityMode = true)
	public void setBecomesMandatory(String value) {
	}

	/**
	 * @ff.mandatory ignoreInCompatibilityMode
	 * @param child
	 */
	public void registerA(Nested child) {
	}
}
