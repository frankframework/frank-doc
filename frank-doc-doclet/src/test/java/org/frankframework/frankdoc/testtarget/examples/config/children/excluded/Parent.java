package org.frankframework.frankdoc.testtarget.examples.config.children.excluded;

import org.frankframework.doc.Protected;

public class Parent implements IInterface {
	@Protected
	public void registerB(Item child) {
	}

	public void registerC(Item child) {
	}
}
