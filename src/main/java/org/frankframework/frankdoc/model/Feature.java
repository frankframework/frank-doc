package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;

final class Feature {
	static final Feature MANDATORY = new Feature("nl.nn.adapterframework.doc.MandatoryAttribute", "@ff.mandatory");
	static final Feature DEFAULT = new Feature("nl.nn.adapterframework.doc.DefaultValue", "@ff.default");
	static final Feature DEPRECATED = new Feature("java.lang.Deprecated", "@deprecated");

	private final String javaAnnotation;
	private final String javaDocTag;

	private Feature(String javaAnnotation, String javaDocTag) {
		this.javaAnnotation = javaAnnotation;
		this.javaDocTag = javaDocTag;
	}

	boolean hasFeature(FrankMethod method) {
		return (method.getAnnotation(javaAnnotation) != null)
				|| (method.getJavaDocTag(javaDocTag) != null);
	}

	boolean hasOrInheritsFeature(FrankMethod method) throws FrankDocException {
		return (method.getAnnotationIncludingInherited(javaAnnotation) != null)
				|| (method.getJavaDocTagIncludingInherited(javaDocTag) != null);
	}

	String featureValueIncludingInherited(FrankMethod method) throws FrankDocException {
		String result = method.getJavaDocTagIncludingInherited(javaDocTag);
		if(result == null) {
			// TODO: Unit test this.
			FrankAnnotation annotation = method.getAnnotationIncludingInherited(javaAnnotation);
			if(annotation != null) {
				result = (String) annotation.getValue();
			}
		}
		return result;
	}

	boolean hasFeature(FrankEnumConstant c) throws FrankDocException {
		return (c.getAnnotation(javaAnnotation) != null)
				|| (c.getJavaDocTag(javaDocTag) != null);
	}

	boolean hasFeature(FrankClass clazz) {
		return (clazz.getAnnotation(javaAnnotation) != null)
				|| (clazz.getJavaDocTag(javaDocTag) != null);		
	}
}
