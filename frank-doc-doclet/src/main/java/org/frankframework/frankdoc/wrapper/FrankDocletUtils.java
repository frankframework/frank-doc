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

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashMap;
import java.util.Map;

final class FrankDocletUtils {
	private FrankDocletUtils() {
	}

	static Map<String, FrankAnnotation> getFrankAnnotationsByName(AnnotationMirror[] AnnotationMirrors) {
		Map<String, FrankAnnotation> annotationsByName = new LinkedHashMap<>();
		for(AnnotationMirror AnnotationMirror: AnnotationMirrors) {
			FrankAnnotation frankAnnotation = new FrankAnnotationDoclet(AnnotationMirror);
			annotationsByName.put(frankAnnotation.getName(), frankAnnotation);
		}
		return annotationsByName;
	}

	static TypeElement getSuperclassElement(TypeElement typeElement) {
		TypeMirror superclass = typeElement.getSuperclass();
		if (superclass.getKind().equals(TypeKind.NONE)) {
			return null;
		}
		if (superclass.toString().equals("java.lang.Record")) {
			return null;
		}
		return (TypeElement) ((DeclaredType) superclass).asElement();
	}

	/**
	 * Returns the value of the first tag with the given name. If no tag with the given name is found, null is returned.
	 * @param docCommentTree
	 * @param tagName
	 * @return
	 */
	static String getJavaDocTag(DocCommentTree docCommentTree, String tagName) {
		if (docCommentTree == null) {
			return null;
		}
		for (DocTree docTree : docCommentTree.getBlockTags()) {
			String foundTagName = docTree.toString().split(" ")[0];
			if (foundTagName.equals(tagName)) {
				return docTree.toString().substring(foundTagName.length()).trim();
			}
		}
		return null;
	}
}
