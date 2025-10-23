/*
Copyright 2021 - 2025 WeAreFrank!

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

package org.frankframework.frankdoc.model;

import org.apache.commons.lang3.StringUtils;
import org.frankframework.frankdoc.feature.Deprecated;
import org.frankframework.frankdoc.feature.Description;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;

import lombok.Getter;

public class EnumValue {
	private static final String ENUM_LABEL = "org.frankframework.doc.EnumLabel";

	private @Getter boolean explicitLabel = false;
	private final @Getter String javaTag;
	private @Getter String label;
	private final @Getter String description;
	private @Getter boolean deprecated = false;

	public EnumValue(FrankEnumConstant c) {
		this.javaTag = c.getName();
		this.label = this.javaTag;
		FrankAnnotation annotation = c.getAnnotation(ENUM_LABEL);
		String annotationValue = null;
		if (annotation != null) {
			annotationValue = (String)annotation.getValue();
		}
		if (!StringUtils.isBlank(annotationValue)) {
			this.explicitLabel = true;
			this.label = annotationValue;
		}
		this.description = Description.getInstance().valueOf(c);
		if (Deprecated.getInstance().isSetOn(c)) {
			this.deprecated = true;
		}
	}
}
