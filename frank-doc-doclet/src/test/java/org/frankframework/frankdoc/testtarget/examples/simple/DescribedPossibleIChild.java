package org.frankframework.frankdoc.testtarget.examples.simple;

import org.frankframework.doc.Mandatory;

/**
 * This is the header of the JavaDoc of "DescribedPossibleIChild".
 *
 * And this is remaining documentation of "DescribedPossibleIChild".
 * @author martijn
 *
 */
public class DescribedPossibleIChild implements IChild {
	@Mandatory
	public void setFirstAttribute(String value) {
	}

	public void setSecondAttribute(String value) {
	}
}
