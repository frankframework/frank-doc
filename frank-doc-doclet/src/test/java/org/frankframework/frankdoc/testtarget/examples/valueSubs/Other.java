package org.frankframework.frankdoc.testtarget.examples.valueSubs;

import org.frankframework.doc.Forward;

/**
 * In the class description, we substitute {@value WithValueSubstitutions#MY_CONSTANT}
 *
 * @ff.parameters In the text that defines the meaning of parameters, we substitute {@value WithValueSubstitutions#MY_CONSTANT}
 *
 * @ff.parameter {@value WithValueSubstitutions#MY_CONSTANT} Description of this parameter is {@value WithValueSubstitutions#MY_CONSTANT}
 */
@Forward(name = "{@value WithValueSubstitutions#MY_CONSTANT}", description = "Forward description {@value WithValueSubstitutions#MY_CONSTANT}")
public class Other {

	/**
	 * Attribute description that cites {@value WithValueSubstitutions#MY_CONSTANT}
	 * @param value
	 */
	public void setMyAttribute(String value) {
	}
}
