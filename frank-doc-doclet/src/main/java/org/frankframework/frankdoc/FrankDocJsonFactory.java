/*
Copyright 2021, 2022, 2025 WeAreFrank!

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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.frankframework.frankdoc.model.AttributeEnum;
import org.frankframework.frankdoc.model.AttributeType;
import org.frankframework.frankdoc.model.ConfigChild;
import org.frankframework.frankdoc.model.CredentialProvider;
import org.frankframework.frankdoc.model.DeprecationInfo;
import org.frankframework.frankdoc.model.ElementChild;
import org.frankframework.frankdoc.model.ElementType;
import org.frankframework.frankdoc.model.EnumValue;
import org.frankframework.frankdoc.model.Forward;
import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.model.FrankElement;
import org.frankframework.frankdoc.model.MandatoryStatus;
import org.frankframework.frankdoc.model.Note;
import org.frankframework.frankdoc.model.ObjectConfigChild;
import org.frankframework.frankdoc.model.ParsedJavaDocTag;
import org.frankframework.frankdoc.model.ServletAuthenticator;
import org.frankframework.frankdoc.model.ServletAuthenticatorMethod;
import org.frankframework.frankdoc.properties.Group;
import org.frankframework.frankdoc.properties.Property;
import org.frankframework.frankdoc.wrapper.AdditionalRootElement;

@Log4j2
public class FrankDocJsonFactory {

	private static final String DESCRIPTION = "description";
	public static final String DEPRECATED = "deprecated";

	private final FrankDocModel model;
	private final JsonBuilderFactory bf;
	final List<FrankElement> elementsOutsideChildren;
	private final String frankFrameworkVersion;

	private final List<AdditionalRootElement> additionalRootElements;

	public FrankDocJsonFactory(FrankDocModel model, String frankFrameworkVersion) {
		this.model = model;
		elementsOutsideChildren = model.getElementsOutsideConfigChildren();
		bf = Json.createBuilderFactory(null);
		this.frankFrameworkVersion = frankFrameworkVersion;
		this.additionalRootElements = findAdditionalRootElements();
	}

	private List<AdditionalRootElement> findAdditionalRootElements() {
		return model.getAllTypes()
			.values()
			.stream()
			.map(ElementType::getSimpleName)
			.filter(AdditionalRootElement.VALUE_BY_TYPE::containsKey)
			.map(AdditionalRootElement.VALUE_BY_TYPE::get)
			.sorted(Comparator.comparing(AdditionalRootElement::getElementName))
			.toList();
	}

	public @Nullable JsonObject getJson() {
		try {
			JsonObjectBuilder result = bf.createObjectBuilder();
			// If the Frank!Framework version is null, the error is logged elsewhere.
			if(frankFrameworkVersion != null) {
				result.add("metadata", getMetadata());
			}
			result.add("types", getTypes());
			result.add("elements", getElements());
			result.add("elementNames", getElementNames());
			result.add("enums", getEnums());
			getLabels().ifPresent(l -> result.add("labels", l));
			getProperties().ifPresent(p -> result.add("properties", p));
			getCredentialProviders().ifPresent(p -> result.add("credentialProviders", p));
			getServletAuthenticators().ifPresent(s -> result.add("servletAuthenticators", s));
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

	private JsonObject getTypes() {
		JsonObjectBuilder result = bf.createObjectBuilder();
		model
			.getAllTypes()
			.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByKey())
			.forEach(type -> addType(type.getValue(), result));
		elementsOutsideChildren.forEach(element -> addNonChildType(element, result));
		getTypeReferencedEntityRoot(result);
		additionalRootElements.forEach(element -> addAdditionalRootElementType(result, element));
		return result.build();
	}

	private void addAdditionalRootElementType(JsonObjectBuilder result, AdditionalRootElement additionalRootElement) {
		JsonArrayBuilder members = bf.createArrayBuilder();
		members.add(additionalRootElement.getElementName());
		result.add(additionalRootElement.getElementName(), members);
	}

	private void getTypeReferencedEntityRoot(JsonObjectBuilder result) {
		JsonArrayBuilder members = bf.createArrayBuilder();
		members.add(Constants.MODULE_ELEMENT_NAME);
		result.add(Constants.MODULE_ELEMENT_NAME, members);
	}

	private void addType(ElementType elementType, JsonObjectBuilder result) {
		final JsonArrayBuilder members = bf.createArrayBuilder();
		elementType.getSyntax2Members().forEach(f -> members.add(f.getFullName()));
		result.add(elementType.getFullName(), members);
	}

	private void addNonChildType(FrankElement frankElement, JsonObjectBuilder result) {
		final JsonArrayBuilder members = bf.createArrayBuilder();
		members.add(frankElement.getFullName());
		result.add(frankElement.getFullName(), members);
	}

	private JsonObject getElements() throws JsonException {
		final JsonObjectBuilder builder = bf.createObjectBuilder();

		Map<String, List<FrankElement>> elementsByName = getAllElements();
		builder.add(Constants.MODULE_ELEMENT_NAME, getRootElementObject());

		additionalRootElements.forEach(element -> builder.add(element.getElementName(), getObjectForElement(element)));

		for (List<FrankElement> elementsPerClass : elementsByName.values()) {
			for (FrankElement element : elementsPerClass) {
				builder.add(element.getFullName(), getElement(element));
			}
		}

		return builder.build();
	}

	private JsonObject getObjectForElement(AdditionalRootElement element) {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", element.getElementName());
		addDescription(result, element.getDocString());
		return result.build();
	}

	private Map<String, List<FrankElement>> getAllElements() {
		return model
			.getAllElements()
			.values()
			.stream()
			.collect(Collectors.groupingBy(this::getElementNameForJson));
	}

	private String getElementNameForJson(FrankElement f) {
		boolean useXmlElementName = (!f.isInterfaceBased()) && f.hasOnePossibleXmlElementName();
		if(useXmlElementName) {
			return f.getTheSingleXmlElementName();
		} else {
			return f.getSimpleName();
		}
	}

	private JsonObject getRootElementObject() {
		JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", Constants.MODULE_ELEMENT_NAME);
		addDescription(result, Constants.MODULE_ELEMENT_DESCRIPTION);
		return result.build();
	}

	private JsonObject getDeprecated(@NonNull DeprecationInfo deprecationInfo) {
		JsonObjectBuilder builder = bf.createObjectBuilder();
		builder.add("forRemoval", deprecationInfo.forRemoval());
		if (deprecationInfo.since() != null) {
			builder.add("since", deprecationInfo.since());
		}
		if (deprecationInfo.description() != null) {
			builder.add(DESCRIPTION, deprecationInfo.description());
		}
		return builder.build();
	}

	private JsonObject getElement(FrankElement frankElement) throws JsonException {
		final JsonObjectBuilder result = bf.createObjectBuilder();
		result.add("name", getElementNameForJson(frankElement));
		if (frankElement.isAbstract()) {
			result.add("abstract", true);
		}
		DeprecationInfo deprecationInfo = frankElement.getDeprecationInfo();
		if (deprecationInfo != null) {
			JsonObject deprecationInfoJsonObject = getDeprecated(deprecationInfo);
			result.add(DEPRECATED, deprecationInfoJsonObject);
		}

		addDescription(result, frankElement.getDescription());
		addIfNotNull(result, "parent", getParentOrNull(frankElement));

		JsonObject attributes = getAttributes(frankElement, getParentOrNull(frankElement) == null);
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
			final JsonObjectBuilder builder = bf.createObjectBuilder();
			frankElement.getSpecificParameters().forEach(parameter -> builder.add(parameter.getName(), getParsedJavaDocTag(parameter)));
			result.add("parameters", builder.build());
		}
		if(!frankElement.getForwards().isEmpty()) {
			final JsonObjectBuilder builder = bf.createObjectBuilder();
			frankElement.getForwards().forEach(forward -> builder.add(forward.name(), getJsonForForward(forward)));
			result.add("forwards", builder.build());
		}

		if (!frankElement.getQuickLinks().isEmpty()) {
			final var builder = bf.createArrayBuilder();
			frankElement.getQuickLinks().forEach(quickLink -> {
				final var quickLinkBuilder = bf.createObjectBuilder();

				quickLinkBuilder.add("label", quickLink.label());
				quickLinkBuilder.add("url", quickLink.url());

				builder.add(quickLinkBuilder);
			});
			result.add("links", builder.build());
		}

		addNotesToBuilder(frankElement.getNotes(), result);

		return result.build();
	}

	private JsonObject getElementName(String elementName,String fullName, Map<String, List<String>> labelGroups) {
		final var result = bf.createObjectBuilder();
		result.add("className", fullName);

		if (!labelGroups.isEmpty()) {
			final JsonObjectBuilder labelsBuilder = bf.createObjectBuilder();

			labelGroups.forEach((group, labels) -> {
				// TODO: This is a bandage fix for validators and pipes. Pipes and validators currently share the same
				// implementation, but we want the labels to be different. The elementName (automatically generated based
				// on the interfaces it implements) is compared against a string to manually exclude specific labels.
				if (elementName.toLowerCase().contains("pipe")) {
					labels = labels.stream().filter(label -> !label.toLowerCase().contains("validator")).toList();
				} else if (elementName.toLowerCase().contains("validator")) {
					labels = labels.stream().filter(label -> !label.toLowerCase().contains("pipe")).toList();
				}

				if (!labels.isEmpty()) {
					// Only get the first label since, most if not all categories have only one label
					labelsBuilder.add(group, labels.getFirst());
				}
			});

			result.add("labels", labelsBuilder.build());
		}

		return result.build();
	}

	private JsonObject getParsedJavaDocTag(ParsedJavaDocTag parsedJavaDocTag) {
		JsonObjectBuilder b = bf.createObjectBuilder();
		if(parsedJavaDocTag.getDescription() != null) {
			b.add(DESCRIPTION, parsedJavaDocTag.getDescription());
		}
		return b.build();
	}

	private JsonObject getJsonForForward(Forward forward) {
		final var builder = bf.createObjectBuilder();
		if(forward.description() != null) {
			builder.add(DESCRIPTION, forward.description());
		}
		return builder.build();
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

	private JsonObject getAttributes(FrankElement frankElement, boolean addAttributeActive) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		for (FrankAttribute attribute: frankElement.getAttributes(ElementChild.IN_COMPATIBILITY_XSD)) {
			result.add(attribute.getName(), getAttribute(attribute));
		}
		if (addAttributeActive) {
			result.add("active", getAttributeActive());
		}
		return result.build();
	}

	private JsonObject getAttribute(FrankAttribute frankAttribute) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		DeprecationInfo deprecationInfo = frankAttribute.getDeprecationInfo();
		if (deprecationInfo != null) {
			JsonObject deprecationInfoJsonObject = getDeprecated(deprecationInfo);
			result.add(DEPRECATED, deprecationInfoJsonObject);
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
		addIfNotNull(result, DESCRIPTION, frankAttribute.getDescription());
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
		result.add(DESCRIPTION, "If defined and empty or false, then this element and all its children are ignored");
		return result.build();
	}

	private void addIfNotNull(JsonObjectBuilder builder, String field, String value) {
		if(value != null) {
			builder.add(field, value);
		}
	}

	private void addDescription(JsonObjectBuilder builder, String value) {
		if(! StringUtils.isBlank(value)) {
			builder.add(DESCRIPTION, value.replace("\"", "\\\""));
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
		result.add(DESCRIPTION, Constants.MODULE_ELEMENT_DESCRIPTION);
		result.add("type", Constants.MODULE_ELEMENT_NAME);
		return result.build();
	}

	private JsonObject getConfigChild(ConfigChild child) throws JsonException {
		JsonObjectBuilder result = bf.createObjectBuilder();
		if(child.isDeprecated()) {
			result.add(DEPRECATED, child.isDeprecated());
		}
		if(child.getMandatoryStatus() != MandatoryStatus.OPTIONAL) {
			result.add("mandatory", true);
		}
		if(child.isReintroduced()) {
			result.add("reintroduced", true);
		}
		result.add("multiple", child.isAllowMultiple());
		result.add("roleName", child.getRoleName());
		addIfNotNull(result, DESCRIPTION, child.getDescription());
		if(child instanceof ObjectConfigChild objectConfigChild) {
			result.add("type", (objectConfigChild).getElementType().getFullName());
		}
		return result.build();
	}

	private JsonObject getElementNames() {
		final JsonObjectBuilder result = bf.createObjectBuilder();
		result.add(Constants.MODULE_ELEMENT_NAME, getElementName(Constants.MODULE_ELEMENT_NAME, Constants.MODULE_ELEMENT_NAME, Map.of("FrankDocGroup", List.of(Constants.MODULE_ELEMENT_FRANK_DOC_GROUP))));

		additionalRootElements.forEach(element -> result.add(element.getElementName(), getElementName(element.getElementName(), element.getElementName(), Map.of("FrankDocGroup", List.of(Constants.MODULE_ELEMENT_FRANK_DOC_GROUP)))));

		Map<String, List<FrankElement>> elements = getAllElements();
		for (List<FrankElement> elementsPerClass : elements.values()) {
			for (FrankElement frankElement : elementsPerClass) {
				for (String elementName : frankElement.getXmlElementNames()) {
					result.add(elementName, getElementName(elementName, frankElement.getFullName(), frankElement.getLabels()));
				}
			}
		}

		return result.build();
	}

	private JsonObject getEnums() {
		final JsonObjectBuilder result = bf.createObjectBuilder();
		for(AttributeEnum attributeEnum: model.getAllAttributeEnumInstances()) {
			result.add(attributeEnum.getFullName(), getAttributeEnumValues(attributeEnum));
		}
		return result.build();
	}

	private JsonObject getAttributeEnumValues(AttributeEnum en) {
		JsonObjectBuilder result = bf.createObjectBuilder();
		for(EnumValue enumValue: en.getValues()) {
			JsonObjectBuilder valueBuilder = bf.createObjectBuilder();
			if(enumValue.getDescription() != null) {
				valueBuilder.add("description", enumValue.getDescription());
			}
			if(enumValue.isDeprecated()) {
				valueBuilder.add(DEPRECATED, true);
			}
			result.add(enumValue.getLabel(), valueBuilder.build());
		}
		return result.build();
	}

	// We omit the "labels" element if there are no labels. This
	// makes no different in production because the F!F sources
	// do have labels. This way, we can omit labels from unit tests.
	private Optional<JsonObject> getLabels() {
		if(model.getAllLabels().isEmpty()) {
			return Optional.empty();
		}
		final JsonObjectBuilder result = bf.createObjectBuilder();
		for(String label : model.getAllLabels()) {
			JsonArrayBuilder labelValuesObject = bf.createArrayBuilder();
			model.getAllValuesOfLabel(label).forEach(labelValuesObject::add);
			result.add(label, labelValuesObject.build());
		}
		return Optional.of(result.build());
	}

	// The properties are optional, so they can be omitted from unit tests.
	private Optional<JsonArray> getProperties() {
		if (model.getPropertyGroups().isEmpty()) {
			return Optional.empty();
		}

		final var builder = bf.createArrayBuilder();
		model.getPropertyGroups().stream()
			.map(this::groupToJson)
			.forEach(builder::add);

		return Optional.of(builder.build());
	}

	private JsonObject propertyToJson(Property property) {
		JsonObjectBuilder b = bf.createObjectBuilder();

		b.add("name", property.getName());
		if (property.getDescription() != null) {
			b.add("description", property.getDescription());
		}
		if (property.getDefaultValue() != null) {
			b.add("defaultValue", property.getDefaultValue());
		}

		if (!property.getFlags().isEmpty()) {
			final var flagsBuilder = bf.createArrayBuilder();
			property.getFlags().forEach(flagsBuilder::add);
			b.add("flags", flagsBuilder);
		}

		return b.build();
	}

	private JsonObject groupToJson(Group group) {
		JsonObjectBuilder b = bf.createObjectBuilder();

		if (group.getName() != null) {
			b.add("name", group.getName());
		}

		var ab = bf.createArrayBuilder();
		group.getProperties().stream().map(this::propertyToJson).forEach(ab::add);
		b.add("properties", ab);

		return b.build();
	}

	private Optional<JsonObject> getCredentialProviders() {
		if (model.getCredentialProviders().isEmpty()) {
			return Optional.empty();
		}

		final JsonObjectBuilder builder = bf.createObjectBuilder();
		for (CredentialProvider credentialProvider : model.getCredentialProviders()) {
			builder.add(credentialProvider.simpleName(), credentialProviderToJson(credentialProvider));
		}

		return Optional.of(builder.build());
	}

	private JsonObject credentialProviderToJson(CredentialProvider credentialProvider) {
		JsonObjectBuilder jsonObjectBuilder = bf.createObjectBuilder();

		jsonObjectBuilder.add("fullName", credentialProvider.fullName());
		if (credentialProvider.description() != null) {
			jsonObjectBuilder.add("description", credentialProvider.description());

			addNotesToBuilder(credentialProvider.notes(), jsonObjectBuilder);
		}

		return jsonObjectBuilder.build();
	}

	private void addNotesToBuilder(List<Note> notes, JsonObjectBuilder parent) {
		if (!notes.isEmpty()) {
			final var builder = bf.createArrayBuilder();

			notes.forEach(note -> {
				final var noteBuilder = bf.createObjectBuilder();

				noteBuilder.add("type", note.type().name());
				noteBuilder.add("value", note.value());

				builder.add(noteBuilder);
			});

			parent.add("notes", builder.build());
		}
	}

	private Optional<JsonObject> getServletAuthenticators() {
		if (model.getServletAuthenticators().isEmpty()) {
			return Optional.empty();
		}

		final var builder = bf.createObjectBuilder();
		for (ServletAuthenticator servletAuthenticator : model.getServletAuthenticators()) {
			builder.add(servletAuthenticator.simpleName(), servletAuthenticatorToJson(servletAuthenticator));
		}

		return Optional.of(builder.build());
	}

	private JsonObject servletAuthenticatorToJson(ServletAuthenticator servletAuthenticator) {
		JsonObjectBuilder b = bf.createObjectBuilder();

		b.add("fullName", servletAuthenticator.fullName());
		if (servletAuthenticator.description() != null) {
			b.add("description", servletAuthenticator.description());
		}

		if (!servletAuthenticator.methods().isEmpty()) {
			b.add("methods", getServletAuthenticatorMethods(servletAuthenticator.methods()));
		}

		addNotesToBuilder(servletAuthenticator.notes(), b);

		return b.build();
	}

	private JsonArray getServletAuthenticatorMethods(List<ServletAuthenticatorMethod> methods) {
		final JsonArrayBuilder result = bf.createArrayBuilder();

		for (ServletAuthenticatorMethod method : methods) {
			JsonObjectBuilder methodObject = bf.createObjectBuilder();

			methodObject.add("name", method.name());
			if (method.description() != null) {
				methodObject.add("description", method.description());
			}

			result.add(methodObject.build());
		}

		return result.build();
	}

}
