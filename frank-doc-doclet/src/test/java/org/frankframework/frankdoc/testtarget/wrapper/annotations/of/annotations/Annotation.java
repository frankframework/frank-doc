package org.frankframework.frankdoc.testtarget.wrapper.annotations.of.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Test annotation, has no meaning
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Annotation {

	String stringValue();
	int intValue();
	String valueWithDefault() default "theDefault";
	String[] stringArrayValue();
	boolean boolValue();
	MyEnum myEnumField();
}
