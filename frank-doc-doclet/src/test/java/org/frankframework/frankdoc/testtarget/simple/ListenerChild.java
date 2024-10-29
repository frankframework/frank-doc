package org.frankframework.frankdoc.testtarget.simple;

public class ListenerChild extends ListenerParent {
	@Override
	public void setChildAttribute(String value) {
	}

	public String getChildAttribute() {
		return null;
	}

	@Override
	public void setInheritedAttribute(String value) {
	}

	public void setListener(IListener listener) {
	}

	public void invalidConfigChildSetterTwoArgs(IListener first, IListener second) {
	}

	public int invalidConfigChildSetterReturnsInt(IListener listener) {
		return 0;
	}

	public String invalidConfigChildSetterReturnsString(IListener listener) {
		return null;
	}

	@Override
	public void setDeprecatedInParentAttribute(String value) {
	}

	public void addTextConfigChild(String value) {
	}

	// This one becomes an attribute of ListenerChild
	public void setNotTextConfigChildButAttribute(String value) {
	}
}
