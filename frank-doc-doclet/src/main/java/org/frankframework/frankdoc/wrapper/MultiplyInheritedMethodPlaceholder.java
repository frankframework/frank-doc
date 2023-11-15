/*
Copyright 2022, 2023 WeAreFrank!

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

import javax.lang.model.element.ExecutableElement;

/**
 * Placeholder for a method that is inherited from the superclass and also from interfaces in the
 * <code>implements</code> clause of a Java class.
 *
 * There is no corresponding {@link javax.lang.model.element.ExecutableElement} because this placeholder does not
 * correspond to a declared Java method. An object of this type corresponds to an inherited
 * method that may be different from the inherited method because of inheritance from the
 * interfaces implemented by the class. The Frank!Doc needs this placeholder to see that a
 * derived attribute or config child may differ from the overridden attribute or config child.
 *
 * @author martijn
 *
 */
public class MultiplyInheritedMethodPlaceholder extends FrankMethodDocletBase {
	private FrankMethodDoclet classAncestorMethod;

	MultiplyInheritedMethodPlaceholder(FrankMethodDoclet classAncestorMethod, FrankClass declaringClass) {
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

	/**
	 * This is not a declared method, so it has no declared annotations.
	 */
	@Override
	public FrankAnnotation[] getAnnotations() {
		return new FrankAnnotation[] {};
	}

	/**
	 * This is not a declared method, so it has no declared annotations. To see an inherited
	 * annotation, use method <code>getAnnotationIncludingInherited()</code> which is
	 * inherited from {@link FrankMethodDocletBase}.
	 */
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

	/**
	 * This is not a declared method, so it has no declared JavaDoc. To
	 * see the inherited JavaDoc, use method <code>getJavaDocIncludingInherited()</code>
	 * which is inherited from {@link FrankMethodDocletBase}.
	 */
	@Override
	public String getJavaDoc() {
		return null;
	}

	/**
	 * This is not a declared method, so it has no declared JavaDoc tags. To see an inherited
	 * JavaDoc tag, use method <code>getJavaDocTagIncludingInherited()</code> which is
	 * inherited from {@link FrankMethodDocletBase}.
	 */
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
	public boolean isProtected() {
		return classAncestorMethod.isProtected();
	}

	@Override
	ExecutableElement getOverriddenExecutableElement() {
		return classAncestorMethod.method;
	}

	@Override
	public String toString() {
		return String.format("%s(placeholder of [%s] in [%s])", this.getClass().getSimpleName(), classAncestorMethod.toString(), getDeclaringClass().toString());
	}
}
