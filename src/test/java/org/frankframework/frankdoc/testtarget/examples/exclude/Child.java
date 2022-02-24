package org.frankframework.frankdoc.testtarget.examples.exclude;

import nl.nn.adapterframework.doc.Protected;

// It is important that this class does not have shown attributes.
// It only has excluded attributes. We want to test that GroupCreator
// considers it properly.
public class Child extends Parent {
	@Protected
	public void setChildExcludedAttribute(String value) {
	}
}
