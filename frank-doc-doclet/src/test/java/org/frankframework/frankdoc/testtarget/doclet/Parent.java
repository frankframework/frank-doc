package org.frankframework.frankdoc.testtarget.doclet;

import java.util.List;

/** @ff.myTag */
@Java5Annotation(myStringArray = {"first", "second"}, myString = "A string", myInt = 5, myBoolean = true)
@ClassValuedAnnotation(Parent.class)
public class Parent<M> {
	// We test here that inner classes are omitted as implementations of an interface.
	public class InnerMyInterfaceImplementation implements MyInterface {
		@Override
		public void myAnnotatedMethod() {
		}
	}

	void hello(List<M> inheritedValueType) {
	}

	// There are spaces around the @ff.default value, please leave them! We test that the value is trimmed.
	/**
	 * This is the JavaDoc of method "setInherited".
	 * @param value
	 * @ff.default   Default
	 */
	public void setInherited(String value, Parent<M> parent, Parent<?> clas, Parent<?>[] parameterTypes) {
	}

	public String getInherited() {
		return null;
	}

	// Asking this JavaDoc tag should produce an empty string.
	/** @ff.default */
	public void myMethod() {
	}

	@ClassValuedAnnotation(Parent.class)
	public void withClassValuedAnnotation() {
	}
}
