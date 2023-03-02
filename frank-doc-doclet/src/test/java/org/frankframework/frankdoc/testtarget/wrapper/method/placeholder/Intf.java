package org.frankframework.frankdoc.testtarget.wrapper.method.placeholder;

public interface Intf {
	default String pureInterfaceMethod() {
		return null;
	}

	void reintroducedMethod();
	void childMethodAlsoInInterface();
}
