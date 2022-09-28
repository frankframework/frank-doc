package org.frankframework.frankdoc.testtarget.examples.deprecated.enumValue;

public class Master {
	public enum MyEnum {
		FIRST,

		@Deprecated
		SECOND;
	}

	public void setMyAttribute(MyEnum value) {
	}
}
