/*
Copyright 2025 WeAreFrank!

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
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

public enum AdditionalRootElement {
	PIPELINE_PART("PipelinePart", "IPipe", "Wrapper element to help create reusable parts of a pipeline");

	public static final Map<String, AdditionalRootElement> VALUE_BY_TYPE = Arrays.stream(values())
		.collect(Collectors.toMap(AdditionalRootElement::getTypeName, r -> r));

	private final @Getter String elementName;
	private final @Getter String typeName;
	private final @Getter String docString;

	AdditionalRootElement(String elementName, String typeName, String docString) {
		this.elementName = elementName;
		this.typeName = typeName;
		this.docString = docString;
	}
}
