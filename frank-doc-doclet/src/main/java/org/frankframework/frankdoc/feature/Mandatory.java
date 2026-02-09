/*
Copyright 2022, 2025 WeAreFrank!

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
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankMethod;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Mandatory {
	public enum Value {
		IGNORE_COMPATIBILITY,
		DONT_IGNORE_COMPATIBILITY;
	}

	private static final String TAG_NAME = "@ff.mandatory";
	private static final String ANNOTATION_NAME = "org.frankframework.doc.Mandatory";

	private static final String IGNORE_COMPATIBILITY_MODE = "ignoreInCompatibilityMode";

	private static final Mandatory INSTANCE = new Mandatory();

	public static Mandatory getInstance() {
		return INSTANCE;
	}

	private Mandatory() {
	}

	public Value valueOf(FrankMethod method) {
		String tagValue = method.getJavaDocTag(TAG_NAME);
		if(tagValue != null) {
			if(StringUtils.isBlank(tagValue)) {
				return Value.DONT_IGNORE_COMPATIBILITY;
			} else if(tagValue.equals(IGNORE_COMPATIBILITY_MODE)) {
				return Value.IGNORE_COMPATIBILITY;
			} else {
				log.error("Method [{}] has JavaDoc tag [{}] with invalid value [{}]", method, TAG_NAME, tagValue);
				return Value.DONT_IGNORE_COMPATIBILITY;
			}
		}
		FrankAnnotation annotation = method.getAnnotation(ANNOTATION_NAME);
		if(annotation != null) {
			boolean annotationValue;
			annotationValue = (Boolean) annotation.getValueOf(IGNORE_COMPATIBILITY_MODE);
			if(annotationValue) {
				return Value.IGNORE_COMPATIBILITY;
			} else {
				return Value.DONT_IGNORE_COMPATIBILITY;
			}
		}
		return null;
	}
}
