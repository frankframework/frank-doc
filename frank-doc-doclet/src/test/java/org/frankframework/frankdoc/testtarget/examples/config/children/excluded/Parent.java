package org.frankframework.frankdoc.testtarget.examples.config.children.excluded;

import nl.nn.adapterframework.doc.Protected;

public class Parent implements IInterface {
	@Protected
	public void registerB(Item child) {		
	}

	public void registerC(Item child) {
	}
}
