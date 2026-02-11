/*
Copyright 2021, 2022 WeAreFrank!

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

import org.jspecify.annotations.Nullable;

import com.sun.source.doctree.DocCommentTree;

import lombok.extern.log4j.Log4j2;

@Log4j2
class FrankMethodDoclet extends FrankMethodDocletBase {

	final ExecutableElement method;
	private final DocCommentTree docCommentTree;
	private final Map<String, FrankAnnotation> frankAnnotationsByName;

	public FrankMethodDoclet(ExecutableElement method, FrankClass declaringClass, DocCommentTree docCommentTree) {
		super(declaringClass);
		this.method = method;
		this.docCommentTree = docCommentTree;
		AnnotationMirror[] annotationsByName = method.getAnnotationMirrors().toArray(new AnnotationMirror[]{});
		frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(annotationsByName);
	}

	@Override
	public boolean isMultiplyInheritedPlaceholder() {
		return false;
	}

	@Override
	public String getName() {
		return method.getSimpleName().toString();
	}

	@Override
	public boolean isPublic() {
		return method.getModifiers().stream().anyMatch(m -> m == Modifier.PUBLIC);
	}

	public boolean isProtected() {
		return method.getModifiers().stream().anyMatch(m -> m == Modifier.PROTECTED);
	}

	@Override
	public FrankAnnotation[] getAnnotations() {
		return frankAnnotationsByName.values().toArray(new FrankAnnotation[]{});
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return frankAnnotationsByName.get(name);
	}

	@Override
	public @Nullable String getJavaDoc() {
		return docCommentTree == null ? null : FrankDocletUtils.convertDocTreeListToStr(docCommentTree.getFullBody());
	}

	@Override
	public String getComment() {
		return docCommentTree == null ? null : docCommentTree.toString();
	}

	@Override
	public FrankType getReturnType() {
		TypeMirror docletType = method.getReturnType();
		return typeOf(docletType);
	}

	private FrankType typeOf(TypeMirror docletType) {
		if (docletType instanceof PrimitiveType || docletType instanceof NoType) {
			return new FrankPrimitiveType(docletType.toString());
		}

		String fullNameWithoutTypeInfo = FrankDocletUtils.getFullNameWithoutTypeInfo(docletType);
		try {
			FrankClass clazz = getDeclaringClass().getRepository().findClass(fullNameWithoutTypeInfo);
			if (clazz == null) {
				return getDeclaringClass().getRepository().findOrCreateNonCompiledClass(fullNameWithoutTypeInfo);
			} else {
				return clazz;
			}
		} catch (FrankDocException e) {
			log.error("Failed to search for class with name {}", fullNameWithoutTypeInfo, e);
			return getDeclaringClass().getRepository().findOrCreateNonCompiledClass(fullNameWithoutTypeInfo);
		}
	}

	@Override
	public int getParameterCount() {
		return method.getParameters().size();
	}

	@Override
	public boolean isVarargs() {
		return method.isVarArgs();
	}

	@Override
	public FrankType[] getParameterTypes() {
		return method.getParameters()
			.stream()
			.map(VariableElement::asType)
			.map(this::typeOf)
			.toArray(FrankType[]::new);
	}

	@Override
	public String getSignature() {
		List<String> components = new ArrayList<>();
		components.add(getName());
		for (FrankType type : getParameterTypes()) {
			components.add(type.getName());
		}
		return String.join(", ", components);
	}

	void removeOverriddenFrom(Map<ExecutableElement, FrankMethod> methodRepository) {
		ExecutableElement toRemove = getOverriddenExecutableElement();
		methodRepository.remove(toRemove);
	}

	void addToRepository(Map<ExecutableElement, FrankMethod> methodRepository) {
		methodRepository.put(method, this);
	}

	@Override
	public String getJavaDocTag(String tagName) {
		return FrankDocletUtils.getJavaDocTag(docCommentTree, tagName);
	}

	@Override
	public String toString() {
		return toStringImpl();
	}

	@Override
	ExecutableElement getOverriddenExecutableElement() {
		TypeElement declaringClassElement = (TypeElement) method.getEnclosingElement();
		Element overriddenMethodElement = checkSuperclassForOverriddenMethod(declaringClassElement);
		return (ExecutableElement) overriddenMethodElement;
	}

	private Element checkSuperclassForOverriddenMethod(TypeElement declaringClassElement) {
		if (declaringClassElement == null) {
			return null;
		}
		TypeElement superClazz = FrankDocletUtils.getSuperclassElement(declaringClassElement);
		if (superClazz == null) {
			return null;
		}
		Element overriddenMethodElement = superClazz.getEnclosedElements().stream()
			.filter(element -> element.getKind().equals(ElementKind.METHOD))
			.filter(element -> element.toString().equals(method.toString())) // Need to use .toString to get the full method name including parameters
			.findFirst()
			.orElse(null);
		if (overriddenMethodElement != null)
			return overriddenMethodElement;

		// When not found yet, check the superclass of the superclass
		return checkSuperclassForOverriddenMethod(superClazz);
	}
}
