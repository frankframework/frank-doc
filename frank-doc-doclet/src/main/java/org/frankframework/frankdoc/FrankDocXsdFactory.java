/*
Copyright 2020, 2021, 2022, 2025 WeAreFrank!

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

import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.AttributeUse.OPTIONAL;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.AttributeUse.REQUIRED;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.AttributeValueStatus.DEFAULT;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.AttributeValueStatus.FIXED;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addChoice;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addComplexContent;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addComplexType;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addDocumentation;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addElement;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addElementRef;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addElementWithName;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addExtension;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addSequence;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createAnyAttribute;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createAnyOtherNamespaceAttribute;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createAttributeGroup;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createComplexType;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createElement;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createElementWithName;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.createGroup;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.getXmlSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.frankframework.frankdoc.model.AttributeEnum;
import org.frankframework.frankdoc.model.ConfigChild;
import org.frankframework.frankdoc.model.ConfigChildGroupKind;
import org.frankframework.frankdoc.model.ConfigChildSet;
import org.frankframework.frankdoc.model.ElementChild;
import org.frankframework.frankdoc.model.ElementRole;
import org.frankframework.frankdoc.model.ElementType;
import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.model.FrankElement;
import org.frankframework.frankdoc.model.Note;
import org.frankframework.frankdoc.model.ObjectConfigChild;
import org.frankframework.frankdoc.model.TextConfigChild;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.util.XmlBuilder;
import org.frankframework.frankdoc.wrapper.AdditionalRootElement;

/**
 * This class writes the XML schema document (XSD) that checks the validity of a
 * Frank configuration XML file. The XML schema is written based on the information
 * in a {@link FrankDocModel} object (the model).
 *
 * <h1>Summary of XSD definitions</h1>
 *
 * Here is a summary of all definitions that appear in the XSD:
 * <p>
 * <table>
 *   <tr>
 *     <th style="text-align:left">Kind</th>
 *     <th style="text-align:left">Name suffix</th>
 *     <th style="text-align:left">Explanation</th>
 *   </tr>
 *   <tr>
 *     <td><code>xs:element</code></td>
 *     <td>n/a</td>
 *     <td>Allows the integration specialist to use a tag.</td>
 *   </tr>
 *   <tr>
 *     <td><code>xs:complexType</code>
 *     <td><code>Type</code></td>
 *     <td>Expresses the allowed contents of a {@link FrankElement}, to be referenced in config child groups where the element is allowed.</td>
 *   </tr>
 *   <tr>
 *     <td><code>xs:complexType</code>
 *     <td><code>Type</code></td>
 *     <td>Expresses the allowed contents of a {@link FrankElement} such that it can be reused for different roles.</td>
 *   </tr>
 *   <tr>
 *     <td><code>xs:attributeGroup</code></td>
 *     <td><code>DeclaredAttributeGroup</code></td>
 *     <td>Groups the attributes that a {@link FrankElement} allows, omitting inherited attributes.
 *   </tr>
 *   <tr>
 *     <td><code>xs:attributeGroup</code></td>
 *     <td><code>CumulativeAttributeGroup</code></td>
 *     <td>Groups the attributes that a {@link FrankElement} allows, including inherited attributes.
 *   </tr>
 *   <tr>
 *     <td><code>xs:group</code></td>
 *     <td><code>DeclaredChildGroup</code></td>
 *     <td>Defines a list of tags that is allowed within a parent tag, disregarding inheritance.</td>
 *   </tr>
 *   <tr>
 *     <td><code>xs:group</code></td>
 *     <td><code>CumulativeChildGroup</code></td>
 *     <td>Defines a list of tags that is allowed within a parent tag, including inheritance.</td>
 *   </tr>
 *     <td><code>xs:group</code></td>
 *     <td><code>ElementGroup</code></td>
 *     <td>Lists all choices that are allowed for a child tag, including the generic element option.</td>
 *   </tr>
 *   <tr>
 *     <td><code>xs:group</code></td>
 *     <td><code>ElementGroupBase</code></td>
 *     <td>Lists all choices that are allowed for a child tag, excluding the generic element option.</td>
 *   </tr>
 *   <tr>
 *     <td><code>xs:simpleType</code></td>
 *     <td><code>AttributeValuesType</code></td>
 *     <td>For attributes constrained by a Java enum, list all allowed values</td>
 *   </tr>
 * </table>
 *
 * @author martijn
 *
 */
public class FrankDocXsdFactory implements AttributeReuseManagerCallback {
	private static final Logger log = LogUtil.getLogger(FrankDocXsdFactory.class);

	private static final Map<XsdVersion, String> outputFileNames = new EnumMap<>(XsdVersion.class);
	public static final String UNBOUNDED = "unbounded";
	public static final String ELEMENT_TYPE_STRING = "xs:string";

	static {
		outputFileNames.put(XsdVersion.STRICT, "strict.xsd");
		outputFileNames.put(XsdVersion.COMPATIBILITY, "compatibility.xsd");
	}

	private static final String ELEMENT_ROLE = "elementRole";
	private static final String CLASS_NAME = "className";
	private static final String ELEMENT_GROUP_BASE = "ElementGroupBase";
	static final String ATTRIBUTE_VALUES_TYPE = "AttributeValuesType";
	static final String VARIABLE_REFERENCE = "variableRef";

	private final FrankDocModel model;
	private final String startClassName;
	private final XsdVersion version;
	private final AttributeReuseManager attributeReuseManager = new AttributeReuseManager();
	private final List<XmlBuilder> xsdElements = new ArrayList<>();
	private final List<XmlBuilder> xsdComplexItems = new ArrayList<>();
	private final List<XmlBuilder> xsdReusedAttributes = new ArrayList<>();
	private final Set<String> namesCreatedFrankElements = new HashSet<>();
	private final Set<ElementRole.Key> idsCreatedElementGroups = new HashSet<>();
	private final ElementGroupManager elementGroupManager;
	private final Set<String> definedAttributeEnumInstances = new HashSet<>();
	private final AttributeTypeStrategy attributeTypeStrategy;
	private final String frankFrameworkVersion;
	/**
	 * Map {@link AdditionalRootElement} enum values for Frank!Framework Java types that have been encountered to the XSD types
	 * that they map to.
	 */
	private final Map<AdditionalRootElement, String> additionalRootElements = new EnumMap<>(AdditionalRootElement.class);

	public FrankDocXsdFactory(FrankDocModel model, AttributeTypeStrategy attributeTypeStrategy, String frankFrameworkVersion, String startClassName, XsdVersion version) {
		this.model = model;
		this.attributeTypeStrategy = attributeTypeStrategy;
		this.frankFrameworkVersion = frankFrameworkVersion;

		this.startClassName = startClassName;
		this.version = version;
		log.trace("Initialized FrankDocXsdFactory with start element name [{}], version [{}] and output file [{}]",
			() -> startClassName, version::toString, () -> outputFileNames.get(version));
		elementGroupManager = new ElementGroupManager(version.getChildSelector(), version.getChildRejector());
	}

	public String getSchema() {
		XmlBuilder xsdRoot = getXmlSchema(frankFrameworkVersion);
		log.trace("Going to create XmlBuilder objects that will be added to the schema root builder afterwards");
		FrankElement startElement = model.findFrankElement(startClassName);
		defineElements(startElement);
		// This call is needed to address generic element option recursion as
		// described in the package doc of the model. If there are generic
		// element options that do not correspond to a ConfigChildSet, then
		// they are finished by this call.
		finishLeftoverGenericOptionsAttributes();
		log.trace("Creating the attributes");
		attributeReuseManager.buildAttributes(this);
		log.trace("Creating helper types");
		List<XmlBuilder> xsdHelperTypes = attributeTypeStrategy.createHelperTypes();
		log.trace("Have the XmlBuilder objects. Going to add them in the right order to the schema root builder");
		xsdElements.forEach(xsdRoot::addSubElement);
		xsdComplexItems.forEach(xsdRoot::addSubElement);
		xsdReusedAttributes.forEach(xsdRoot::addSubElement);
		xsdHelperTypes.forEach(xsdRoot::addSubElement);
		log.trace("Populating schema root builder is done. Going to create the XML string to return");

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			String.format("<!-- %s -->", this.version.getDescription()) + "\n" +
			xsdRoot.toXML(false);
	}

