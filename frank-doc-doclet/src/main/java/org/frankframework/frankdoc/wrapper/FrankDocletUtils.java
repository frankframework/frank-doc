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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor14;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;

import lombok.extern.log4j.Log4j2;

@Log4j2
final class FrankDocletUtils {
	private FrankDocletUtils() {
	}

	static Map<String, FrankAnnotation> getFrankAnnotationsByName(AnnotationMirror[] annotationMirrors) {
		Map<String, FrankAnnotation> annotationsByName = new LinkedHashMap<>();
		for (AnnotationMirror AnnotationMirror : annotationMirrors) {
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
			String foundTagName = docTree.toString().split("\\s+")[0];
			if (foundTagName.equals(tagName)) {
				return docTree.toString().substring(foundTagName.length()).trim();
			}
		}
		return null;
	}

	/**
	 * Return the string representation of a list of {@link DocTree}.
	 *
	 * @param docTreeList a list of {@link DocTree}
	 * @return string representation of the docTreeList
	 */
	public static @Nullable String convertDocTreeListToStr(List<? extends DocTree> docTreeList) {
		String docTreeStrList = docTreesToText(docTreeList);
		if (docTreeStrList.isEmpty())
			return null;
		return docTreeStrList;
	}

	/**
	 * Convert a list of DocTrees into a text string.
	 * Text nodes, entity nodes and element nodes are handled in detail, because whitespaces are missing in the toString representation.
	 * Other nodes are converted by toString.
	 *
	 * @param docTreeList list of trees
	 * @return text content
	 */
	private static String docTreesToText(@NonNull List<? extends DocTree> docTreeList) {
		StringBuilder builder = new StringBuilder();
		for (DocTree docTree : docTreeList) {
			if (docTree instanceof TextTree text) {
				builder.append(text.getBody());
			} else if (docTree instanceof EntityTree entity) {
				builder.append('&')
					.append(entity.getName())
					.append(';');
			} else if (docTree instanceof StartElementTree startEl) {
				builder.append("<").append(startEl.getName());
				for (DocTree tree : startEl.getAttributes()) {
					if (tree instanceof AttributeTree att) {
						char quote = (att.getValueKind() == AttributeTree.ValueKind.SINGLE) ? '\'' : '"';
						builder.append(' ')
							.append(att.getName())
							.append('=')
							.append(quote)
							.append(docTreesToText(att.getValue()))
							.append(quote);
					}
				}
				if (startEl.isSelfClosing()) {
					builder.append('/');
				}
				builder.append('>');
			} else if (docTree instanceof EndElementTree endEl) {
				builder.append("</").append(endEl.getName()).append(">");
			} else {
				builder.append(docTree);
			}
		}
		return builder.toString();
	}

	/**
	 * Get the canonical name of the inputClassType, which does not include any reference to its formal type parameter
	 * when it comes to generic type. For example, the canonical name of the interface java.util.Set<E> is java.util.Set.
	 *
	 * @param inputClassType class/method/variable type str
	 * @return canonical name of the inputClassType
	 */
	public static String getCanonicalClassName(String inputClassType) {
		if (inputClassType == null) {
			return null;
		}
		Pattern pattern = Pattern.compile("<.*>");
		Matcher matcher = pattern.matcher(inputClassType);
		StringBuilder sb = new StringBuilder();
		int start = 0;
		while (matcher.find()) {
			sb.append(inputClassType, start, matcher.start());
			start = matcher.end();
		}
		sb.append(inputClassType.substring(start));
		return sb.toString();
	}

	public static String getFullNameWithoutTypeInfo(TypeMirror typeMirror) {
		String fullNameWithoutTypeInfo = typeMirror.accept(new NameWithoutTypeInfoBuilder(), null);
		log.debug("<*> getFullNameWithoutTypeInfo: {}", fullNameWithoutTypeInfo);
		if (fullNameWithoutTypeInfo.contains("<") || fullNameWithoutTypeInfo.contains("[")) {
			log.error("Still found type with Generic or Array information in type at: {}; {}. Please fix this inside FrankDocletutils", fullNameWithoutTypeInfo, typeMirror);
		}
		return fullNameWithoutTypeInfo;
	}

	private static String buildQualifiedElementName(Element el) {
		if (el.getKind() == ElementKind.PACKAGE) {
			return el.toString();
		}
		Element enclosingElement = el.getEnclosingElement();
		if (enclosingElement == null || enclosingElement.getKind() == ElementKind.MODULE) {
			return el.toString();
		}
		return buildQualifiedElementName(enclosingElement) + "." + el.getSimpleName();
	}

	@SuppressWarnings("java:S110")
	private static class NameWithoutTypeInfoBuilder extends SimpleTypeVisitor14<String, Void> {
		@Override
		protected String defaultAction(TypeMirror t, Void unused) {
			String fullName = t.toString();
			int typeParamPos = fullName.indexOf('<');
			String fullNameWithoutTypeInfo = typeParamPos == -1 ? fullName : fullName.substring(0, typeParamPos);
			int arrayBracketPos = fullNameWithoutTypeInfo.indexOf('[');
			return arrayBracketPos == -1 ? fullNameWithoutTypeInfo : fullNameWithoutTypeInfo.substring(0, arrayBracketPos);
		}

		@Override
		public String visitArray(ArrayType at, Void unused) {
			return getFullNameWithoutTypeInfo(at.getComponentType());
		}

		@Override
		public String visitDeclared(DeclaredType dt, Void unused) {
			return buildQualifiedElementName(dt.asElement());
		}
	}
}
