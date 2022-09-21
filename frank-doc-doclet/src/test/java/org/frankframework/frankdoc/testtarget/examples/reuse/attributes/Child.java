package org.frankframework.frankdoc.testtarget.examples.reuse.attributes;

public class Child extends Parent {
	/**
	 * Documentation in Child - attribute reusedAttribute
	 * does not come from parent. Therefore reusedAttribute
	 * appears both in ParentDeclaredChildGroup and ChildCumulativeChildGroup.
	 * We test that that attribute is reused.
	 * 
	 * There are two different attributes overriddenAttribute; one
	 * in Parent and one in Child. These should be inline.
	 */
	@Override
	public void setOverriddenAttribute(String value) {
	}
}
