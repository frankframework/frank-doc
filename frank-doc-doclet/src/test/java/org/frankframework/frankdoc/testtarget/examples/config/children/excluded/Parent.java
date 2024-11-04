package org.frankframework.frankdoc.testtarget.examples.config.children.excluded;

import org.frankframework.doc.Protected;

public class Parent implements IInterface {
	@Protected
	public void addB(Item child) {
	}

	public void addC(Item child) {
	}
}
