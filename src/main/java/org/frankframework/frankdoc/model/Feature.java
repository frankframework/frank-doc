/*
   Copyright 2021 WeAreFrank!

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

import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;

final class Feature {
	static final Feature MANDATORY = new Feature("nl.nn.adapterframework.doc.Mandatory", "@ff.mandatory");
	static final Feature DEFAULT = new Feature("nl.nn.adapterframework.doc.Default", "@ff.default");
	static final Feature DEPRECATED = new Feature("java.lang.Deprecated", "@deprecated");

	private final String javaAnnotation;
	private final String javaDocTag;

	private Feature(String javaAnnotation, String javaDocTag) {
		this.javaAnnotation = javaAnnotation;
		this.javaDocTag = javaDocTag;
	}

	boolean isSetOn(FrankMethod method) {
		return (method.getAnnotation(javaAnnotation) != null)
				|| (method.getJavaDocTag(javaDocTag) != null);
	}

	boolean isEffectivelySetOn(FrankMethod method) throws FrankDocException {
		return (method.getAnnotationIncludingInherited(javaAnnotation) != null)
				|| (method.getJavaDocTagIncludingInherited(javaDocTag) != null);
	}

	String featureValueIncludingInherited(FrankMethod method) throws FrankDocException {
		String result = method.getJavaDocTagIncludingInherited(javaDocTag);
		if(result == null) {
			FrankAnnotation annotation = method.getAnnotationIncludingInherited(javaAnnotation);
			if(annotation != null) {
				result = (String) annotation.getValue();
			}
		}
		return result;
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
