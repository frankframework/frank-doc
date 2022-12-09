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
