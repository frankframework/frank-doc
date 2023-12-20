package org.frankframework.frankdoc.testtarget.walking;

import org.frankframework.doc.IbisDoc;

public class GrandChild extends Child {
	@IbisDoc("Some description")
	@Override
	public void setParentAttributeSecond(String value) {
	}

	public void setGrandChildAttribute(String value) {
	}
}
