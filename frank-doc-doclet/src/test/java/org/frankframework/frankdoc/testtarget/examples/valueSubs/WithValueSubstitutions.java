package org.frankframework.frankdoc.testtarget.examples.valueSubs;

import org.frankframework.doc.Forward;

/**
 * In the class description, we substitute {@value #MY_CONSTANT}
 *
 * @ff.parameters In the text that defines the meaning of parameters, we substitute {@value #MY_CONSTANT}
 *
 * @ff.parameter {@value #MY_CONSTANT} Description of this parameter is {@value #MY_CONSTANT}
 */
@Forward(name = "{@value #MY_CONSTANT}", description = "Forward description {@value #MY_CONSTANT}")
public class WithValueSubstitutions {
	public static final String MY_CONSTANT = "my-constant";

	/**
	 * In an attribute description, we substitute {@value #MY_CONSTANT}
	 */
	public void setMyAttribute(String value) {
	}

	public void addA(Other child) {
	}
}
