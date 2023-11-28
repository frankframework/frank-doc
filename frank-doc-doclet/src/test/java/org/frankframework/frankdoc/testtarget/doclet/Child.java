package org.frankframework.frankdoc.testtarget.doclet;

import org.frankframework.frankdoc.testtarget.examples.config.children.IChild;

/**
 * This is test class "Child". We use this comment to see how
 * JavaDoc text is treated by the Doclet API.
 * @author martijn
 *
 * @ff.myTag This is the tag argument.
 */
public class Child<M> extends Parent<M> implements MyInterface {
	@Override
	public void setInherited(String value, Parent<M> parent, Parent<?> clas, Parent<?>[] parameterTypes) {
	}

	String packagePrivateMethod() {
		return null;
	}

	private void privateMethod() {
	}

	public void setVarargMethod(String ...value) {
	}

	public enum MyInnerEnum {
		INNER_FIRST,

		// It would be nice if the JavaDoc could go after the Java 5 annotation.
		// The doclet API does not support that, however.
		/** Description of INNER_SECOND */ @Java5Annotation(myStringArray = {"a", "b"}, myString = "s", myInt = 4, myBoolean = false)
		INNER_SECOND};

	public MyInnerEnum getMyInnerEnum() {
		return null;
	}

	@Override
	public void myAnnotatedMethod() {
	}

	public void methodWithoutAnnotations() {
	}

	public static class ResponseValidatorWrapper implements IChild {
		// No methods needed
	}

	protected String getProtectedStuff() {
		return "protectedStuff";
	}
}
