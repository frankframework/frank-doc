package org.frankframework.frankdoc.testtarget.doclet;

import org.frankframework.doc.IbisDoc;

@Deprecated
public class DeprecatedChild extends Parent {
	@Deprecated
	@IbisDoc({"100", "Some description", "0"})
	public void someSetter(int value) {
	}

	void packagePrivateMethod() {
	}
}
