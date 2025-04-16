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

package org.frankframework.frankdoc.feature;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.*;

public class Description {
	private static Logger log = LogUtil.getLogger(Description.class);
	private static final Description INSTANCE = new Description();

	public static final String INHERIT_DOC_TAG = "{@inheritDoc}";
	public static final String INHERIT_CLASS_DOC_TAG = "{@inheritClassDoc }"; // The space is intentional, due to the way tags are parsed.

	public static Description getInstance() {
		return INSTANCE;
	}

	private Description() {
	}

	public String valueOf(FrankMethod method) {
		String result =  method.getJavaDoc();

		// Recursively searches for a method with the same signature to use as this method's javadoc.
		if (result != null && result.contains(INHERIT_DOC_TAG)) {
			FrankClass clazz = method.getDeclaringClass();
			String parentJavadoc = null;

			FrankClass superClazz = clazz.getSuperclass();
			if (superClazz != null) {
				for (FrankMethod superClassMethod : superClazz.getDeclaredMethods()) {
					if (superClassMethod.getSignature().equals(method.getSignature())) {
						parentJavadoc = valueOf(superClassMethod);
						break;
					}
				}
			}

			result = result.replace(INHERIT_DOC_TAG, parentJavadoc == null ? "" : parentJavadoc).strip();
		}
		return Utils.substituteJavadocTags(result, method.getDeclaringClass());
	}

	private String replaceInheritDocInResult(FrankClass superClazz, String childJavaDoc) {
		if (superClazz == null) {
			return childJavaDoc;
		}

		String parentJavaDoc = valueOf(superClazz);
		return childJavaDoc.replace(INHERIT_CLASS_DOC_TAG, parentJavaDoc == null ? "" : parentJavaDoc).strip();
	}

	public String valueOf(FrankClass clazz) {
		String result = clazz.getJavaDoc();

		if (result != null && result.contains(INHERIT_CLASS_DOC_TAG)) {
			FrankClass superClazz = clazz.getSuperclass();
			result = replaceInheritDocInResult(superClazz, result);
		}

		return Utils.substituteJavadocTags(result, clazz);
	}

	public String valueOf(FrankEnumConstant enumConstant) {
		String result = enumConstant.getJavaDoc();
		if (!StringUtils.isBlank(result)) {
			return Utils.substituteJavadocTags(result, null);
		}

		return null;
	}

}
