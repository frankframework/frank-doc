package org.frankframework.frankdoc.testtarget.examples.parentforward;

import org.frankframework.doc.Forward;

@Forward(name = "exception", description = "When an exception occurs")
public abstract class AbstractBase {
	public void setBaseAttr(String baseAttr) {}
}
