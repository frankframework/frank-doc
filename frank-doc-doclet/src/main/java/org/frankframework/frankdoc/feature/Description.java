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

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankDocletConstants;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Description {
	private static Logger log = LogUtil.getLogger(Description.class);
	private static final Description INSTANCE = new Description();

	public static Description getInstance() {
		return INSTANCE;
	}

	private Description() {
	}

	public String valueOf(FrankMethod method) {
		String result = null;
		FrankAnnotation annotation = method.getAnnotation(FrankDocletConstants.IBISDOC);
		if (annotation != null) {
			try {
				ParsedIbisDocAnnotation ibisDoc = new ParsedIbisDocAnnotation(annotation);
				result = ibisDoc.getDescription();
			} catch(FrankDocException e) {
				log.error("Could not parse annotation [{}] on method [{}]", FrankDocletConstants.IBISDOC, method.toString());
			}
		}
		if (result == null) {
			result = method.getJavaDoc();

			// Recursively searches for a method with the same signature to use as this method's javadoc.
			if (result != null && result.contains("{@inheritDoc}")) {
				FrankClass clazz = method.getDeclaringClass();

				FrankClass superClazz = clazz.getSuperclass();
				if (superClazz != null) {
					for (FrankMethod superClassMethod : superClazz.getDeclaredMethods()) {
						if (superClassMethod.getSignature().equals(method.getSignature())) {
							result = valueOf(superClassMethod);
							break;
						}
					}
				}
			}
		}
		try {
			return Utils.substituteJavadocTags(result, method.getDeclaringClass());
		} catch(FrankDocException e) {
			log.error("Could not replace {@value ...} in [{}]", result);
			return result;
		}
	}

	public String valueOf(FrankClass clazz) {
		String result = clazz.getJavaDoc();
		try {
			return Utils.substituteJavadocTags(result, clazz);
		} catch(FrankDocException e) {
			log.error("Could not replace {@value ...} in [{}]", result);
			return result;
		}
	}
}
