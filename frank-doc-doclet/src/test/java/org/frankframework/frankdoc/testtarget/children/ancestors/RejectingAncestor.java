package org.frankframework.frankdoc.testtarget.children.ancestors;

public class RejectingAncestor extends SelectingAncestor {
	// Technical override, so not a child
	public void setAttribute1(String value) {
	}

	// Technical override, so not a child
	public void addB(Nested1 child) {
	}

	@Deprecated
	public void setSelectedAttribute(String value) {
	}

	@Deprecated
	public void addC(Nested2 child) {
	}
}
