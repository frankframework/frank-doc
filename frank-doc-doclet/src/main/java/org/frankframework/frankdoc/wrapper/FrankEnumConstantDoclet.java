/*
Copyright 2021, 2022, 2024 WeAreFrank!

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

import com.sun.source.doctree.DocCommentTree;
import lombok.Getter;
import org.frankframework.frankdoc.model.EnumValue;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class FrankEnumConstantDoclet implements FrankEnumConstant {
	private final @Getter String name;
	private final boolean isPublic;
	private final @Getter String javaDoc;
	private final DocCommentTree docCommentTree;
	private final VariableElement variableElement;

	private final Map<String, FrankAnnotation> annotationsByName;

	public FrankEnumConstantDoclet(VariableElement variableElement, DocCommentTree docCommentTree) {
		name = variableElement.getSimpleName().toString();
		isPublic = variableElement.getModifiers().stream().anyMatch(m -> m == Modifier.PUBLIC);
		javaDoc = docCommentTree != null ? docCommentTree.toString() : null;
		this.docCommentTree = docCommentTree;
		this.variableElement = variableElement;
		AnnotationMirror[] javaDocAnnotations = variableElement.getAnnotationMirrors().toArray(new AnnotationMirror[0]);
		annotationsByName = FrankDocletUtils.getFrankAnnotationsByName(javaDocAnnotations);
	}

	@Override
	public List<String> getAllEnumValues() {
		var enumParent = (TypeElement) variableElement.getEnclosingElement();

		return enumParent.getEnclosedElements().stream().map(e -> {
			if (e instanceof VariableElement ve) {
				var x = new FrankEnumConstantDoclet(ve, null);

				var enumValue = new EnumValue(x);
				return enumValue.getLabel();
			}

			return null;
		}).filter(Objects::nonNull).toList();
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return annotationsByName.get(name);
	}

	@Override
	public String getJavaDocTag(String tagName) {
		return FrankDocletUtils.getJavaDocTag(docCommentTree, tagName);
	}
}
