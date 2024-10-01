package org.frankframework.frankdoc.testtarget.examples.simple;

import org.frankframework.doc.FrankDocGroup;
import org.frankframework.doc.FrankDocGroupValue;

@FrankDocGroup(value = FrankDocGroupValue.ERROR_MESSAGE_FORMATTER)
public class NotDescribedPossibleIChild implements IChild {
	public void setFirstAttribute(String value) {
	}

	public void setSecondAttribute(String value) {
	}

	public MyEnum getFirstAttributeEnum() {
		return MyEnum.ONE;
	}

	public void setThirdAttribute(int value) {
	}
}