	/**
	 * Starts the recursion to generate all XML element definitions.
	 * We decide here whether the root element is introduced as an
	 * XML type declaration (strict XSD) or as an XML element declaration
	 * (compatibility XSD). We need a type for the strict XSD because we
	 * reuse XSD code to define {@code <Configuration>} and to define {@code <Module>}.
	 * <br/>
	 * In strict XSD, the {@code <Module>} element has to appear as a child of
	 * {@code <Configuration>}. This is done elsewhere by method
	 * {@link #addReferencedEntityRootChildIfApplicable(XmlBuilder, FrankElement)}.
	 */
	private void defineElements(FrankElement startElement) {
		switch(version) {
		case STRICT:
			addStartElementAsTypeReference(startElement);
			addReferencedEntityRoot(startElement);
			recursivelyDefineReusableFrankElementType(startElement);
			break;
		case COMPATIBILITY:
			recursivelyDefineRootFrankElement(startElement);
			break;
		default:
			throw new IllegalArgumentException("Cannot happen - all case labels should be in switch");
		}
		additionalRootElements.forEach(this::createAdditionalRootElement);
	}

	/** Defines XML element {@code <Configuration>} */
	private void addStartElementAsTypeReference(FrankElement startElement) {
		log.trace("Adding element [{}] as type reference", startElement::getSimpleName);
		XmlBuilder startElementBuilder = createElementWithName(startElement.getSimpleName());
		xsdElements.add(startElementBuilder);
		addDocumentationFrom(startElementBuilder, startElement);
		XmlBuilder complexType = addComplexType(startElementBuilder);
		XmlBuilder complexContent = addComplexContent(complexType);
		addExtension(complexContent, xsdElementType(startElement));
	}

	/** Defines XML element {@code <Module>} */
	private void addReferencedEntityRoot(FrankElement startElement) {
		log.trace("Adding element [{}] using type [{}]", () -> Constants.MODULE_ELEMENT_NAME, () -> xsdElementType(startElement));
		XmlBuilder startElementBuilder = createElementWithName(Constants.MODULE_ELEMENT_NAME);
		xsdElements.add(startElementBuilder);
		addDocumentation(startElementBuilder, Constants.MODULE_ELEMENT_DESCRIPTION);
		XmlBuilder complexType = addComplexType(startElementBuilder);
		String declaredChildGroup = getConfigChildGroupOf(startElement);
		if (declaredChildGroup != null) {
			FrankDocXsdFactoryXmlUtils.addGroupRef(complexType, declaredChildGroup);
		}
		log.trace("Adding attribute active explicitly to [{}] and also any attribute in another namespace", () -> Constants.MODULE_ELEMENT_NAME);
		XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
		attributeReuseManager.addAttribute(attributeActive, complexType);
		XmlBuilder anyOther = createAnyOtherNamespaceAttribute();
		attributeReuseManager.addAttribute(anyOther, complexType);
	}

	/**
	 * Defines XML element {@code <PipelinePart/>} or other additional root elements
	 */
	private void createAdditionalRootElement(AdditionalRootElement additionalRootElement, String referencedElementGroupName) {
		String elementName = additionalRootElement.getElementName();
		log.trace("Creating element [{}]", elementName);
		XmlBuilder startElementBuilder = createElementWithName(elementName);
		xsdElements.add(startElementBuilder);
		addDocumentation(startElementBuilder, additionalRootElement.getDocString());
		XmlBuilder complexType = addComplexType(startElementBuilder);
		XmlBuilder sequence = addSequence(complexType);
		FrankDocXsdFactoryXmlUtils.addGroupRef(sequence, referencedElementGroupName, "0", "unbounded");

		log.trace("Adding attribute active explicitly to [{}] and also any attribute in another namespace", elementName);
		XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
		attributeReuseManager.addAttribute(attributeActive, complexType);
		XmlBuilder anyOther = createAnyOtherNamespaceAttribute();
		attributeReuseManager.addAttribute(anyOther, complexType);
	}

	private String getConfigChildGroupOf(FrankElement frankElement) {
		// TODO: Add cumulative group if the start element (typically <Configuration>) has
		//  ancestors with config children. Or even take a declared/cumulative group of an ancestor
		//  if <Configuration> itself has no config children. These do not apply in practice, so
		//  implementing this has not a high priority.
		if(frankElement.getCumulativeConfigChildren(version.getChildSelector(), version.getChildRejector()).isEmpty()) {
			// This will not happen in production, but we have integration tests in which config children are not relevant.
			return null;
		}
		if (frankElement.hasOrInheritsPluralConfigChildren(version.getChildSelector(), version.getChildRejector())) {
			return xsdPluralGroupNameForChildren(frankElement);
		} else {
			return xsdDeclaredGroupNameForChildren(frankElement);
		}
	}

	private void recursivelyDefineRootFrankElement(FrankElement frankElement) {
		log.trace("Enter root FrankElement [{}]", frankElement::getFullName);
		if(checkNotDefined(frankElement)) {
			String xsdElementName = frankElement.getSimpleName();
			XmlBuilder elementBuilder = createElement(xsdElementName, getTypeName(xsdElementName));
			xsdElements.add(elementBuilder);
			XmlBuilder attributeBuilder = recursivelyDefineSimpleFrankElementTypeUnchecked(frankElement, getTypeName(xsdElementName));
			// Adding the other-namespace attribute cannot be done in recursivelyDefineXsdElementUnchecked() because the anyAttribute must be last.
			// After recursivelyDefineXsdElementUnchecked(), the "elementRole" attribute is added separately for non-root elements.
			log.trace("Adding any attribute in another namespace to root element [{}]", frankElement::getFullName);
			XmlBuilder anyOther = createAnyOtherNamespaceAttribute();
			attributeReuseManager.addAttribute(anyOther, attributeBuilder);
		}
		log.trace("Leave root FrankElement [{}]", frankElement::getFullName);
	}

	private String getTypeName(String elementName) {
		return elementName + "Type";
	}

	private XmlBuilder recursivelyDefineSimpleFrankElementTypeUnchecked(FrankElement frankElement, String xsdElementTypeName) {
		log.trace("Defining [{}] for FrankElement [{}], no groups for attributes or config children", () -> xsdElementTypeName, frankElement::getFullName);
		XmlBuilder complexType = createComplexType(xsdElementTypeName);
		xsdElements.add(complexType);
		addDocumentationFrom(complexType, frankElement);
		log.trace("Adding cumulative config chidren of FrankElement [{}] to XSD type [{}]", frankElement::getFullName, () -> xsdElementTypeName);
		if (frankElement.hasOrInheritsPluralConfigChildren(version.getChildSelector(), version.getChildRejector())) {
			log.trace("FrankElement [{}] has plural config children", frankElement::getFullName);
			// Within <xs:sequence><xs:choice> group, we cannot enforce that mandatory config children are
			// included. We also do not check there that non-plural config children occur at most once.
			// Martijn investigated whether the <xs:all> element can be used instead. That element
			// enforces that each <xs:element> inside occurs exactly once without enforcing a sequence.
			// However, within <xs:all> the <xs:group> element is not allowed. Therefore <xs:all> will
			// not work. We accept the limitations of <xs:sequence><xs:choice>, becuase plural config
			// children are rare within the Frank!Doc.
			XmlBuilder sequence = addSequence(complexType);
			XmlBuilder choice = addChoice(sequence, "0", UNBOUNDED);
			for(ConfigChildSet configChildSet: frankElement.getCumulativeConfigChildSets()) {
				addPluralConfigChild(choice, configChildSet, frankElement);
			}
		} else {
			log.trace("FrankElement [{}] does not have plural config children", frankElement::getFullName);
			List<ConfigChild> cumulativeConfigChildren = frankElement.getCumulativeConfigChildren(version.getChildSelector(), version.getChildRejector());
			if(cumulativeConfigChildren.isEmpty()) {
				log.trace("There are no config children, not adding <sequence><choice>");
			} else {
				XmlBuilder sequence = addSequence(complexType);
				final XmlBuilder childContext = version.configChildBuilderWithinSequence(sequence);
				cumulativeConfigChildren.forEach(c -> addConfigChild(childContext, c));
			}
		}
		log.trace("Adding cumulative attributes of FrankElement [{}] to XSD element type [{}]", frankElement::getFullName, () -> xsdElementTypeName);
		addAttributeList(complexType, frankElement.getCumulativeAttributes(version.getChildSelector(), version.getChildRejector()), xsdElementTypeName);
		log.trace("Adding attribute className for FrankElement [{}]", frankElement::getFullName);
		XmlBuilder classNameAttribute = FrankDocXsdFactoryXmlUtils.createAttribute(CLASS_NAME, FIXED, frankElement.getFullName(), version.getClassNameAttributeUse(frankElement));
		attributeReuseManager.addAttribute(classNameAttribute, complexType);
		log.trace("Adding attribute active for FrankElement [{}]", frankElement::getFullName);
		XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
		attributeReuseManager.addAttribute(attributeActive, complexType);
		return complexType;
	}

