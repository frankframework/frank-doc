package org.frankframework.frankdoc.testtarget.wrapper.annotations.of.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copied from IAF project.
 * 
 * @author Jaco de Groot
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
