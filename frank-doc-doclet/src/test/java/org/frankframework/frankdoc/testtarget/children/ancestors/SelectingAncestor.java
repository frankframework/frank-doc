package org.frankframework.frankdoc.testtarget.children.ancestors;

public class SelectingAncestor extends NoRelevantAncestorBecauseOnlyTechnicalOverrides {
	// Technical override, so not a child
	public void setAttribute1(String value) {
	}

	// Technical override, so not a child
	public void addB(Nested1 child) {
	}

	public void setSelectedAttribute(String value) {
	}

	public void addC(Nested2 child) {
	}
}
