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
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * This enum class allows for an extensible way to add multiple additional root elements in the XML that can be included in
 * a configuration.
 */
public enum AdditionalRootElement {
	PIPELINE_PART("PipelinePart", "IPipe", true, "Wrapper element to help create reusable parts of a pipeline that can be included into other pipelines"),
	COMPONENT_PIPE("Pipeline", "ComponentPipe", false, "Wrapper element to help create reusable parts of a pipeline that can be built as independent pipelines", "Pipeline");

	/**
	 * Lookup-map to find the Enum value by the (simple) classname of a type being processed.
	 */
	public static final Map<String, AdditionalRootElement> VALUE_BY_TYPE = Arrays.stream(values())
		.collect(Collectors.toMap(AdditionalRootElement::getTypeName, r -> r));

	/**
	 * Name of the element that should be added as root-level element in the XSD or JSON.
	 */
	private final @Getter String elementName;
	/**
	 * Name of the type that, if present, should trigger the generation of the corresponding root element.
	 * The XSD typename is dynamically generated for the type and not part of the enum definition.
	 */
	private final @Getter String typeName;
	/**
	 * If true, and the typeName is an interface from which a group-type will be generated consisting of all implementations
	 * of the interface, the "Include" element will be added to that group.
	 */
	private final @Getter boolean addIncludeToTypeGroup;
	/**
	 * Doc string that should be added to the XSD or JSON for this top-level element.
	 */
	private final @Getter String docString;

	/**
	 * Set of types allowed in the additional root element
	 */
	private final @Getter Set<String> allowedTypes;
	AdditionalRootElement(String elementName, String typeName, boolean addIncludeToTypeGroup, String docString, String... allowedTypes) {
		this.elementName = elementName;
		this.typeName = typeName;
		this.addIncludeToTypeGroup = addIncludeToTypeGroup;
		this.docString = docString;
		this.allowedTypes = Set.of(allowedTypes);
	}
}
