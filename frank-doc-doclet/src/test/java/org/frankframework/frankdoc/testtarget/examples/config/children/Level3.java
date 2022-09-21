package org.frankframework.frankdoc.testtarget.examples.config.children;

public class Level3 extends Level2 {
	/** This comment makes it documented. Not a technical override so no reuse of declaration in {@link Level1} */
	@Override
	public void registerA(IChild child) {
	}

	public void registerD(IChild child) {
	}
}