	private void recursivelyDefineReusableFrankElementType(FrankElement frankElement) {
		if (log.isTraceEnabled()) {
			log.trace("XML Schema needs reusable type definition (or only groups for config children or attributes) for FrankElement [{}]",
					() -> frankElement == null ? "null" : frankElement.getFullName());
		}
		if (checkNotDefined(frankElement)) {
			ElementBuildingStrategy elementBuildingStrategy = getElementBuildingStrategy(frankElement);
			log.trace("Visiting config children for FrankElement [{}]", frankElement::getFullName);
			addConfigChildren(elementBuildingStrategy, frankElement);
			log.trace("Visiting attributes for FrankElement [{}]", frankElement::getFullName);
			addAttributes(elementBuildingStrategy, frankElement);
			if (!frankElement.isAbstract()) {
				log.trace("Adding attribute className to the element type of [{}]", frankElement::getFullName);
				XmlBuilder classNameAttribute = FrankDocXsdFactoryXmlUtils.createAttribute(CLASS_NAME, FIXED, frankElement.getFullName(), version.getClassNameAttributeUse(frankElement));
				attributeReuseManager.addAttribute(classNameAttribute, elementBuildingStrategy.getElementTypeBuilder());
			}
			if (elementBuildingStrategy.needsSpecialAttributesInElementType()) {
				log.trace("Adding attribute active and anyAttribute in another namespace to the element type of [{}], because there are no attribute groups to put this in", frankElement);
				XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
				attributeReuseManager.addAttribute(attributeActive, elementBuildingStrategy.getElementTypeBuilder());
				XmlBuilder anyOther = createAnyOtherNamespaceAttribute();
				attributeReuseManager.addAttribute(anyOther, elementBuildingStrategy.getElementTypeBuilder());
			}
			log.trace("Creating reusable type definitions (or only groups) for Java ancestors of FrankElement [{}]", frankElement::getFullName);
			recursivelyDefineReusableFrankElementType(frankElement.getNextAncestorThatHasOrRejectsConfigChildren(version.getChildSelector(), version.getChildRejector()));
			recursivelyDefineReusableFrankElementType(frankElement.getNextAncestorThatHasOrRejectsAttributes(version.getChildSelector(), version.getChildRejector()));
			log.trace("Done with reusable XSD type definitions for ancestors of FrankElement [{}]", frankElement::getFullName);
		} else {
			log.trace("Reusable type definition was already included");
		}
	}

	/**
	 * @param frankElement The {@link FrankElement} for which an XSD element or XSD type is needed, or null
	 * @return true if the input is not null and if the element is not yet created.
	 */
	private boolean checkNotDefined(FrankElement frankElement) {
		if (frankElement == null) {
			return false;
		}
		if (namesCreatedFrankElements.contains(frankElement.getFullName())) {
			return false;
		} else {
			namesCreatedFrankElements.add(frankElement.getFullName());
			return true;
		}
	}

	/**
	 * This class is responsible for adding an xs:element in the XML schema if required.
	 * If a FrankElement corresponds to an abstract class, then no XML element
	 * should be added. This is achieved using the derived class ElementOmitter.
	 * <br/>
	 * For an abstract FrankElement, the config child declared/cumulative groups
	 * and the attribute declared/cumulative groups are still needed. Adding them is
	 * outside the scope of this class.
	 */
	private interface ElementBuildingStrategy {
		void onNoAttributes();
		boolean needsSpecialAttributesInElementType();
		XmlBuilder getElementTypeBuilder();
		void addGroupRef(String referencedGroupName);
		void addAttributeGroupRef(String referencedGroupName);

		/**
		 * When there are config children that share a role name (plural config children),
		 * then the element type under construction references only one config child group.
		 * The config child group does not check the sequence of the included elements.
		 * There is no need to also wrap the singleton config child group reference into
		 * {@code <sequence><choice>} if the order is not important.
		 * <br/>
		 * This method is added to ElementBuildingStrategy because we know here
		 * to which XmlBuilder we want to add the group reference.
		 */
		void addThePluralConfigChildGroup(String referencedGroupName);
	}

	private ElementBuildingStrategy getElementBuildingStrategy(FrankElement element) {
		if(element.isAbstract()) {
			log.trace("FrankElement [{}] is abstract, so we do not actually create an XSD type definition. We only create config child or attribute groups to be referenced from other XSD types", element::getFullName);
			return new ElementOmitter();
		} else {
			log.trace("FrankElement [{}] is not abstract. We really make the XSD type definition", element::getFullName);
			return new ElementAdder(element);
		}
	}

	private class ElementAdder implements ElementBuildingStrategy {
		private final XmlBuilder elementTypeBuilder;
		private XmlBuilder configChildBuilder;
		private final FrankElement addingTo;
		private boolean noAttributes = false;

		ElementAdder(FrankElement frankElement) {
			elementTypeBuilder = createComplexType(xsdElementType(frankElement));
			xsdComplexItems.add(elementTypeBuilder);
			this.addingTo = frankElement;
		}

		@Override
		public void onNoAttributes() {
			noAttributes = true;
		}

		@Override
		public boolean needsSpecialAttributesInElementType() {
			return noAttributes;
		}

		@Override
		public XmlBuilder getElementTypeBuilder() {
			return elementTypeBuilder;
		}

		@Override
		public void addGroupRef(String referencedGroupName) {
			log.trace("Appending XSD type def of [{}] with reference to XSD group [{}]", addingTo::getFullName, () -> referencedGroupName);
			// We do not create configChildBuilder during construction, because
			// that would produce an empty <seqneuce><choice> when there are no
			// config children.
			if (configChildBuilder == null) {
				log.trace("Create <sequence><choice> to wrap the config children in");
				configChildBuilder = version.configChildBuilder(elementTypeBuilder);
			} else {
				log.trace("Already have <sequence><choice> for the config children");
			}
			FrankDocXsdFactoryXmlUtils.addGroupRef(configChildBuilder, referencedGroupName);
		}

		@Override
		public void addAttributeGroupRef(String referencedGroupName) {
			log.trace("Appending XSD type def of [{}] with reference to XSD group [{}]", addingTo::getFullName, () -> referencedGroupName);
			XmlBuilder builder = FrankDocXsdFactoryXmlUtils.createAttributeGroupRef(referencedGroupName);
			attributeReuseManager.addAttribute(builder, elementTypeBuilder);
		}

		@Override
		public void addThePluralConfigChildGroup(String referencedGroupName) {
			log.trace("Appending XSD type def of [{}] with reference to XSD group [{}], without adding <sequence><choice>",
				addingTo::getFullName, () -> referencedGroupName);
			FrankDocXsdFactoryXmlUtils.addGroupRef(elementTypeBuilder, referencedGroupName);
		}
	}

