package org.frankframework.frankdoc.testtarget.category.doc;

import org.frankframework.doc.Label;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Label(name = "CategoryA")
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryA {
	CategoryAValue value();
}
