package org.frankframework.frankdoc.testtarget.wrapper.variables;

public class Constants extends Parent implements IMailFileSystem<Integer> {
	public static final int INT_CONSTANT = 7;

	public enum MyEnum {
		ENUM_CONSTANT;
	}
}
