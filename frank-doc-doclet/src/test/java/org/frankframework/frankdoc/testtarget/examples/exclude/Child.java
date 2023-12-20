package org.frankframework.frankdoc.testtarget.examples.exclude;

import org.frankframework.doc.Protected;

// It is important that this class does not have shown attributes.
// It only has excluded attributes. We want to test that GroupCreator
// considers it properly.
public class Child extends Parent {
	@Override
	@Protected
	public void setChildExcludedAttribute(String value) {
	}
}
