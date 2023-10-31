/*
Copyright 2021 - 2023 WeAreFrank!

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

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class FrankAnnotationDoclet implements FrankAnnotation {
	private static final Logger log = LogUtil.getLogger(FrankAnnotationDoclet.class);
	private static final Set<String> RECURSIVE_ANNOTATIONS = new HashSet<>(Arrays.asList(
		"java.lang.annotation.Documented",
		"java.lang.annotation.Retention",
		"java.lang.annotation.Target"));

	private final AnnotationMirror annotation;
	private final Map<String, FrankAnnotation> frankAnnotationsByName;

	FrankAnnotationDoclet(AnnotationMirror annotation) {
		log.trace("Creating FrankAnnotation for [{}]", annotation.getAnnotationType());
		this.annotation = annotation;
		AnnotationMirror[] javaDocAnnotationsOfAnnotation = annotation.getAnnotationType().getAnnotationMirrors().stream()
			.filter(a -> !RECURSIVE_ANNOTATIONS.contains(a.getAnnotationType().toString()))
			.collect(Collectors.toList())
			.toArray(new AnnotationMirror[]{});
		log.trace("Creating annotations of annotations");
//		frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(annotation.getAnnotationType().getAnnotationMirrors().toArray(new AnnotationMirror[] {}));
		frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(javaDocAnnotationsOfAnnotation);
		log.trace("Done with annotations of annotations");
	}

	@Override
	public String getName() {
		return annotation.getAnnotationType().toString();
	}

	@Override
	public boolean isPublic() {
		// TODO: This is not correct. It should be the visibility of the annotation type.
		return true;
	}

	@Override
	public Object getValue() throws FrankDocException {
		return getValueOf("value");
	}

	@Override
	public Object getValueOf(String fieldName) throws FrankDocException {
		Optional<? extends AnnotationValue> foundAnnotation = annotation.getElementValues().keySet().stream()
			.filter(executableElement -> executableElement.getSimpleName().toString().equals(fieldName))
			.map(executableElement -> annotation.getElementValues().get(executableElement))
			.findFirst();

		if (foundAnnotation.isPresent()) {
			return parseAnnotationValue(foundAnnotation.get());
		} else {
			return getAnnotationDefaultValue(annotation, fieldName);
		}
	}

	private Object parseAnnotationValue(AnnotationValue raw) {
		if (raw instanceof Attribute.Array) {
			return parseAnnotationValueAsStringArray(((Attribute.Array) raw).getValue());
		}
		if (raw instanceof Attribute.Class) {
			return ((Attribute.Class) raw).classType.toString();
		}
		if (raw instanceof Attribute.Enum) {
			return new FrankEnumConstantDoclet(((Attribute.Enum) raw).getValue(), null);
		}

		//return raw.getValue();
//		if ((raw instanceof Integer) || (raw instanceof String) || (raw instanceof Boolean)) {
//			return raw;
//		} else
//		if (raw instanceof TypeElement) {
//			return ((TypeElement) raw).getQualifiedName();
//		} else if(raw instanceof DeclaredType) {
//			return ((DeclaredType) raw).asElement().toString(); //TODO: check if this is correct
//		} else if (raw instanceof VariableElement) {
//			return new FrankEnumConstantDoclet((VariableElement) raw, null);
//		} else {
//			return parseAnnotationValueAsStringArray(raw);
//		}
		return raw.getValue();
	}

	private Object parseAnnotationValueAsStringArray(com.sun.tools.javac.util.List<Attribute> valueList) {
		String[] result = new String[valueList.size()];
		for (int i = 0; i < valueList.size(); ++i) {
			result[i] = valueList.get(i).getValue().toString();
		}
		return result;
	}

	public static Object getAnnotationDefaultValue(AnnotationMirror annotationMirror, String elementName) {
		TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
		Element defaultValueMethod = annotationTypeElement.getEnclosedElements().stream()
			.filter(methodElement -> methodElement.getSimpleName().contentEquals(elementName))
			.findFirst()
			.orElse(null);

		if (defaultValueMethod instanceof Symbol.MethodSymbol) {
			Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) defaultValueMethod;
			return methodSymbol.getDefaultValue().getValue();
		}
		return null;
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return frankAnnotationsByName.get(name);
	}
}