	private static class ElementOmitter implements ElementBuildingStrategy {
		@Override
		public void onNoAttributes() {
			// No-op in this implementation
		}
		@Override
		public boolean needsSpecialAttributesInElementType() {
			return false;
		}
		@Override
		public XmlBuilder getElementTypeBuilder() {
			return null;
		}
		@Override
		public void addGroupRef(String referencedGroupName) {
			// No-op in this implementation
		}
		@Override
		public void addAttributeGroupRef(String referencedGroupName) {
			// No-op in this implementation
		}
		@Override
		public void addThePluralConfigChildGroup(String referencedGroupName) {
			// No-op in this implementation
		}
	}

	private void addConfigChildren(ElementBuildingStrategy elementBuildingStrategy, FrankElement frankElement) {
		if(frankElement.hasOrInheritsPluralConfigChildren(version.getChildSelector(), version.getChildRejector())) {
			addConfigChildrenWithPluralConfigChildSets(elementBuildingStrategy, frankElement);
		} else {
			addConfigChildrenNoPluralConfigChildSets(elementBuildingStrategy, frankElement);
		}
	}

	private void addConfigChildrenNoPluralConfigChildSets(ElementBuildingStrategy elementBuildingStrategy, FrankElement frankElement) {
		Consumer<GroupCreator.Callback<ConfigChild>> cumulativeGroupTrigger =
				ca -> frankElement.walkCumulativeConfigChildren(ca, version.getChildSelector(), version.getChildRejector());
		new GroupCreator<>(frankElement, version.getHasRelevantChildrenPredicate(ConfigChild.class), cumulativeGroupTrigger, new GroupCreator.Callback<>() {
			private XmlBuilder cumulativeBuilder;
			private String cumulativeGroupName;

			@Override
			public void noChildren() {
				// No-op in this implementation
			}

			@Override
			public void addDeclaredGroupRef(FrankElement referee) {
				elementBuildingStrategy.addGroupRef(xsdDeclaredGroupNameForChildren(referee));
			}

			@Override
			public void addCumulativeGroupRef(FrankElement referee) {
				elementBuildingStrategy.addGroupRef(xsdCumulativeGroupNameForChildren(referee));
			}

			@Override
			public void addTopLevelDeclaredGroup() {
				addDeclaredGroup();
			}

			@Override
			public void addDeclaredGroup() {
				String groupName = xsdDeclaredGroupNameForChildren(frankElement);
				log.trace("Creating XSD group [{}]", groupName);
				XmlBuilder group = createGroup(groupName);
				xsdComplexItems.add(group);
				XmlBuilder sequence = addSequence(group);
				// Adds <Module> as a child of <Configuration>
				addReferencedEntityRootChildIfApplicable(sequence, frankElement);
				frankElement.getConfigChildren(version.getChildSelector()).forEach(c -> addConfigChild(sequence, c));
				log.trace("Done creating XSD group [{}] on behalf of FrankElement [{}]", () -> groupName, frankElement::getFullName);
			}

			@Override
			public void addCumulativeGroup() {
				cumulativeGroupName = xsdCumulativeGroupNameForChildren(frankElement);
				log.trace("Start creating XSD group [{}]", cumulativeGroupName);
				XmlBuilder group = createGroup(cumulativeGroupName);
				xsdComplexItems.add(group);
				cumulativeBuilder = addSequence(group);
			}

			@Override
			public void handleSelectedChildren(List<ConfigChild> children, FrankElement owner) {
				log.trace("Appending some of the config children of FrankElement [{}] to XSD group [{}]", owner::getFullName, () -> cumulativeGroupName);
				children.forEach(c -> addConfigChild(cumulativeBuilder, c));
			}

			@Override
			public void handleSelectedChildrenOfTopLevel(List<ConfigChild> children, FrankElement owner) {
				handleSelectedChildren(children, owner);
			}

			@Override
			public void handleChildrenOf(FrankElement elem) {
				String referencedGroupName = xsdDeclaredGroupNameForChildren(elem);
				log.trace("Appending XSD group [{}] with reference to [{}]", () -> cumulativeGroupName, () -> referencedGroupName);
				FrankDocXsdFactoryXmlUtils.addGroupRef(cumulativeBuilder, referencedGroupName);
			}

			@Override
			public void handleCumulativeChildrenOf(FrankElement elem) {
				String referencedGroupName = xsdCumulativeGroupNameForChildren(elem);
				log.trace("Appending XSD group [{}] with reference to [{}]", () -> cumulativeGroupName, () -> referencedGroupName);
				FrankDocXsdFactoryXmlUtils.addGroupRef(cumulativeBuilder, referencedGroupName);
			}
		}).run();
	}

	private void addConfigChild(XmlBuilder context, ConfigChild child) {
		log.trace("Adding config child [{}]", child::toString);
		version.checkForMissingDescription(child);
		if (child instanceof ObjectConfigChild objectConfigChild) {
			addObjectConfigChild(context, objectConfigChild);
		} else if (child instanceof TextConfigChild textConfigChild) {
			addTextConfigChild(context, textConfigChild);
		} else {
			throw new IllegalArgumentException("Cannot happen, there are no other ConfigChild subclasses than ObjectConfigChild or TextConfigChild");
		}
		log.trace("Done adding config child [{}]", child::toString);
	}

	private void addObjectConfigChild(XmlBuilder context, ObjectConfigChild child) {
		ElementRole theRole = model.findElementRole(child);
		if(isNoElementTypeNeeded(theRole)) {
			addConfigChildSingleReferredElement(context, child);
		} else {
			addConfigChildWithElementGroup(context, child);
		}
	}

	private boolean isNoElementTypeNeeded(ElementRole role) {
		ElementType elementType = role.getElementType();
		if(elementType.isFromJavaInterface()) {
			return false;
		}
		else {
			return true;
		}
	}

	private void addConfigChildSingleReferredElement(XmlBuilder context, ObjectConfigChild child) {
		ElementRole role = model.findElementRole(child);
		FrankElement elementInType = singleElementOf(role);
		if(elementInType == null) {
			log.trace("Omitting config child [{}] because of name conflict", child::toString);
			return;
		}
		String referredXsdElementName = elementInType.getXsdElementName(role);
		log.trace("Config child appears as element reference to FrankElement [{}], XSD element [{}]", elementInType::getFullName, () -> referredXsdElementName);
		addElement(context, referredXsdElementName, getTypeName(referredXsdElementName), getMinOccurs(child), getMaxOccurs(child));
		recursivelyDefineSimpleFrankElementType(elementInType, role);
	}

	private FrankElement singleElementOf(ElementRole elementRole) {
		List<FrankElement> members = elementRole.getMembers();
		if(members.isEmpty()) {
			return null;
		}
		return members.iterator().next();
	}

	private void recursivelyDefineSimpleFrankElementType(FrankElement frankElement, ElementRole role) {
		log.trace("FrankElement [{}] is needed in XML Schema", frankElement::getFullName);
		if(checkNotDefined(frankElement)) {
			log.trace("Not yet defined in XML Schema, going to define a type for it without reusable config children or attribute groups");
			String xsdElementName = frankElement.getXsdElementName(role);
			XmlBuilder attributeBuilder = recursivelyDefineSimpleFrankElementTypeUnchecked(frankElement, getTypeName(xsdElementName));
			log.trace("Adding attribute [{}] for FrankElement [{}]", () -> ELEMENT_ROLE, frankElement::getFullName);
			XmlBuilder attributeElementRole = FrankDocXsdFactoryXmlUtils.createAttribute(ELEMENT_ROLE, FIXED, role.getRoleName(), version.getRoleNameAttributeUse());
			attributeReuseManager.addAttribute(attributeElementRole, attributeBuilder);
			// Adding the other-namespace attribute cannot be done in recursivelyDefineXsdElementUnchecked because the anyAttribute must be last.
			log.trace("Adding any attribute in another namespace to FrankElement [{}]", frankElement::getFullName);
			XmlBuilder anyOther = createAnyOtherNamespaceAttribute();
			attributeReuseManager.addAttribute(anyOther, attributeBuilder);
			log.trace("Done defining type for FrankElement [{}], XSD element [{}]", frankElement::getFullName, () -> xsdElementName);
		} else {
			log.trace("Already defined in XML Schema");
		}
	}

