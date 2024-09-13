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
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Default {
	private static final String ANNOTATION_DEFAULT = "org.frankframework.doc.Default";
	private static final String TAG_DEFAULT = "@ff.default";

	private static Logger log = LogUtil.getLogger(Default.class);
	private static final Default INSTANCE = new Default();

	public static Default getInstance() {
		return INSTANCE;
	}

	private Default() {
	}

	public String valueOf(FrankMethod method) {
		String result = fromDefaultAnnotation(method);
		if(result == null) {
			result = method.getJavaDocTag(TAG_DEFAULT);
		}
		try {
			return Utils.substituteJavadocTags(result, method.getDeclaringClass());
		} catch(FrankDocException e) {
			log.error("Could not replace {@value ...} in [{}]", result);
			return result;
		}
	}

	private String fromDefaultAnnotation(FrankMethod method) {
		FrankAnnotation annotation = method.getAnnotation(ANNOTATION_DEFAULT);
		if(annotation == null) {
			return null;
		}
		return annotation.getValue().toString();
	}

}
