package org.frankframework.frankdoc.testtarget.examples.forwards;

import org.frankframework.doc.Forward;

/**
 * @author martijn
 */
@Forward(name = "success")
@Forward(name = "failure", description = "When something goes wrong.")
@Forward(name = "continue", description = "Description of the continue forward")
@Forward(name = "break")
public class Master extends Parent {
}
