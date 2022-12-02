package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;

abstract class AbstractNonValuedFeature {
	private final String javaAnnotation;
	private final String javaDocTag;

	AbstractNonValuedFeature(String javaAnnotation, String javaDocTag) {
		this.javaAnnotation = javaAnnotation;
		this.javaDocTag = javaDocTag;
	}

	public boolean isSetOn(FrankMethod method) {
		return (method.getAnnotation(javaAnnotation) != null)
				|| (method.getJavaDocTag(javaDocTag) != null);
	}

	public boolean isSetOn(FrankEnumConstant c) throws FrankDocException {
		return (c.getAnnotation(javaAnnotation) != null)
				|| (c.getJavaDocTag(javaDocTag) != null);
	}

	public boolean isSetOn(FrankClass clazz) {
		return (clazz.getAnnotation(javaAnnotation) != null)
				|| (clazz.getJavaDocTag(javaDocTag) != null);		
	}
}
