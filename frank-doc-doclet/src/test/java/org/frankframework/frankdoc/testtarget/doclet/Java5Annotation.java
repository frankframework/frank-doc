package org.frankframework.frankdoc.testtarget.doclet;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Java5Annotation {
	String[] myStringArray();
	String myString();
	int myInt();
	boolean myBoolean();
}
