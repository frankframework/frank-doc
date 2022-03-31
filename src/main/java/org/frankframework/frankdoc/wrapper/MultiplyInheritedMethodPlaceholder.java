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
package org.frankframework.frankdoc.wrapper;

import com.sun.javadoc.MethodDoc;

public class MultiplyInheritedMethodPlaceholder extends FrankMethodDocletBase {
	private FrankMethodDoclet classAncestorMethod;

	MultiplyInheritedMethodPlaceholder(FrankMethodDoclet classAncestorMethod, FrankClassDoclet declaringClass) {
		super(declaringClass);
		this.classAncestorMethod = classAncestorMethod;
	}

	@Override
	public boolean isMultiplyInheritedPlaceholder() {
		return true;
	}

	@Override
	public String getSignature() {
		return classAncestorMethod.getSignature();
	}

	@Override
	public FrankAnnotation[] getAnnotations() {
		return new FrankAnnotation[] {};
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return null;
	}

	@Override
	public FrankType getReturnType() {
		return classAncestorMethod.getReturnType();
	}

	@Override
	public int getParameterCount() {
		return classAncestorMethod.getParameterCount();
	}

	@Override
	public FrankType[] getParameterTypes() {
		return classAncestorMethod.getParameterTypes();
	}

	@Override
	public boolean isVarargs() {
		return classAncestorMethod.isVarargs();
	}

	@Override
	public String getJavaDoc() {
		return null;
	}

	@Override
	public String getJavaDocTag(String tagName) {
		return null;
	}

	@Override
	public String getName() {
		return classAncestorMethod.getName();
	}

	@Override
	public boolean isPublic() {
		return classAncestorMethod.isPublic();
	}

	@Override
	MethodDoc getOverriddenMethodDoc() {
		return classAncestorMethod.method;
	}
}
