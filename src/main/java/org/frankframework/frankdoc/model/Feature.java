/*
   Copyright 2022 WeAreFrank!

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.Constants;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;

enum Feature {
	MANDATORY("nl.nn.adapterframework.doc.Mandatory", Constants.JAVA_DOC_TAG_MANDATORY, Constants.IGNORE_COMPATIBILITY_MODE),
	OPTIONAL("nl.nn.adapterframework.doc.Optional", "@ff.optional"),
	DEFAULT("nl.nn.adapterframework.doc.Default", "@ff.default"),
	DEPRECATED("java.lang.Deprecated", "@deprecated"),
	PROTECTED("nl.nn.adapterframework.doc.Protected", "@ff.protected");

	private final String javaAnnotation;
	private final String javaDocTag;
	private final String fieldName;

	private Feature(String javaAnnotation, String javaDocTag, String fieldName) {
		this.javaAnnotation = javaAnnotation;
		this.javaDocTag = javaDocTag;
		this.fieldName = fieldName;
	}

	private Feature(String javaAnnotation, String javaDocTag) {
		this.javaAnnotation = javaAnnotation;
		this.javaDocTag = javaDocTag;
		this.fieldName = null;
	}

	boolean isSetOn(FrankMethod method) {
		return (method.getAnnotation(javaAnnotation) != null)
				|| (method.getJavaDocTag(javaDocTag) != null);
	}

	boolean isEffectivelySetOn(FrankMethod method) throws FrankDocException {
		return (method.getAnnotationIncludingInherited(javaAnnotation) != null)
				|| (method.getJavaDocTagIncludingInherited(javaDocTag) != null);
	}

	String valueOf(FrankMethod method) throws FrankDocException {
		String result = method.getJavaDocTagIncludingInherited(javaDocTag);
		if(result == null) {
			FrankAnnotation annotation = method.getAnnotationIncludingInherited(javaAnnotation);
			if(annotation != null) {
				result = getValueFromAnnotation(annotation);
			}
		}
		return result;
	}

	private String getValueFromAnnotation(FrankAnnotation a) throws FrankDocException {
		if(fieldName == null) {
			return a.getValue().toString();
		} else {
			Object value = a.getValueOf(fieldName);
			return (value == null) ? null : value.toString();
		}
	}

	boolean isSetOn(FrankEnumConstant c) throws FrankDocException {
		return (c.getAnnotation(javaAnnotation) != null)
				|| (c.getJavaDocTag(javaDocTag) != null);
	}

	boolean isSetOn(FrankClass clazz) {
		return (clazz.getAnnotation(javaAnnotation) != null)
				|| (clazz.getJavaDocTag(javaDocTag) != null);		
	}
}
