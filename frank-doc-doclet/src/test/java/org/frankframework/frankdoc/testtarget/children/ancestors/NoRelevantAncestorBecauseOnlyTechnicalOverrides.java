package org.frankframework.frankdoc.testtarget.children.ancestors;

public class NoRelevantAncestorBecauseOnlyTechnicalOverrides extends DefaultAncestor {
	// Technical override, so not a child
	public void setAttribute1(String value) {
	}

	// Technical override, so not a child
	public void registerB(Nested1 child) {
	}
}
