package org.frankframework.frankdoc.testtarget.examples.config.children.excluded;

import org.frankframework.doc.Protected;

public class Child extends Parent {
	/**
	 * Documented, but still not a config child. @Protected is inherited.
	 */
	public void registerB(Item child) {
	}

	// Not documented, but the Frank!Doc should see this config child.
	// The Frank!Doc must also prevent inheritance.
	@Protected
	public void registerC(Item child) {
	}
}
