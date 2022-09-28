package org.frankframework.frankdoc.testtarget.examples.reintroduce;

import nl.nn.adapterframework.doc.Reintroduce;

/**
 * The attribute and the config child are the same as in parent and they are not documented.
 * They should still be declared as attribute and config child of Child because of the
 * @ff.reintroduce and the @Reintroduce.
 *
 * @author martijn
 *
 */
public class Child extends Parent {
	/**
	 * @ff.reintroduce
	 */
	public void setAttribute(String value) {
	}

	@Reintroduce
	public void registerB(MyConfigChild child) {
	}
}