	private XmlBuilder addConfigChildWithElementGroup(XmlBuilder context, ObjectConfigChild child) {
		log.trace("Config child appears as element group reference");
		// If this is a ConfigChildGroupKind.MIXED config child set, then the warning about that has been written already
		// during model creation. We can assume we only have OBJECT config children.
		ConfigChildSet configChildSet = child.getOwningElement().getConfigChildSet(child.getRoleName());
		if(log.isTraceEnabled()) {
			ThreadContext.push(String.format("Owning element [%s], ConfigChildSet [%s]", child.getOwningElement().getSimpleName(), configChildSet.toString()));
		}
		List<ElementRole> roles = configChildSet.getFilteredElementRoles(version.getChildSelector(), version.getChildRejector());
		requestElementGroupForConfigChildSet(configChildSet, roles);
		if(log.isTraceEnabled()) {
			ThreadContext.pop();
		}
		return FrankDocXsdFactoryXmlUtils.addGroupRef(context, elementGroupManager.getGroupName(roles), getMinOccurs(child), getMaxOccurs(child));
	}

	private void requestElementGroupForConfigChildSet(ConfigChildSet configChildSet, List<ElementRole> roles) {
		requestElementGroup(roles);
		log.trace("Checking whether generic element option has its attributes");
		if (elementGroupManager.hasGenericOptionAttributeTask(configChildSet)) {
			log.trace("Finishing generic element option with its attributes");
			XmlBuilder builder = elementGroupManager.doGenericOptionAttributeTask(configChildSet);
			addGenericElementOptionAttributes(builder, configChildSet);
		} else {
			log.trace("Generic element option already has its attributes");
		}
		log.trace("Done with generic element option attributes");
	}

	private void requestElementGroup(List<ElementRole> roles) {
		log.trace("Doing requestElementGroup");
		Set<ElementRole.Key> key = ConfigChildSet.getKey(roles);
		log.trace("Element group needed for ElementRole-s [{}]", () -> ElementRole.Key.describeCollection(key));
		if (!elementGroupManager.groupExists(key)) {
			log.trace("Element group does not exist, creating it");
			String groupName = elementGroupManager.addGroup(key);
			XmlBuilder group = FrankDocXsdFactoryXmlUtils.createGroup(groupName);
			xsdComplexItems.add(group);
			XmlBuilder choice = addChoice(group);
			addElementGroupGenericOption(choice, roles);
			addElementGroupOptions(choice, roles);
			String elementTypeSimpleName = key.iterator().next().getElementTypeSimpleName();

			// The "Include" element should be manually added to the XSD as part of the "xs:choice" for this element-group.
			// We cannot have it added in a simpler way from the Frank!Framework code, as it will then not be valid as
			// part of the choice of elements in this element-group, but as a separate group of elements.
			if (model.shouldGetIncludeElement(elementTypeSimpleName)) {
				addElement(choice, "Include", "IncludeType", "0", "unbounded");
				additionalRootElements.put(model.getAdditionalRootElement(elementTypeSimpleName), groupName);
			}
		} else {
			log.trace("Element group already exists");
		}
		log.trace("Done requestElementGroup");
	}

	private void addElementGroupOptions(XmlBuilder context, List<ElementRole> roles) {
		for(ElementRole role: roles) {
			if(isNoElementTypeNeeded(role)) {
				log.trace("ElementRole [{}] is not interface-based, nothing to do for this role", role::toString);
				continue;
			}
			String groupName = role.createXsdElementName(ELEMENT_GROUP_BASE);
			log.trace("Adding group [{}] of role [{}] to element group", () -> groupName, role::toString);
			FrankDocXsdFactoryXmlUtils.addGroupRef(context, groupName);
			if (!idsCreatedElementGroups.contains(role.getKey())) {
				idsCreatedElementGroups.add(role.getKey());
				log.trace("Creating group [{}] for role [{}]", () -> groupName, role::toString);
				defineElementGroupBaseUnchecked(role);
				log.trace("Done creating group [{}] for role [{}]", () -> groupName, role::toString);
			} else {
				log.trace("Group [{}] of role [{}] exists, no need to create it again", () -> groupName, role::toString);
			}
		}
	}

	private void defineElementGroupBaseUnchecked(ElementRole role) {
		String groupName = role.createXsdElementName(ELEMENT_GROUP_BASE);
		XmlBuilder group = createGroup(groupName);
		xsdComplexItems.add(group);
		XmlBuilder choice = addChoice(group);
		List<FrankElement> frankElementOptions = role.getMembers().stream()
				.filter(version.getElementFilter())
				.toList();

		if (version == XsdVersion.COMPATIBILITY) {
			frankElementOptions = frankElementOptions.stream()
				.filter(f -> f != role.getDefaultElementOptionConflict())
				.toList();
		}

		for(FrankElement frankElement: frankElementOptions) {
			log.trace("Append ElementGroup with FrankElement [{}]", frankElement::getFullName);
			addElementToElementGroup(choice, frankElement, role);
		}
	}

	private void addElementToElementGroup(XmlBuilder context, FrankElement frankElement, ElementRole role) {
		String referredXsdElementName = frankElement.getXsdElementName(role);
		if(isNoElementTypeNeeded(role)) {
			log.error("Expected ElementRole [{}] to be interface-based", role);
		} else {
			log.trace("FrankElement [{}] in role [{}] appears as type reference, XSD element [{}]",
				frankElement::getFullName, role::toString, () -> referredXsdElementName);
			addElementTypeRefToElementGroup(context, frankElement, role);
			recursivelyDefineReusableFrankElementType(frankElement);
		}
	}

	private void addElementTypeRefToElementGroup(XmlBuilder context, FrankElement frankElement, ElementRole role) {
		XmlBuilder element = addElementWithName(context, frankElement.getXsdElementName(role));
		addDocumentationFrom(element, frankElement);
		XmlBuilder complexType = addComplexType(element);
		XmlBuilder complexContent = addComplexContent(complexType);
		XmlBuilder extension = addExtension(complexContent, xsdElementType(frankElement));

		log.trace("Adding attribute [{}] for FrankElement [{}]", () -> ELEMENT_ROLE, frankElement::getFullName);
		XmlBuilder attributeElementRole = FrankDocXsdFactoryXmlUtils.createAttribute(ELEMENT_ROLE, FIXED, role.getRoleName(), version.getRoleNameAttributeUse());
		attributeReuseManager.addAttribute(attributeElementRole, extension);

		if (version == XsdVersion.STRICT && role.getDefaultElementOptionConflict() == frankElement) {
			attributeReuseManager.addAttribute(createAnyAttribute(), extension);
		}
	}

	private void addDocumentationFrom(XmlBuilder element, FrankElement frankElement) {
		if(version == XsdVersion.STRICT) {
			if (!StringUtils.isBlank(frankElement.getDescription())) {
				StringBuilder description = new StringBuilder(frankElement.getDescription());

				if (!frankElement.getNotes().isEmpty()) {
					description.append("<br>");
				}
				for (Note note : frankElement.getNotes()) {
					description.append("<br><b>")
						.append(note.type().name())
						.append("</b>")
						.append("<p>")
						.append(note.value())
						.append("</p>");
				}

				addDocumentation(element, description.toString());
			}
		}
	}

