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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor14;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

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
		AnnotationMirror[] javaDocAnnotationsOfAnnotation = annotation.getAnnotationType().asElement().getAnnotationMirrors().stream()
			.filter(a -> !RECURSIVE_ANNOTATIONS.contains(a.getAnnotationType().toString()))
			.toArray(AnnotationMirror[]::new);

		if (javaDocAnnotationsOfAnnotation.length > 0) {
			log.trace("Creating annotations of annotations");
			frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(javaDocAnnotationsOfAnnotation);
		} else {
			frankAnnotationsByName = new LinkedHashMap<>();
		}
	}

	@Override
	public String getName() {
		return annotation.getAnnotationType().toString();
	}

	@Override
	public boolean isPublic() {
		// This is not correct. It should be the visibility of the annotation type.
		// Important note: In Java 8, this is never false inside unit tests.
		return true;
	}

	@Override
	public Object getValue() {
		return getValueOf("value");
	}

	@Override
	public Object getValueOf(String fieldName) {
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

	private static Object parseAnnotationValue(AnnotationValue raw) {
		return raw.accept(new AnnotationValueParser(), null);
	}

	public static Object getAnnotationDefaultValue(AnnotationMirror annotationMirror, String elementName) {
		TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
		Element defaultValueMethod = annotationTypeElement.getEnclosedElements().stream()
			.filter(methodElement -> methodElement.getSimpleName().contentEquals(elementName))
			.findFirst()
			.orElse(null);

		if (defaultValueMethod instanceof ExecutableElement executableElement) {
			AnnotationValue defaultValue = executableElement.getDefaultValue();
			if (defaultValue != null) {
				return defaultValue.getValue();
			}
		}
		return null;
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return frankAnnotationsByName.get(name);
	}

	@Override
	public int getAnnotationCount() {
		return frankAnnotationsByName.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FrankAnnotationDoclet other)) return false;

		return getName().equals(other.getName())
			&& Objects.equals(getValue(), other.getValue())
			&& getAnnotationCount() == other.getAnnotationCount();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getValue(), getAnnotationCount());
	}

	@Override
	public String toString() {
		final String value = getValue() == null ? "null" : getValue().toString();
		return "FrankAnnotationDoclet name: [" + getName() + "], value: [" + value + "] annotations size: " + getAnnotationCount();
	}

	@SuppressWarnings("java:S110") // Sonar says: Too many parent classes. Sorry, can't help it.
	private static class AnnotationValueParser extends SimpleAnnotationValueVisitor14<Object,Object> implements AnnotationValueVisitor<Object, Object> {
		@Override
		protected Object defaultAction(Object o, Object o2) {
			return o;
		}

		@Override
		public Object visitType(TypeMirror t, Object o) {
			return t.toString();
		}

		@Override
		public Object visitEnumConstant(VariableElement c, Object o) {
			return new FrankEnumConstantDoclet(c, null);
		}

		@Override
		public Object visitAnnotation(AnnotationMirror a, Object o) {
			return new FrankAnnotationDoclet(a);
		}

		@Override
		public Object visitArray(List<? extends AnnotationValue> vals, Object o) {
			if (!vals.isEmpty() && vals.get(0).getValue() instanceof AnnotationMirror) {
				return vals.stream()
					.map(AnnotationValue::getValue)
					.map(AnnotationMirror.class::cast)
					.map(FrankAnnotationDoclet::new)
					.toArray(FrankAnnotationDoclet[]::new);
			}
			return vals.stream()
				.map(AnnotationValue::getValue)
				.map(Object::toString)
				.toArray(String[]::new);
		}

		@Override
		public Object visitUnknown(AnnotationValue av, Object o) {
			return av.toString();
		}
	}
}
