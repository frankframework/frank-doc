package org.frankframework.frankdoc.testtarget.examples.forwards;

import org.frankframework.doc.Forward;

/**
 * @ff.forward success
 *
 * @ff.forward failure When something goes wrong.
 *
 * @ff.tag myTag myValue
 * @ff.tag myOtherTag myOtherValue
 *
 * @author martijn
 *
 */
@Forward(name = "continue", description = "Description of the continue forward")
@Forward(name = "break")
public class Master extends Parent {
}
