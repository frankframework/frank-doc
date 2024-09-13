package org.frankframework.frankdoc.testtarget.children;

public class Container extends ContainerParent {
	public void setChild(Child child) {
	}

	@Deprecated
	public void setDeprecatedChild(Child child) {
	}

	@Override
	public void registerInheritedChilds(InheritedChild inheritedChild) {
		super.registerInheritedChilds(inheritedChild);
	}

	@Override
	public void setInheritedChildDocOnDerived(InheritedChildDocOnDerived child) {
		super.setInheritedChildDocOnDerived(child);
	}

	public void setNoChildNotInDictionary(NoChild noChild) {
	}

	public void setAttribute(String attributeValue) {
	}

	@Override
	public void setInheritedChildDocWithDescriptionOverride(InheritedChildDocWithDescriptionOverride child) {
	}

	@Override
	public void setInheritedChildNonSelected(InheritedChildNonSelected child) {
	}

	@Override
	public void setChildOverriddenOnlyParentAnnotated(ChildOverriddenOnlyParentAnnotated child) {
	}

	// To test TextConfigChild
	public void registerText(String value) {
	}

	// Not a config child because the name starts with "set" and the argument is String.
	public void setNotConfigChildButAttribute(String value) {
	}
}
