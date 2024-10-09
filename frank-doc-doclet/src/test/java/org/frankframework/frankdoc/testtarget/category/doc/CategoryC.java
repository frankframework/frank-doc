package org.frankframework.frankdoc.testtarget.category.doc;

import org.frankframework.doc.Label;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Label(name = "CategoryC")
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CategoriesC.class)
public @interface CategoryC {
	CategoryCValue value();
}