	private void addElementGroupGenericOption(XmlBuilder context, List<ElementRole> roles) {
		if (version == XsdVersion.STRICT && roles.stream().noneMatch(role -> role.getDefaultElementOptionConflict() == null)) {
			return;
		}

		log.trace("Doing the generic element option, role group [{}]", () -> ElementRole.describeCollection(roles));
		String roleName = ElementGroupManager.getRoleName(roles);
		XmlBuilder genericElementOption = addElementWithName(context, Utils.toUpperCamelCase(roleName));
		XmlBuilder complexType = addComplexType(genericElementOption);
		fillGenericOption(complexType, roles);
		// We do not add the attributes here directly, because we may
		// have to solve a conflict between a member FrankElement and
		// the XML element name of the generic element option. We need a ConfigChildSet to find
		// this possible conflicting FrankElement, but it is not available here.
		// We thus add a task to the ElementGroupManager. Later we check
		// whether a task is available corresponding to the related
		// ConfigChildSet. If there is no corresponding ConfigChildSet,
		// the attributes are set by method finishLeftoverGenericOptionsAttributes().
		elementGroupManager.addGenericOptionAttributeTask(roles, complexType);
		log.trace("Done with the generic element option, role group [{}]", () -> ElementRole.describeCollection(roles));
	}

	private void addGenericElementOptionAttributes(XmlBuilder complexType, ConfigChildSet configChildSet) {
		log.trace("Enter for ConfigChildSet [{}]", configChildSet::toString);
		log.trace("Adding attribute [{}] to generic element option", AttributeTypeStrategy.ATTRIBUTE_ACTIVE_NAME);
		XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
		attributeReuseManager.addAttribute(attributeActive, complexType);
		log.trace("Adding attribute [{}] to generic element option", ELEMENT_ROLE);
		XmlBuilder attributeElementRole = FrankDocXsdFactoryXmlUtils.createAttribute(ELEMENT_ROLE, FIXED, configChildSet.getRoleName(), version.getRoleNameAttributeUse());
		attributeReuseManager.addAttribute(attributeElementRole, complexType);
		Optional<String> defaultFrankElementName = configChildSet.getGenericElementOptionDefault(version.getElementFilter());
		XmlBuilder attributeClassName;
		if(defaultFrankElementName.isPresent()) {
			log.trace("Adding attribute [{}] with default [{}]", () -> CLASS_NAME, defaultFrankElementName::get);
			attributeClassName = FrankDocXsdFactoryXmlUtils.createAttribute(CLASS_NAME, DEFAULT, defaultFrankElementName.get(), OPTIONAL);
		} else {
			log.trace("Adding attribute [{}] without default", () -> CLASS_NAME);
			attributeClassName = FrankDocXsdFactoryXmlUtils.createAttribute(CLASS_NAME, DEFAULT, null, REQUIRED);
		}
		attributeReuseManager.addAttribute(attributeClassName, complexType);
		// The XSD is invalid if addAnyAttribute is added before attributes elementType and className.
		log.trace("Adding any attribute in another namespace");
		XmlBuilder anyAttribute = FrankDocXsdFactoryXmlUtils.createAnyAttribute();
		attributeReuseManager.addAttribute(anyAttribute, complexType);
	}

	private void finishLeftoverGenericOptionsAttributes() {
		log.trace("Setting attributes of leftover nested generic elements");
		for(GenericOptionAttributeTask task: elementGroupManager.doLeftoverGenericOptionAttributeTasks()) {
			log.trace("Have to do element group [{}]", () -> task.getRolesKey().toString());
			addGenericElementOptionAttributes(task.getBuilder(), task.getRolesKey().iterator().next().getRoleName());
		}
		log.trace("Done setting attributes of leftover nested generic elements");
	}

	private void addGenericElementOptionAttributes(XmlBuilder complexType, String roleName) {
		XmlBuilder attributeElementRole = FrankDocXsdFactoryXmlUtils.createAttribute(ELEMENT_ROLE, FIXED, roleName, version.getRoleNameAttributeUse());
		attributeReuseManager.addAttribute(attributeElementRole, complexType);
		XmlBuilder attributeClassName = FrankDocXsdFactoryXmlUtils.createAttribute(CLASS_NAME, DEFAULT, null, REQUIRED);
		attributeReuseManager.addAttribute(attributeClassName, complexType);
		// The XSD is invalid if addAnyAttribute is added before attributes elementType and className.
		XmlBuilder anyAttribute = FrankDocXsdFactoryXmlUtils.createAnyAttribute();
		attributeReuseManager.addAttribute(anyAttribute, complexType);
	}

	private void fillGenericOption(XmlBuilder context, List<ElementRole> parents) {
		Map<String, List<ConfigChild>> memberChildrenByRoleName = ConfigChildSet.getMemberChildren(
				parents, version.getChildSelector(), version.getChildRejector(), version.getElementFilter());
		List<String> names = new ArrayList<>(memberChildrenByRoleName.keySet());
		Collections.sort(names);
		XmlBuilder choice = null;
		for(String name: names) {
			if(choice == null) {
				XmlBuilder sequence = addSequence(context);
				choice = addChoice(sequence, "0", UNBOUNDED);
			}
			addConfigChildrenOfRoleNameToGenericOption(choice, memberChildrenByRoleName.get(name));
		}
	}

	private void addConfigChildrenOfRoleNameToGenericOption(XmlBuilder context, List<ConfigChild> configChildren) {
		ConfigChild configChild = configChildren.get(0);
		String roleName = configChild.getRoleName();
		switch(ConfigChildGroupKind.groupKind(configChildren)) {
		case TEXT:
			String elementType = ((TextConfigChild)configChild).getElementTypeName();
			addTextConfigChildToGenericOption(context, roleName, elementType);
			break;
		case MIXED:
			log.error("Encountered group of config children that mixes TextConfigChild and ObjectConfigChild, not supported: [{}]",
					ConfigChild.toString(configChildren));
			// No break, consider only the ObjectConfigChild instances instead of throwing exception.
		case OBJECT:
			addObjectConfigChildrenToGenericOption(context, configChildren);
			break;
		default:
			throw new IllegalArgumentException("Should not come here because switch should cover all enum values");
		}
	}

	private void addObjectConfigChildrenToGenericOption(XmlBuilder context, List<ConfigChild> configChildren) {
		String roleName = configChildren.get(0).getRoleName();
		List<ElementRole> childRoles = ElementRole.promoteIfConflict(ConfigChild.getElementRoleStream(configChildren).toList());
		if((childRoles.size() == 1) && isNoElementTypeNeeded(childRoles.get(0))) {
			log.trace("A single ElementRole [{}] that appears as element reference", () -> childRoles.get(0).toString());
			addElementRoleAsElement(context, childRoles.get(0));
		} else {
			if(log.isTraceEnabled()) {
				ThreadContext.push(String.format("nest [%s]", roleName));
			}
			requestElementGroup(childRoles);
			if(log.isTraceEnabled()) {
				ThreadContext.pop();
			}
			FrankDocXsdFactoryXmlUtils.addGroupRef(context, elementGroupManager.getGroupName(childRoles));
		}
	}

	private void addElementRoleAsElement(XmlBuilder context, ElementRole elementRole) {
		FrankElement elementInType = singleElementOf(elementRole);
		if(elementInType == null) {
			log.trace("Omitting ElementRole [{}] from group because of conflict", elementRole::toString);
			return;
		}
		String referredXsdElementName = elementInType.getXsdElementName(elementRole);
		addElement(context, referredXsdElementName, getTypeName(referredXsdElementName));
		recursivelyDefineSimpleFrankElementType(elementInType, elementRole);
	}

	private void addTextConfigChild(XmlBuilder context, TextConfigChild child) {
		addElement(context, Utils.toUpperCamelCase(child.getRoleName()), child.getElementTypeName(), getMinOccurs(child), getMaxOccurs(child));
	}

	/*
	 * In the generic element option, all member children are treated as optional. This is justified
	 * by the follow abstract argument. Say there is an interface-based config child with role name "a".
	 * It gives rise to XML tag <A> that has a className attribute. It can stand for any class
	 * that implements the interface of the config child. If some of these classes have a mandatory
	 * config child, the mandatory config child should not be a mandatory child element of <A>.
	 * The reason is that <A> may also stand for a class that does not have the config child.
	 */
	private void addTextConfigChildToGenericOption(XmlBuilder context, String roleName, String elementType) {
		addElement(context, Utils.toUpperCamelCase(roleName), elementType, "0", UNBOUNDED);
	}

