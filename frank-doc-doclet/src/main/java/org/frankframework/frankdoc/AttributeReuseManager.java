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
package org.frankframework.frankdoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.util.XmlBuilder;

class AttributeReuseManager {
	private static class AttributeReference {
		FrankAttribute frankAttribute;
		XmlBuilder xsdGroup;
		String xsdGroupName;
		boolean reused = true;

		AttributeReference(FrankAttribute frankAttribute, XmlBuilder xsdGroup, String xsdGroupName) {
			this.frankAttribute = frankAttribute;
			this.xsdGroup = xsdGroup;
			this.xsdGroupName = xsdGroupName;
		}
	}

	private static class AttributeToInsert {
		XmlBuilder attributeBuilder;
		XmlBuilder parentBuilder;

		AttributeToInsert(XmlBuilder attributeBuilder, XmlBuilder parentBuilder) {
			this.attributeBuilder = attributeBuilder;
			this.parentBuilder = parentBuilder;
		}
	}

	private final List<Object> attributeSequence = new ArrayList<>();
	private final Map<String, ReferencedFrankAttributeNameGroup> groupedAttributeReferences = new HashMap<>();
	private final Set<FrankAttribute> definedReusableAttributes = new HashSet<>();

	/**
	 * Call this method when a FrankAttribute is encountered that should appear in an
	 * attribute group of the XSD. This class will determine later whether the attribute should
	 * be added as a reference or inline.
	 */
	void addAttribute(FrankAttribute frankAttribute, XmlBuilder xsdGroup, String xsdGroupName) {
		AttributeReference attributeReference = new AttributeReference(frankAttribute, xsdGroup, xsdGroupName);
		attributeSequence.add(attributeReference);
		if(groupedAttributeReferences.containsKey(frankAttribute.getName())) {
			groupedAttributeReferences.get(frankAttribute.getName()).add(attributeReference);
		} else {
			groupedAttributeReferences.put(frankAttribute.getName(), new ReferencedFrankAttributeNameGroup(attributeReference));
		}
	}

	/**
	 * Call this method to add any attribute that does not appear in the FrankDocModel
	 * (e.g. "active", "roleName"). If such attributes would be added directly to the
	 * XSD, they would always come first which is not what we want.
	 */
	void addAttribute(XmlBuilder attributeBuilder, XmlBuilder group) {
		attributeSequence.add(new AttributeToInsert(attributeBuilder, group));
	}

	/**
	 * Call this method to add all the attributes to the XSD.
	 */
	void buildAttributes(AttributeReuseManagerCallback callback) {
		classifyAttributesToBuild();
		buildClassifiedAttributes(callback);
	}

	private void classifyAttributesToBuild() {
		groupedAttributeReferences.values().forEach(this::classifyAttributesHavingNameInCommon);
	}

	private void classifyAttributesHavingNameInCommon(ReferencedFrankAttributeNameGroup nameGroup) {
		if(nameGroup.hasMultipleReusedAttributes()) {
			nameGroup.setNoAttributeReferenceReuses();
		} else {
			nameGroup.getAttributesReferencedOnce().forEach(ReferencedFrankAttribute::setNoAttributeReferenceReuses);
		}
	}

	private void buildClassifiedAttributes(AttributeReuseManagerCallback callback) {
		for(Object item: attributeSequence) {
			if (item instanceof AttributeReference attributeReference) {
				buildAttribute(attributeReference, callback);
			} else {
				AttributeToInsert attributeToInsert = (AttributeToInsert) item;
				attributeToInsert.parentBuilder.addSubElement(attributeToInsert.attributeBuilder);
			}
		}
	}

	private void buildAttribute(AttributeReference attributeReference, AttributeReuseManagerCallback callback) {
		if(attributeReference.reused) {
			if(! definedReusableAttributes.contains(attributeReference.frankAttribute)) {
				callback.addReusableAttribute(attributeReference.frankAttribute);
				definedReusableAttributes.add(attributeReference.frankAttribute);
			}
			callback.addReusedAttributeReference(attributeReference.frankAttribute, attributeReference.xsdGroup, attributeReference.xsdGroupName);
		} else {
			callback.addAttributeInline(attributeReference.frankAttribute, attributeReference.xsdGroup, attributeReference.xsdGroupName);
		}
	}

	private static class ReferencedFrankAttribute {
		private final List<AttributeReference> itemsThatShareFrankAttribute = new ArrayList<>();

		ReferencedFrankAttribute(AttributeReference first) {
			itemsThatShareFrankAttribute.add(first);
		}

		void add(AttributeReference item) {
			if(item.frankAttribute != itemsThatShareFrankAttribute.getFirst().frankAttribute) {
				throw new IllegalArgumentException("Referenced FrankAttribute does not match");
			}
			itemsThatShareFrankAttribute.add(item);
		}

		boolean isFrankAttributeReferencedOnce() {
			return itemsThatShareFrankAttribute.size() == 1;
		}

		String getAttributeName() {
			return itemsThatShareFrankAttribute.getFirst().frankAttribute.getName();
		}

		void setNoAttributeReferenceReuses() {
			itemsThatShareFrankAttribute.forEach(item -> item.reused = false);
		}
	}

	private static class ReferencedFrankAttributeNameGroup {
		private final Map<FrankAttribute, ReferencedFrankAttribute> referencedFrankAttributes = new HashMap<>();

		ReferencedFrankAttributeNameGroup(AttributeReference first) {
			ReferencedFrankAttribute referencedFrankAttribute = new ReferencedFrankAttribute(first);
			referencedFrankAttributes.put(first.frankAttribute, referencedFrankAttribute);
		}

		private String getName() {
			return referencedFrankAttributes.values().iterator().next().getAttributeName();
		}

		void add(AttributeReference item) {
			if(! item.frankAttribute.getName().equals(getName())) {
				throw new IllegalArgumentException("FrankAttribute name mismatch");
			}
			if(referencedFrankAttributes.containsKey(item.frankAttribute)) {
				referencedFrankAttributes.get(item.frankAttribute).add(item);
			} else {
				ReferencedFrankAttribute referencedFrankAttribute = new ReferencedFrankAttribute(item);
				referencedFrankAttributes.put(item.frankAttribute, referencedFrankAttribute);
			}
		}

		List<ReferencedFrankAttribute> getAttributesReferencedOnce() {
			return referencedFrankAttributes.values().stream()
					.filter(ReferencedFrankAttribute::isFrankAttributeReferencedOnce)
					.toList();
		}

		boolean hasMultipleReusedAttributes() {
			return referencedFrankAttributes.values().stream()
					.filter(g -> ! g.isFrankAttributeReferencedOnce())
					.skip(1)
					.findAny()
					.isPresent();
		}

		void setNoAttributeReferenceReuses() {
			referencedFrankAttributes.values().forEach(ReferencedFrankAttribute::setNoAttributeReferenceReuses);
		}
	}
}
