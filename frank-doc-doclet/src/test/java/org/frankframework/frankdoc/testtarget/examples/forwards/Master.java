package org.frankframework.frankdoc.testtarget.examples.forwards;

import org.frankframework.doc.Forward;

/**
 * @ff.forward success
 *
 * @ff.forward failure When something goes wrong.
 *
 * @author martijn
 *
 */
@Forward(name = "continue", description = "Description of the continue forward")
@Forward(name = "break")
public class Master extends Parent {
}
