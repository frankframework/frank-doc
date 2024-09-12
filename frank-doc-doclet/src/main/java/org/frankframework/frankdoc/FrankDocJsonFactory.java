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

package org.frankframework.frankdoc;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.model.AttributeEnum;
import org.frankframework.frankdoc.model.AttributeType;
import org.frankframework.frankdoc.model.ConfigChild;
import org.frankframework.frankdoc.model.DeprecationInfo;
import org.frankframework.frankdoc.model.ElementChild;
import org.frankframework.frankdoc.model.ElementType;
import org.frankframework.frankdoc.model.EnumValue;
import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.model.FrankDocGroup;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.model.FrankElement;
import org.frankframework.frankdoc.model.FrankLabel;
import org.frankframework.frankdoc.model.MandatoryStatus;
import org.frankframework.frankdoc.model.ObjectConfigChild;
import org.frankframework.frankdoc.model.ParsedJavaDocTag;
import org.frankframework.frankdoc.util.LogUtil;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FrankDocJsonFactory {
	private static Logger log = LogUtil.getLogger(FrankDocJsonFactory.class);

	private static final String DESCRIPTION = "description";

	private FrankDocModel model;
	private JsonBuilderFactory bf;
	List<FrankElement> elementsOutsideChildren;
	private final String frankFrameworkVersion;

	public FrankDocJsonFactory(FrankDocModel model, String frankFrameworkVersion) {
		this.model = model;
		elementsOutsideChildren = new ArrayList<>(model.getElementsOutsideConfigChildren());
		bf = Json.createBuilderFactory(null);
		this.frankFrameworkVersion = frankFrameworkVersion;
	}

	public JsonObject getJson() {
		try {
			JsonObjectBuilder result = bf.createObjectBuilder();
			// If the Frank!Framework version is null, the error is logged elsewhere.
			if(frankFrameworkVersion != null) {
				result.add("metadata", getMetadata());
			}
			result.add("groups", getGroups());
			result.add("types", getTypes());
			result.add("elements", getElements());
			result.add("enums", getEnums());
			getLabels().ifPresent(l -> result.add("labels", l));
			return result.build();
		} catch(JsonException e) {
			log.error("Error producing JSON", e);
			return null;
		}
	}

	private JsonObject getMetadata() {
		JsonObjectBuilder metadata = bf.createObjectBuilder();
		metadata.add("version", frankFrameworkVersion);
		return metadata.build();
	}

	private JsonArray getGroups() throws JsonException {
		JsonArrayBuilder result = bf.createArrayBuilder();
		for(FrankDocGroup group: model.getGroups()) {
			result.add(getGroup(group));
		}
		return result.build();
	}

	private JsonObject getGroup(FrankDocGroup group) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", group.getName());
		final JsonArrayBuilder types = bf.createArrayBuilder();
		group.getElementTypes().stream()
				.map(ElementType::getFullName)
				.forEach(types::add);
		if(group.getName().equals(FrankDocGroup.GROUP_NAME_OTHER)) {
			elementsOutsideChildren.forEach(f -> types.add(f.getFullName()));
			types.add(Constants.MODULE_ELEMENT_NAME);
		}
		result.add("types", types);
		return result.build();
	}

	private JsonArray getTypes() {
		JsonArrayBuilder result = bf.createArrayBuilder();
		List<ElementType> sortedTypes = new ArrayList<>(model.getAllTypes().values());
		Collections.sort(sortedTypes);
		for(ElementType elementType: sortedTypes) {
			result.add(getType(elementType));
		}
		elementsOutsideChildren.forEach(f -> result.add(getNonChildType(f)));
		result.add(getTypeReferencedEntityRoot());
		return result.build();
	}

	private JsonObject getTypeReferencedEntityRoot() {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", Constants.MODULE_ELEMENT_NAME);
		JsonArrayBuilder members = bf.createArrayBuilder();
		members.add(Constants.MODULE_ELEMENT_NAME);
		result.add("members", members);
		return result.build();
	}

	private JsonObject getType(ElementType elementType) {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", elementType.getFullName());
		final JsonArrayBuilder members = bf.createArrayBuilder();
		elementType.getSyntax2Members().forEach(f -> members.add(f.getFullName()));
		result.add("members", members);
		return result.build();
	}

	private JsonObject getNonChildType(FrankElement frankElement) {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", frankElement.getFullName());
		final JsonArrayBuilder members = bf.createArrayBuilder();
		members.add(frankElement.getFullName());
		result.add("members", members);
		return result.build();
	}

	private JsonArray getElements() throws JsonException {
		Map<String, List<FrankElement>> elementsByName = model.getAllElements().values().stream()
				.collect(Collectors.groupingBy(this::getElementNameForJson));
		List<String> sortKeys = new ArrayList<>(elementsByName.keySet());
		sortKeys.add(Constants.MODULE_ELEMENT_NAME);
		Collections.sort(sortKeys);
		JsonArrayBuilder result = bf.createArrayBuilder();
		for(String sortKey: sortKeys) {
			if(sortKey.equals(Constants.MODULE_ELEMENT_NAME)) {
				result.add(getElementReferencedEntityRoot());
			} else {
				elementsByName.get(sortKey).stream()
						.map(this::getElement)
						.forEach(result::add);
			}
		}
		return result.build();
	}

	private String getElementNameForJson(FrankElement f) {
		boolean useXmlElementName = (! f.isInterfaceBased()) && f.hasOnePossibleXmlElementName();
		if(useXmlElementName) {
			return f.getTheSingleXmlElementName();
		} else {
			return f.getSimpleName();
		}
	}

	private JsonObject getElementReferencedEntityRoot() {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", Constants.MODULE_ELEMENT_NAME);
		result.add("fullName", Constants.MODULE_ELEMENT_NAME);
		addDescription(result, Constants.MODULE_ELEMENT_DESCRIPTION);
		JsonArrayBuilder xmlElementNames = bf.createArrayBuilder();
		xmlElementNames.add(Constants.MODULE_ELEMENT_NAME);
		result.add("elementNames", xmlElementNames.build());
		return result.build();
	}

	private JsonObject getDeprecated(@NonNull DeprecationInfo deprecationInfo) {
		JsonObjectBuilder builder = bf.createObjectBuilder();
		builder.add("forRemoval", deprecationInfo.forRemoval());
		if (deprecationInfo.since() != null) {
			builder.add("since", deprecationInfo.since());
		}
		if (deprecationInfo.description() != null) {
			builder.add("description", deprecationInfo.description());
		}
		return builder.build();
	}

	private JsonObject getElement(FrankElement frankElement) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", getElementNameForJson(frankElement));
		result.add("fullName", frankElement.getFullName());
		if (frankElement.isAbstract()) {
			result.add("abstract", true);
		}
		DeprecationInfo deprecationInfo = frankElement.getDeprecationInfo();
		if (deprecationInfo != null) {
			JsonObject deprecationInfoJsonObject = getDeprecated(deprecationInfo);
			result.add("deprecated", deprecationInfoJsonObject);
		}

		addDescription(result, frankElement.getDescription());
		addIfNotNull(result, "parent", getParentOrNull(frankElement));
		JsonArrayBuilder xmlElementNames = bf.createArrayBuilder();
		frankElement.getXmlElementNames().forEach(xmlElementNames::add);
		result.add("elementNames", xmlElementNames);
		JsonArray attributes = getAttributes(frankElement, getParentOrNull(frankElement) == null);
		if (!attributes.isEmpty()) {
			result.add("attributes", attributes);
		}
		List<FrankAttribute> nonInheritedAttributes = frankElement.getChildrenOfKind(ElementChild.JSON_NOT_INHERITED, FrankAttribute.class);
		if (!nonInheritedAttributes.isEmpty()) {
			final var builder = bf.createArrayBuilder();
			nonInheritedAttributes.forEach(nia -> builder.add(nia.getName()));
			result.add("nonInheritedAttributes", builder.build());
		}
		JsonArray configChildren = getConfigChildren(frankElement);
		if (!configChildren.isEmpty()) {
			result.add("children", configChildren);
		}
		if (frankElement.getMeaningOfParameters() != null) {
			result.add("parametersDescription", frankElement.getMeaningOfParameters());
		}
		if (!frankElement.getSpecificParameters().isEmpty()) {
			final var builder = bf.createArrayBuilder();
			frankElement.getSpecificParameters().forEach(sp -> builder.add(getParsedJavaDocTag(sp)));
			result.add("parameters", builder.build());
		}
		if (!frankElement.getForwards().isEmpty()) {
			final var builder = bf.createArrayBuilder();
			frankElement.getForwards().forEach(fw -> builder.add(getParsedJavaDocTag(fw)));
			result.add("forwards", builder.build());
		}
		if (!frankElement.getTags().isEmpty()) {
			final var builder = bf.createObjectBuilder();
			for (ParsedJavaDocTag tag: frankElement.getTags()) {
				builder.add(tag.getName(), tag.getDescription());
			}
			result.add("tags", builder.build());
		}
		if (!frankElement.getLabels().isEmpty()) {
			final var builder = bf.createArrayBuilder();
			for (FrankLabel lab: frankElement.getLabels()) {
				JsonObjectBuilder ob = bf.createObjectBuilder();
				ob.add("label", lab.getName());
				ob.add("value", lab.getValue());
				builder.add(ob.build());
			}
			result.add("labels", builder.build());
		}
		if (!frankElement.getQuickLinks().isEmpty()) {
			final var builder = bf.createArrayBuilder();
			frankElement.getQuickLinks().forEach(quickLink -> {
				final var quickLinkBuilder = bf.createObjectBuilder();

				quickLinkBuilder.add("label", quickLink.label());
				quickLinkBuilder.add("url", quickLink.url());

				builder.add(quickLinkBuilder);
			});
			result.add("quickLinks", builder.build());
		}
		if (!frankElement.getNotes().isEmpty()) {
			final var builder = bf.createArrayBuilder();
			frankElement.getNotes().forEach(note -> {
				final var noteBuilder = bf.createObjectBuilder();

				noteBuilder.add("type", note.type().name());
				noteBuilder.add("value", note.value());

				builder.add(noteBuilder);
			});
			result.add("notes", builder.build());
		}
		return result.build();
	}

	private JsonObject getParsedJavaDocTag(ParsedJavaDocTag parsedJavaDocTag) {
		JsonObjectBuilder b = bf.createObjectBuilder();
		b.add("name", parsedJavaDocTag.getName());
		if(parsedJavaDocTag.getDescription() != null) {
			b.add("description", parsedJavaDocTag.getDescription());
		}
		return b.build();
	}

	private static String getParentOrNull(FrankElement frankElement) {
		if(frankElement != null) {
			FrankElement parent = frankElement.getNextAncestorThatHasChildren(
					elem -> elem.getAttributes(ElementChild.JSON_RELEVANT).isEmpty() && elem.getConfigChildren(ElementChild.JSON_RELEVANT).isEmpty());
			if(parent != null) {
				return parent.getFullName();
			}
		}
		return null;
	}

	private JsonArray getAttributes(FrankElement frankElement, boolean addAttributeActive) throws JsonException {
		JsonArrayBuilder result = bf.createArrayBuilder();
		for(FrankAttribute attribute: frankElement.getAttributes(ElementChild.IN_COMPATIBILITY_XSD)) {
			result.add(getAttribute(attribute));
		}
		if(addAttributeActive) {
			result.add(getAttributeActive());
		}
		return result.build();
	}

	private JsonObject getAttribute(FrankAttribute frankAttribute) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", frankAttribute.getName());
		DeprecationInfo deprecationInfo = frankAttribute.getDeprecationInfo();
		if (deprecationInfo != null) {
			JsonObject deprecationInfoJsonObject = getDeprecated(deprecationInfo);
			result.add("deprecated", deprecationInfoJsonObject);
		}
		if (frankAttribute.getMandatoryStatus() != MandatoryStatus.OPTIONAL) {
			result.add("mandatory", true);
		}
		if (frankAttribute.isReintroduced()) {
			result.add("reintroduced", true);
		}
		if (frankAttribute.isUnsafe()) {
			result.add("unsafe", true);
		}
		addIfNotNull(result, "description", frankAttribute.getDescription());
		addIfNotNull(result, "default", frankAttribute.getDefaultValue());
		if (!frankAttribute.getAttributeType().equals(AttributeType.STRING)) {
			result.add("type", frankAttribute.getAttributeType().name().toLowerCase());
		}
		if (frankAttribute.getAttributeEnum() != null) {
			result.add("enum", frankAttribute.getAttributeEnum().getFullName());
		}
		return result.build();
	}

	private JsonObject getAttributeActive() {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", "active");
		result.add("description", "If defined and empty or false, then this element and all its children are ignored");
		return result.build();
	}

	private void addIfNotNull(JsonObjectBuilder builder, String field, String value) {
		if(value != null) {
			builder.add(field, value);
		}
	}

	private void addDescription(JsonObjectBuilder builder, String value) {
		if(! StringUtils.isBlank(value)) {
			builder.add(DESCRIPTION, value.replaceAll("\"", "\\\\\\\""));
		}
	}

	private JsonArray getConfigChildren(FrankElement frankElement) throws JsonException {
		JsonArrayBuilder result = bf.createArrayBuilder();
		if(frankElement.getFullName().equals(model.getRootClassName())) {
			result.add(getConfigChildReferencedEntityRoot());
		}
		for(ConfigChild child: frankElement.getConfigChildren(ElementChild.IN_COMPATIBILITY_XSD)) {
			result.add(getConfigChild(child));
		}
		return result.build();
	}

	private JsonObject getConfigChildReferencedEntityRoot() {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("multiple", true);
		result.add("roleName", Constants.MODULE_ELEMENT_NAME.toLowerCase());
		result.add("description", Constants.MODULE_ELEMENT_DESCRIPTION);
		result.add("type", Constants.MODULE_ELEMENT_NAME);
		return result.build();
	}

	private JsonObject getConfigChild(ConfigChild child) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		if(child.isDeprecated()) {
			result.add("deprecated", child.isDeprecated());
		}
		if(child.getMandatoryStatus() != MandatoryStatus.OPTIONAL) {
			result.add("mandatory", true);
		}
		if(child.isReintroduced()) {
			result.add("reintroduced", true);
		}
		result.add("multiple", child.isAllowMultiple());
		result.add("roleName", child.getRoleName());
		addIfNotNull(result, "description", child.getDescription());
		if(child instanceof ObjectConfigChild) {
			result.add("type", ((ObjectConfigChild) child).getElementType().getFullName());
		}
		return result.build();
	}

	private JsonArray getEnums() {
		final JsonArrayBuilder result = bf.createArrayBuilder();
		for(AttributeEnum attributeEnum: model.getAllAttributeEnumInstances()) {
			result.add(getAttributeEnum(attributeEnum));
		}
		return result.build();
	}

	private JsonObject getAttributeEnum(AttributeEnum en) {
		final JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", en.getFullName());
		result.add("values", getAttributeEnumValues(en));
		return result.build();
	}

	private JsonArray getAttributeEnumValues(AttributeEnum en) {
		JsonArrayBuilder result = bf.createArrayBuilder();
		for(EnumValue v: en.getValues()) {
			JsonObjectBuilder valueBuilder = bf.createObjectBuilder();
			valueBuilder.add("label", v.getLabel());
			if(v.getDescription() != null) {
				valueBuilder.add("description", v.getDescription());
			}
			if(v.isDeprecated()) {
				valueBuilder.add("deprecated", true);
			}
			result.add(valueBuilder.build());
		}
		return result.build();
	}

	// We omit the "labels" element if there are no labels. This
	// makes no different in production because the F!F sources
	// do have labels. This way, we can omit labels from unit tests.
	private Optional<JsonArray> getLabels() {
		if(model.getAllLabels().isEmpty()) {
			return Optional.empty();
		}
		final JsonArrayBuilder result = bf.createArrayBuilder();
		for(String label: model.getAllLabels()) {
			JsonObjectBuilder labelObject = bf.createObjectBuilder();
			JsonArrayBuilder labelValuesObject = bf.createArrayBuilder();
			for(String value: model.getAllValuesOfLabel(label)) {
				labelValuesObject.add(value);
			}
			labelObject.add("label", label);
			labelObject.add("values", labelValuesObject.build());
			result.add(labelObject.build());
		}
		return Optional.of(result.build());
	}
}
