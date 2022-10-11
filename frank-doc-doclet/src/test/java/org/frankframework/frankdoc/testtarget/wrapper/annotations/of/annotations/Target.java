package org.frankframework.frankdoc.testtarget.wrapper.annotations.of.annotations;

public class Target {
	@Annotation(intValue=5, stringValue="myString", stringArrayValue= {"value 1", "value 2"}, boolValue=true)
	public void someAnnotatedMethod() {
	}
}