	private void addReferencedEntityRootChildIfApplicable(XmlBuilder context, FrankElement declaredGroupOwner) {
		if((version == XsdVersion.STRICT) && declaredGroupOwner.getFullName().equals(model.getRootClassName())) {
			log.trace("Adding referenced entity file root [{}] as config child", Constants.MODULE_ELEMENT_NAME);
			addElementRef(context, Constants.MODULE_ELEMENT_NAME, "0", UNBOUNDED);
		}
	}

	void addConfigChildrenWithPluralConfigChildSets(ElementBuildingStrategy elementBuildingStrategy, FrankElement frankElement) {
		log.trace("Applying algorithm for plural config children for FrankElement [{}]", frankElement::getFullName);
		if(! frankElement.hasFilledConfigChildSets(version.getChildSelector(), version.getChildRejector())) {
			FrankElement ancestor = frankElement.getNextPluralConfigChildrenAncestor(version.getChildSelector(), version.getChildRejector());
			log.trace("No config children, inheriting from [{}]", ancestor::getFullName);
			elementBuildingStrategy.addThePluralConfigChildGroup(xsdPluralGroupNameForChildren(ancestor));
		} else {
			log.trace("Adding new group for plural config children for FrankElement [{}]", frankElement::getFullName);
			addConfigChildrenWithPluralConfigChildSetsUnchecked(elementBuildingStrategy, frankElement);
		}
		log.trace("Done applying algorithm for plural config children for FrankElement [{}]", frankElement::getFullName);
	}

	private void addConfigChildrenWithPluralConfigChildSetsUnchecked(ElementBuildingStrategy elementBuildingStrategy, FrankElement frankElement) {
		String groupName = xsdPluralGroupNameForChildren(frankElement);
		elementBuildingStrategy.addThePluralConfigChildGroup(groupName);
		XmlBuilder builder = createGroup(groupName);
		xsdComplexItems.add(builder);
		// Within <xs:sequence><xs:choice> group, we cannot enforce that mandatory config children are
		// included. We also do not check there that non-plural config children occur at most once.
		// See comment in recursivelyDefineXsdElementUnchecked().
		XmlBuilder sequence = addSequence(builder);
		XmlBuilder choice = addChoice(sequence, "0", UNBOUNDED);
		// Adds <Module> as a child of <Configuration>
		addReferencedEntityRootChildIfApplicable(choice, frankElement);
		List<ConfigChildSet> configChildSets = frankElement.getCumulativeConfigChildSets();
		for(ConfigChildSet configChildSet: configChildSets) {
			addPluralConfigChild(choice, configChildSet, frankElement);
		}
	}

	private void addPluralConfigChild(XmlBuilder choice, ConfigChildSet configChildSet, FrankElement frankElement) {
		if(log.isTraceEnabled()) {
			log.trace("Adding ConfigChildSet [{}]", configChildSet::toString);
			ThreadContext.push(String.format("Owning element [%s], ConfigChildSet [%s]", frankElement.getSimpleName(), configChildSet.toString()));
		}
		configChildSet.getConfigChildren().forEach(version::checkForMissingDescription);
		switch(configChildSet.getConfigChildGroupKind()) {
		case OBJECT:
		// The warning that MIXED is not supported has been written during model initialization.
		case MIXED:
			addPluralObjectConfigChild(choice, configChildSet);
			break;
		case TEXT:
			addPluralTextConfigChild(choice, configChildSet);
			break;
		default:
			throw new IllegalArgumentException("Cannot happen, switch should cover all enum values");
		}
		if(log.isTraceEnabled()) {
			ThreadContext.pop();
			log.trace("Done adding ConfigChildSet with ElementRoleSet [{}]", configChildSet::toString);
		}
	}

	private void addPluralObjectConfigChild(XmlBuilder choice, ConfigChildSet configChildSet) {
		List<ElementRole> roles = configChildSet.getFilteredElementRoles(version.getChildSelector(), version.getChildRejector());
		if((roles.size() == 1) && isNoElementTypeNeeded(roles.get(0))) {
			log.trace("Config child set appears as element reference");
			addElementRoleAsElement(choice, roles.get(0));
		} else {
			log.trace("Config child set appears as group reference");
			requestElementGroupForConfigChildSet(configChildSet, roles);
			FrankDocXsdFactoryXmlUtils.addGroupRef(choice, elementGroupManager.getGroupName(roles));
		}
	}

	/**
	 * It does not make sense to set minOccurs="1" for mandatory config children.
	 * Plural config children are wrapped inside <xs:sequence minOccurs="0" maxOccurs="unbounded"><xs:choice>.
	 * Setting <xs:element minOccurs="1"...> has no effect.
	 * See also the comment in {@link #recursivelyDefineXsdElementUnchecked()}.
	 */
	private void addPluralTextConfigChild(XmlBuilder choice, ConfigChildSet configChildSet) {
		addElement(choice, Utils.toUpperCamelCase(configChildSet.getRoleName()), configChildSet.getElementTypeName(), "0", UNBOUNDED);
	}

	private void addAttributes(ElementBuildingStrategy elementBuildingStrategy, FrankElement frankElement) {
		Consumer<GroupCreator.Callback<FrankAttribute>> cumulativeGroupTrigger =
				ca -> frankElement.walkCumulativeAttributes(ca, version.getChildSelector(), version.getChildRejector());
		new GroupCreator<>(frankElement, version.getHasRelevantChildrenPredicate(FrankAttribute.class), cumulativeGroupTrigger, new GroupCreator.Callback<>() {
			private XmlBuilder cumulativeBuilder;
			private String cumulativeGroupName;

			@Override
			public void noChildren() {
				elementBuildingStrategy.onNoAttributes();
			}

			@Override
			public void addDeclaredGroupRef(FrankElement referee) {
				elementBuildingStrategy.addAttributeGroupRef(xsdDeclaredGroupNameForAttributes(referee));
			}

			@Override
			public void addCumulativeGroupRef(FrankElement referee) {
				elementBuildingStrategy.addAttributeGroupRef(xsdCumulativeGroupNameForAttributes(referee));
			}

			@Override
			public void addTopLevelDeclaredGroup() {
				XmlBuilder attributeGroup = commonAddAttributeGroup();
				log.trace("Adding attribute active");
				XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
				attributeReuseManager.addAttribute(attributeActive, attributeGroup);
				log.trace("Adding any attribute in another namespace");
				XmlBuilder anyOther = createAnyOtherNamespaceAttribute();
				attributeReuseManager.addAttribute(anyOther, attributeGroup);
			}

			@Override
			public void addDeclaredGroup() {
				commonAddAttributeGroup();
			}

			private XmlBuilder commonAddAttributeGroup() {
				String groupName = xsdDeclaredGroupNameForAttributes(frankElement);
				log.trace("Creating XSD group [{}]", groupName);
				XmlBuilder attributeGroup = createAttributeGroup(groupName);
				xsdComplexItems.add(attributeGroup);
				addAttributeList(attributeGroup, frankElement.getAttributes(version.getChildSelector()), groupName);
				log.trace("Done creating XSD group [{}] on behalf of FrankElement [{}]", () -> groupName, frankElement::getFullName);
				return attributeGroup;
			}

			@Override
			public void addCumulativeGroup() {
				cumulativeGroupName = xsdCumulativeGroupNameForAttributes(frankElement);
				log.trace("Start creating XSD group [{}]", cumulativeGroupName);
				cumulativeBuilder = createAttributeGroup(cumulativeGroupName);
				xsdComplexItems.add(cumulativeBuilder);
			}

			@Override
			public void handleSelectedChildren(List<FrankAttribute> children, FrankElement owner) {
				log.trace("Appending some of the attributes of FrankElement [{}] to XSD group [{}]", owner::getFullName, () -> cumulativeGroupName);
				addAttributeList(cumulativeBuilder, children, cumulativeGroupName);
			}

			@Override
			public void handleSelectedChildrenOfTopLevel(List<FrankAttribute> children, FrankElement owner) {
				handleSelectedChildren(children, owner);
				log.trace("Adding attribute active because [{}] has no ancestors with children", owner::getFullName);
				XmlBuilder attributeActive = AttributeTypeStrategy.createAttributeActive();
				attributeReuseManager.addAttribute(attributeActive, cumulativeBuilder);
				log.trace("Adding any attribute in another namespace");
				XmlBuilder anyAttribute = createAnyOtherNamespaceAttribute();
				attributeReuseManager.addAttribute(anyAttribute, cumulativeBuilder);
			}

			@Override
			public void handleChildrenOf(FrankElement elem) {
				String referencedGroupName = xsdDeclaredGroupNameForAttributes(elem);
				log.trace("Appending XSD group [{}] with reference to [{}]", cumulativeGroupName, referencedGroupName);
				XmlBuilder builder = FrankDocXsdFactoryXmlUtils.createAttributeGroupRef(referencedGroupName);
				attributeReuseManager.addAttribute(builder, cumulativeBuilder);
			}

			@Override
			public void handleCumulativeChildrenOf(FrankElement elem) {
				String referencedGroupName = xsdCumulativeGroupNameForAttributes(elem);
				log.trace("Appending XSD group [{}] with reference to [{}]", cumulativeGroupName, referencedGroupName);
				XmlBuilder builder = FrankDocXsdFactoryXmlUtils.createAttributeGroupRef(referencedGroupName);
				attributeReuseManager.addAttribute(builder, cumulativeBuilder);
			}
		}).run();
	}

	private void addAttributeList(XmlBuilder context, List<FrankAttribute> frankAttributes, String groupName) {
		frankAttributes.forEach(version::checkForMissingDescription);
		for(FrankAttribute frankAttribute: frankAttributes) {
			log.trace("Group [{}] has attribute [{}], will decide later whether to add it inline or to reuse it", () -> groupName, frankAttribute::getName);
			attributeReuseManager.addAttribute(frankAttribute, context, groupName);
		}
	}

	@Override
	public void addAttributeInline(FrankAttribute attribute, XmlBuilder group, String targetName) {
		log.trace("Attribute [{}] in FrankElement [{}] for group [{}] is inline", attribute::toString, attribute::getOwningElement, () -> targetName);
		XmlBuilder attributeBuilder = createAttribute(attribute);
		if(version.childIsMandatory(attribute)) {
			log.trace("It is mandatory, adding \"use=required\"");
			attributeBuilder.addAttribute("use", "required");
		}
		group.addSubElement(attributeBuilder);
	}

	@Override
	public void addReusableAttribute(FrankAttribute attribute) {
		log.trace("Attribute [{}] of FrankElement [{}] is reused, creating it for reference from elsewhere", attribute, attribute.getOwningElement());
		xsdReusedAttributes.add(createAttribute(attribute));
	}

	@Override
	public void addReusedAttributeReference(FrankAttribute attribute, XmlBuilder group, String targetName) {
		log.trace("Reference reused attribute [{}] of FrankElement [{}] for group [{}]", attribute::toString, attribute::getOwningElement, () -> targetName);
		XmlBuilder attributeBuilder = FrankDocXsdFactoryXmlUtils.createAttributeRef(attribute.getName());
		if(version.childIsMandatory(attribute)) {
			log.trace("It is mandatory, adding \"use=required\" with the reference, not the referee");
			attributeBuilder.addAttribute("use", "required");
		}
		group.addSubElement(attributeBuilder);
	}

	private XmlBuilder createAttribute(FrankAttribute frankAttribute) {
		XmlBuilder attribute = null;
		if(frankAttribute.getAttributeEnum() == null) {
			// The default value in the model is a *description* of the default value.
			// Therefore, it should be added to the description in the xs:attribute.
			// The "default" attribute of the xs:attribute should not be set.
			attribute = attributeTypeStrategy.createAttribute(frankAttribute.getName(), frankAttribute.getAttributeType());
			documentAttributeIfNeeded(frankAttribute, attribute);
		} else {
			log.trace("Attribute is restricted by enum [{}]", frankAttribute.getAttributeEnum().getFullName());
			attribute = createRestrictedAttribute(frankAttribute, a -> documentAttributeIfNeeded(frankAttribute, a));
		}
		return attribute;
	}

	private void documentAttributeIfNeeded(FrankAttribute frankAttribute, XmlBuilder attributeBuilder) {
		if(needsDocumentation(frankAttribute)) {
			log.trace("Attribute has documentation");
			addDocumentation(attributeBuilder, getDocumentationText(frankAttribute));
		}
	}

	private XmlBuilder createRestrictedAttribute(FrankAttribute attribute, Consumer<XmlBuilder> documenter) {
		XmlBuilder result = attributeTypeStrategy.createRestrictedAttribute(attribute, documenter);
		AttributeEnum attributeEnum = attribute.getAttributeEnum();
		if(! definedAttributeEnumInstances.contains(attributeEnum.getFullName())) {
			log.trace("Defining type for the values of enum [{}]", attributeEnum.getFullName());
			definedAttributeEnumInstances.add(attributeEnum.getFullName());
			xsdComplexItems.add(attributeTypeStrategy.createAttributeEnumType(attributeEnum));
		}
		return result;
	}

	private boolean needsDocumentation(ElementChild elementChild) {
		return (! StringUtils.isEmpty(elementChild.getDescription())) || (! StringUtils.isEmpty(elementChild.getDefaultValue()));
	}

	private String getDocumentationText(FrankAttribute attribute) {
		StringBuilder result = new StringBuilder();
		if(StringUtils.isNotEmpty(attribute.getDescription())) {
			result.append(attribute.getDescription());
		}

		if (attribute.isUnsafe()) {
			if (!result.isEmpty() && result.charAt(result.length() - 1) != '.') {
				result.append(".");
			}
			if(!result.isEmpty()) {
				result.append(" ");
			}
			result.append("Note: This attribute is unsafe and should not be used in a production environment.");
		}

		if (StringUtils.isNotEmpty(attribute.getDefaultValue())) {
			if(!result.isEmpty()) {
				result.append(" ");
			}
			result.append("Default: ");
			result.append(attribute.getDefaultValue());
		}
		return result.toString();
	}

	// We cannot simply use getSimpleName(). If multiple elements share
	// a simple name, then only one should be non-deprecated. The deprecated
	// elements sharing a simple name are omitted as members of an ElementRole.
	// This way, no duplicate elements are defined in the XSD. However, this
	// is not enough to avoid duplicate type names, because the inheritance parent
	// of an element may have the same simple name as the child. This issue is solved
	// by adding a sequence number.
	private String xsdElementType(FrankElement frankElement) {
		return frankElement.getTypeNameBase() + "Type";
	}

	private static String xsdDeclaredGroupNameForChildren(FrankElement element) {
		return element.getTypeNameBase() + "DeclaredChildGroup";
	}

	private static String xsdCumulativeGroupNameForChildren(FrankElement element) {
		return element.getTypeNameBase() + "CumulativeChildGroup";
	}

	private static String xsdPluralGroupNameForChildren(FrankElement element) {
		return element.getTypeNameBase() + "PluralConfigChildGroup";
	}

	private static String xsdDeclaredGroupNameForAttributes(FrankElement element) {
		return element.getTypeNameBase() + "DeclaredAttributeGroup";
	}

	private static String xsdCumulativeGroupNameForAttributes(FrankElement element) {
		return element.getTypeNameBase() + "CumulativeAttributeGroup";
	}

	private String getMinOccurs(ConfigChild child) {
		if(version.childIsMandatory(child)) {
			return "1";
		} else {
			return "0";
		}
	}

	private static String getMaxOccurs(ConfigChild child) {
		if(child.isAllowMultiple()) {
			return UNBOUNDED;
		} else {
			return "1";
		}
	}
}
