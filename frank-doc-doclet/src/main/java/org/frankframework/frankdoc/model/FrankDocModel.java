/*
Copyright 2020 - 2024 WeAreFrank!

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

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.feature.Default;
import org.frankframework.frankdoc.feature.Deprecated;
import org.frankframework.frankdoc.feature.Description;
import org.frankframework.frankdoc.feature.ExcludeFromTypeFeature;
import org.frankframework.frankdoc.feature.Mandatory;
import org.frankframework.frankdoc.feature.Optional;
import org.frankframework.frankdoc.feature.Protected;
import org.frankframework.frankdoc.feature.Reference;
import org.frankframework.frankdoc.feature.Reintroduce;
import org.frankframework.frankdoc.model.AncestorMethodBrowser.References;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static org.frankframework.frankdoc.model.ElementChild.ALL;

public class FrankDocModel {
	private static final Logger log = LogUtil.getLogger(FrankDocModel.class);
	private static final String ENUM = "Enum";
	private static final List<String> EXPECTED_HTML_TAGS = Arrays.asList("a", "b", "br", "code", "h1", "h2", "h3", "h4", "i", "li", "ol", "p", "pre", "strong", "table", "td", "th", "tr", "ul");
	private static final String UNSAFE_ANNOTATION_CLASSNAME = "org.frankframework.doc.Unsafe";

	private final FrankClassRepository classRepository;

	private final @Getter Map<String, List<ConfigChildSetterDescriptor>> configChildDescriptors = new HashMap<>();

	private final FrankDocGroupFactory groupFactory;
	private @Getter List<FrankDocGroup> groups;

	// We want to iterate FrankElement in the order they are created, to be able
	// to create the ElementRole objects in the right order.
	private final @Getter Map<String, FrankElement> allElements = new LinkedHashMap<>();

	// We have a LinkedHashMap because the sequence of the types is relevant. This
	// sequence determines the sort order of the elements of FrankDocGroup Other.
	private final @Getter Map<String, ElementType> allTypes = new LinkedHashMap<>();

	private @Getter List<FrankElement> elementsOutsideConfigChildren;

	private final @Getter Map<ElementRole.Key, ElementRole> allElementRoles = new HashMap<>();
	private final ElementRole.Factory elementRoleFactory = new ElementRole.Factory();
	private final Map<Set<ElementRole.Key>, ElementRoleSet> allElementRoleSets = new HashMap<>();
	private final AttributeEnumFactory attributeEnumFactory = new AttributeEnumFactory();
	private final @Getter String rootClassName;
	private final LabelValues labelValues = new LabelValues();

	FrankDocModel(FrankClassRepository classRepository, String rootClassName) {
		this.classRepository = classRepository;
		this.groupFactory = new FrankDocGroupFactory(classRepository);
		this.rootClassName = rootClassName;
	}

	public static FrankDocModel populate(final URL digesterRules, final String rootClassName, FrankClassRepository classRepository) {
		FrankDocModel result = new FrankDocModel(classRepository, rootClassName);
		try {
			log.trace("Populating FrankDocModel");
			result.createConfigChildDescriptorsFrom(digesterRules);
			result.findOrCreateRootFrankElement(rootClassName);
			result.buildDescendants();
			result.allElements.values().forEach(result::finishConfigChildrenFor);
			result.labelValues.finishInitialization();
			result.checkSuspiciousHtml();
			result.calculateTypeNameSeq();
			result.calculateInterfaceBased();
			result.calculateCommonInterfacesHierarchies();
			result.setHighestCommonInterface();
			result.setOverriddenFrom();
			result.createConfigChildSets();
			result.setElementNamesOfFrankElements(rootClassName);
			result.buildGroups();
		} catch(Exception e) {
			log.fatal("Could not populate FrankDocModel", e);
			return null;
		}
		log.trace("Done populating FrankDocModel");
		return result;
	}

	void createConfigChildDescriptorsFrom(final URL digesterRules) throws IOException, SAXException {
		log.trace("Creating config child descriptors from file [{}]", digesterRules::toString);
		InputSource digesterRulesInputSource = Utils.asInputSource(digesterRules);
		try {
			Utils.parseXml(digesterRulesInputSource, new Handler());
			log.trace("Successfully created config child descriptors");
		}
		catch(IOException e) {
			throw new IOException(String.format("An IOException occurred while parsing XML from [%s]", digesterRulesInputSource.getSystemId()), e);
		}
		catch(SAXException e) {
			throw new SAXException(String.format("A SAXException occurred while parsing XML from [%s]", digesterRulesInputSource.getSystemId()), e);
		}
	}

	private class Handler extends DigesterRulesHandler {
		@Override
		protected void handle(DigesterRule rule) throws SAXException {
			DigesterRulesPattern pattern = new DigesterRulesPattern(rule.getPattern());
			String registerTextMethod = rule.getRegisterTextMethod();
			if(StringUtils.isNotEmpty(rule.getRegisterMethod())) {
				if(StringUtils.isNotEmpty(registerTextMethod)) {
					log.warn("digester-rules.xml, role name {}: Have both registerMethod and registerTextMethod, ignoring the latter", pattern.getRoleName());
				}
				addTypeObject(rule.getRegisterMethod(), pattern);
			} else {
				if(StringUtils.isNotEmpty(registerTextMethod)) {
					if(registerTextMethod.startsWith("set")) {
						log.error("digester-rules.xml: Ignoring registerTextMethod {} because it starts with \"set\" to avoid confusion with attributes", registerTextMethod);
					} else {
						addTypeText(registerTextMethod, pattern);
					}
				} else {
					// roleName is not final, so a lambda wont work in the trace statement.
					// We use isTraceEnabled() instead.
					if(log.isTraceEnabled()) {
						log.trace("digester-rules.xml, ignoring role name {} because there is no registerMethod and no registerTextMethod attribute", pattern.getRoleName());
					}
				}
			}
		}

		private void addTypeObject(String registerMethod, DigesterRulesPattern pattern)	throws SAXException {
			log.trace("Have ConfigChildSetterDescriptor for ObjectConfigChild: roleName = {}, registerMethod = {}", pattern::getRoleName, () -> registerMethod);
			ConfigChildSetterDescriptor descriptor = new ConfigChildSetterDescriptor.ForObject(registerMethod, pattern);
			register(descriptor, pattern);
		}

		private void addTypeText(String registerMethod, DigesterRulesPattern pattern) throws SAXException {
			log.trace("Have ConfigChildSetterDescriptor for TextConfigChild: roleName = {}, registerMethod = {}", pattern::getRoleName, () -> registerMethod);
			ConfigChildSetterDescriptor descriptor = new ConfigChildSetterDescriptor.ForText(registerMethod, pattern);
			register(descriptor, pattern);
		}

		private void register(ConfigChildSetterDescriptor descriptor, DigesterRulesPattern pattern) {
			if(! configChildDescriptors.containsKey(descriptor.getMethodName())) {
				configChildDescriptors.put(descriptor.getMethodName(), new ArrayList<>());
			}
			configChildDescriptors.get(descriptor.getMethodName()).add(descriptor);
			DigesterRulesPattern.Matcher matcher = pattern.getMatcher();
			if(matcher != null) {
				log.trace("Role name [{}] has Matcher [{}]", descriptor::getRoleName, matcher::toString);
			}
		}
	}

	public boolean hasType(String typeName) {
		return allTypes.containsKey(typeName);
	}

	void buildDescendants() throws Exception {
		log.trace("Enter");
		boolean addedDescendants;
		int pass = 1;
		do {
			addedDescendants = false;
			if(log.isTraceEnabled()) {
				log.trace("Pass [{}]", pass++);
			}
			// We update allElements during the iteration, so we have to copy
			for(FrankElement existing: new ArrayList<>(allElements.values())) {
				if(createConfigChildren(existing)) {
					addedDescendants = true;
				}
			}
		} while(addedDescendants);
		log.trace("Leave");
	}

	FrankElement findOrCreateRootFrankElement(String fullClassName) throws FrankDocException {
		return findOrCreateFrankElement(fullClassName, new FrankElementCreationStrategyRoot());
	}

	FrankElement findOrCreateFrankElement(String fullClassName) throws FrankDocException {
		return findOrCreateFrankElement(fullClassName, new FrankElementCreationStrategyNonRoot());
	}

	private abstract class FrankElementCreationStrategy {
		abstract FrankElement createFromClass(FrankClass clazz);
		abstract FrankElement recursiveFindOrCreate(String fullClassName) throws FrankDocException;
	}

	private class FrankElementCreationStrategyRoot extends FrankElementCreationStrategy{
		@Override
		FrankElement createFromClass(FrankClass clazz) {
			return new RootFrankElement(clazz, classRepository, groupFactory, labelValues);
		}

		@Override
		FrankElement recursiveFindOrCreate(String fullClassName) throws FrankDocException {
			return findOrCreateRootFrankElement(fullClassName);
		}
	}

	private class FrankElementCreationStrategyNonRoot extends FrankElementCreationStrategy {
		@Override
		FrankElement createFromClass(FrankClass clazz) {
			return new FrankElement(clazz, classRepository, groupFactory, labelValues);
		}

		@Override
		FrankElement recursiveFindOrCreate(String fullClassName) throws FrankDocException {
			return findOrCreateFrankElement(fullClassName);
		}
	}

	FrankElement findOrCreateFrankElement(String fullClassName, FrankElementCreationStrategy creator) throws FrankDocException {
		log.trace("FrankElement requested for class name [{}]", () -> fullClassName);
		FrankClass clazz = classRepository.findClass(fullClassName);
		if(allElements.containsKey(clazz.getName())) {
			log.trace("Already present");
			return allElements.get(clazz.getName());
		}
		log.trace("Creating FrankElement for class name [{}]", clazz::getName);
		FrankElement current = creator.createFromClass(clazz);
		log.trace("Created [{}] [{}]", current.getClass().getSimpleName(), current.getFullName());
		allElements.put(clazz.getName(), current);
		FrankClass superClass = clazz.getSuperclass();
		FrankElement parent = superClass == null ? null : creator.recursiveFindOrCreate(superClass.getName());
		current.setParent(parent);
		current.setAttributes(createAttributes(clazz, current, classRepository));
		log.trace("Done creating FrankElement for class name [{}]", clazz::getName);
		return current;
	}

	public FrankElement findFrankElement(String fullName) {
		return allElements.get(fullName);
	}

	List<FrankAttribute> createAttributes(FrankClass clazz, FrankElement attributeOwner, FrankClassRepository classRepository) throws FrankDocException {
		log.trace("Creating attributes for FrankElement [{}]", attributeOwner::getFullName);
		checkForAttributeSetterOverloads(clazz);
		FrankMethod[] methods = clazz.getDeclaredMethodsAndMultiplyInheritedPlaceholders();
		log.trace("Considering the following methods as possible attribute setters: [{}]", () -> Arrays.asList(methods));
		Map<String, FrankMethod> enumGettersByAttributeName = getEnumGettersByAttributeName(clazz);
		LinkedHashMap<String, FrankMethod> setterAttributes = getAttributeToMethodMap(methods, "set");
		Map<String, FrankMethod> getterAttributes = getGetterAndIsserAttributes(methods, attributeOwner);
		List<FrankAttribute> result = new ArrayList<>();
		for(Entry<String, FrankMethod> entry: setterAttributes.entrySet()) {
			String attributeName = entry.getKey();
			log.trace("Attribute [{}]", () -> attributeName);
			FrankMethod method = entry.getValue();
			if(log.isTraceEnabled() && method.isMultiplyInheritedPlaceholder()) {
				log.trace("Attribute [{}] does not come from a declared method, but may be relevant because of multiple inheritance", attributeName);
			}
			if(getterAttributes.containsKey(attributeName)) {
				checkForTypeConflict(method, getterAttributes.get(attributeName), attributeOwner);
			}
			boolean unsafe = method.getAnnotation(UNSAFE_ANNOTATION_CLASSNAME) != null;
			FrankAttribute attribute = new FrankAttribute(attributeName, unsafe, attributeOwner);
			if(method.getParameterTypes()[0].isEnum()) {
				log.trace("Attribute [{}] has setter that takes enum: [{}]", attribute::getName, () -> method.getParameterTypes()[0].toString());
				attribute.setAttributeType(AttributeType.STRING);
				FrankClass enumClass = (FrankClass) method.getParameterTypes()[0];
				attribute.setAttributeEnum(findOrCreateAttributeEnum(enumClass));
			} else {
				attribute.setAttributeType(AttributeType.fromJavaType(method.getParameterTypes()[0].getName()));
				log.trace("Attribute [{}] has type [{}]", () -> attributeName, () -> attribute.getAttributeType().toString());
				if(enumGettersByAttributeName.containsKey(attributeName)) {
					log.trace("Attribute [{}] has enum values", () -> attributeName);
					attribute.setAttributeEnum(findOrCreateAttributeEnum((FrankClass) enumGettersByAttributeName.get(attributeName).getReturnType()));
				}
			}
			setAttributeFeatures(attribute, method, classRepository);
			result.add(attribute);
			log.trace("Attribute [{}] done", () -> attributeName);
		}
		log.trace("Done creating attributes for {}", attributeOwner.getFullName());
		return result;
	}

	private void setAttributeFeatures(FrankAttribute attribute, FrankMethod method, FrankClassRepository classRepository) {
		Reference featureReference = new Reference(classRepository);
		attribute.setDocumented(
				(Description.getInstance().valueOf(method) != null)
				|| (featureReference.valueOf(method) != null)
				|| (Default.getInstance().valueOf(method) != null)
				|| (Mandatory.getInstance().valueOf(method) != null)
				|| Optional.getInstance().isSetOn(method));
		attribute.setDeprecated(Deprecated.getInstance().isSetOn(method));
		attribute.setReintroduced(Reintroduce.getInstance().isSetOn(method));
		log.trace("Attribute: deprecated = [{}], documented = [{}], reintroduced = [{}]",
			attribute::isDeprecated, attribute::isDocumented, attribute::isReintroduced);
		CreationContext context = new CreationContext();
		(new AncestorMethodBrowser(classRepository, AncestorMethodBrowser.References.WITH_REFERENCES)).browse(method,
				ancestorMethod -> handleAttributeSetterAncestor(attribute, ancestorMethod, context));
		attribute.setMandatoryStatus(MandatoryStatus.of(context.mandatoryValue, context.optionalValue));
		log.trace("Mandatory status [{}]", () -> attribute.getMandatoryStatus().toString());
		attribute.handleDefaultExplicitNull(method.getParameterTypes()[0]);
		log.trace("Default [{}]", attribute::getDefaultValue);
	}

	private class CreationContext {
		Mandatory.Value mandatoryValue;
		boolean optionalValue = false;
	}

	private void handleAttributeSetterAncestor(FrankAttribute attribute, FrankMethod ancestorMethod, CreationContext context) {
		updateOptionalMandatoryContextFromAncestorMethod(ancestorMethod, context);
		if(attribute.getDescription() == null) {
			attribute.setDescription(Description.getInstance().valueOf(ancestorMethod));
			if(attribute.getDescription() != null) {
				log.trace("Description comes from (ancestor) method [{}], attribute: {}", ancestorMethod::toString, attribute::toString);
			}
		}
		if(attribute.getDefaultValue() == null) {
			// We do not type-check the default value. The default value is actually a description of the default.
			// It may not be the literal default value.
			attribute.setDefaultValue(Default.getInstance().valueOf(ancestorMethod));
			if(attribute.getDefaultValue() != null) {
				log.trace("Default value comes from (ancestor) method [{}], attribute: {}", ancestorMethod::toString, attribute::toString);
			}
		}
		if(Protected.getInstance().isSetOn(ancestorMethod)) {
			attribute.setExcluded(true);
			if(attribute.isExcluded()) {
				log.trace("Attribute is excluded because of method [{}]", ancestorMethod::toString);
			}
		}
	}

	private void updateOptionalMandatoryContextFromAncestorMethod(FrankMethod ancestorMethod, CreationContext context) {
		if(Optional.getInstance().isSetOn(ancestorMethod)) {
			context.optionalValue = true;
			log.trace("(Ancestor) method [{}] sets Optional", ancestorMethod::toString);
		}
		if(context.mandatoryValue == null) {
			context.mandatoryValue = Mandatory.getInstance().valueOf(ancestorMethod);
			if(context.mandatoryValue != null) {
				log.trace("(Ancestor) method [{}] sets Mandatory to value [{}]", ancestorMethod::toString, () -> context.mandatoryValue.toString());
			}
		}
	}

	private void checkForAttributeSetterOverloads(FrankClass clazz) {
		List<FrankMethod> cumulativeAttributeSetters = getAttributeMethodList(clazz.getDeclaredAndInheritedMethods(), "set");
		Map<String, List<FrankMethod>> attributeSettersByAttributeName = cumulativeAttributeSetters.stream()
				.collect(Collectors.groupingBy(m -> attributeOf(m.getName(), "set")));
		for(String attributeName: attributeSettersByAttributeName.keySet()) {
			List<FrankMethod> attributeSetterCandidates = attributeSettersByAttributeName.get(attributeName);
			List<String> candidateTypes = attributeSetterCandidates.stream()
					.map(m -> m.getParameterTypes()[0].getName())
					.distinct()
					.sorted()
					.collect(Collectors.toList());
			if(candidateTypes.size() >= 2) {
				log.error("Class [{}] has overloaded declared or inherited attribute setters. Type of attribute [{}] can be any of [{}]",
						clazz.getName(), attributeName, candidateTypes.stream().collect(Collectors.joining(", ")));
			}
		}
	}

	private Map<String, FrankMethod> getGetterAndIsserAttributes(FrankMethod[] methods, FrankElement attributeOwner) {
		Map<String, FrankMethod> getterAttributes = getAttributeToMethodMap(methods, "get");
		Map<String, FrankMethod> isserAttributes = getAttributeToMethodMap(methods, "is");
		for(String isserAttributeName : isserAttributes.keySet()) {
			if(getterAttributes.containsKey(isserAttributeName)) {
				log.warn("For FrankElement [{}], attribute [{}] has both a getX and an isX method", attributeOwner::getSimpleName, () -> isserAttributeName);
			} else {
				getterAttributes.put(isserAttributeName, isserAttributes.get(isserAttributeName));
			}
		}
		return getterAttributes;
	}

	/**
     * The original order of the methods is preserved, which you get when you iterate
     * over the entrySet() of the returned Map.
	 */
	static LinkedHashMap<String, FrankMethod> getAttributeToMethodMap(FrankMethod[] methods, String prefix) {
		LinkedHashMap<String, FrankMethod> result = new LinkedHashMap<>();
		for(FrankMethod method: getAttributeMethodList(methods, prefix)) {
			String attributeName = attributeOf(method.getName(), prefix);
			result.put(attributeName, method);
		}
		return result;
	}

	private static List<FrankMethod> getAttributeMethodList(FrankMethod[] methods, String prefix) {
		List<FrankMethod> methodList = Arrays.asList(methods);
		return methodList.stream()
				.filter(FrankMethod::isPublic)
				.filter(Utils::isAttributeGetterOrSetter)
				.filter(m -> m.getName().startsWith(prefix) && (m.getName().length() > prefix.length()))
				.collect(Collectors.toList());
	}

	private static String attributeOf(String methodName, String prefix) {
		String strippedName = methodName.substring(prefix.length());
		return strippedName.substring(0, 1).toLowerCase() + strippedName.substring(1);
	}

	static Map<String, FrankMethod> getEnumGettersByAttributeName(FrankClass clazz) {
		FrankMethod[] rawMethods = clazz.getDeclaredAndInheritedMethods();
		List<FrankMethod> methods = Arrays.stream(rawMethods)
				.filter(m -> m.getName().endsWith(ENUM))
				.filter(m -> m.getReturnType().isEnum())
				.filter(m -> m.getParameterCount() == 0)
				// This filter cannot be covered with tests, because getMethods
				// does not include a non-public method in the test classes.
				.filter(FrankMethod::isPublic)
				.collect(Collectors.toList());
		Map<String, FrankMethod> result = new HashMap<>();
		for(FrankMethod m: methods) {
			result.put(enumAttributeOf(m), m);
		}
		return result;
	}

	private static String enumAttributeOf(FrankMethod method) {
		String nameWithoutEnum = method.getName().substring(0, method.getName().length() - ENUM.length());
		return attributeOf(nameWithoutEnum, "get");
	}

	private void checkForTypeConflict(FrankMethod setter, FrankMethod getter, FrankElement attributeOwner) {
		log.trace("Checking for type conflict with getter or isser [{}]", getter::getName);
		String setterType = setter.getParameterTypes()[0].getName();
		String getterType = getter.getReturnType().getName();
		if(getter.getName().startsWith("get")) {
			// For issers we require an exact match of the type name. For getters,
			// the setter and the getter may mix boxed and unboxed types.
			// This allows the framework code to distinguish null values
			// (=not configured) from default values.
			setterType = Utils.promoteIfPrimitive(setterType);
			getterType = Utils.promoteIfPrimitive(getterType);
		}
		if(! getterType.equals(setterType)) {
			// Cannot work with lambdas because setterType is not final.
			if(log.isWarnEnabled()) {
				log.error("In Frank element [{}]: setter [{}] has type [{}] while the getter has type [{}]",
						attributeOwner.getSimpleName(), setter.getName(), setterType, getterType);
			}
		}
	}

	private boolean createConfigChildren(FrankElement parent) throws FrankDocException {
		log.trace("Creating config children of FrankElement [{}]", parent::getFullName);
		boolean createdNewConfigChildren = false;
		List<FrankMethod> frankMethods = parent.getUnusedConfigChildSetterCandidates().keySet().stream()
				.filter(m -> configChildDescriptors.containsKey(m.getName()))
				.collect(Collectors.toList());
		for(FrankMethod frankMethod: frankMethods) {
			log.trace("Have config child setter candidate [{}]", frankMethod::getName);
			List<ConfigChildSetterDescriptor> descriptorCandidates = configChildDescriptors.get(frankMethod.getName());
			ConfigChildSetterDescriptor configChildDescriptor = ConfigChildSetterDescriptor.find(parent, descriptorCandidates);
			if(configChildDescriptor == null) {
				log.trace("Not a config child, next");
				continue;
			}
			log.trace("Have ConfigChildSetterDescriptor [{}]", configChildDescriptor::toString);
			ConfigChild configChild = configChildDescriptor.createConfigChild(parent, frankMethod);
			configChild.setAllowMultiple(configChildDescriptor.isAllowMultiple());
			log.trace("Allow multiple = [{}]", () -> Boolean.toString(configChild.isAllowMultiple()));
			CreationContext context = new CreationContext();
			(new AncestorMethodBrowser(classRepository, References.WITHOUT_REFERENCES)).browse(frankMethod,
					ancestorMethod -> handleConfigChildSetterAncestor(configChild, ancestorMethod, context));
			configChild.setMandatoryStatus(MandatoryStatus.of(context.mandatoryValue, context.optionalValue));
			log.trace("Mandatory status [{}]", configChild.getMandatoryStatus());
			if(configChildDescriptor.isForObject() && frankMethod.getParameterTypes()[0] instanceof FrankClass) {
				log.trace("For FrankElement [{}] method [{}], going to search element role", parent::getFullName, frankMethod::getName);
				FrankClass elementTypeClass = (FrankClass) frankMethod.getParameterTypes()[0];
				((ObjectConfigChild) configChild).setElementRole(findOrCreateElementRole(elementTypeClass, configChildDescriptor.getRoleName()));
				((ObjectConfigChild) configChild).getElementRole().getElementType().getMembers().forEach(f -> f.addConfigParent(configChild));
				log.trace("For FrankElement [{}] method [{}], have the element role", parent::getFullName, frankMethod::getName);
			}
			configChild.setOrder(parent.getUnusedConfigChildSetterCandidates().get(frankMethod));
			createdNewConfigChildren = true;
			if (configChild.getRoleName() != null)
				parent.getConfigChildrenUnderConstruction().add(configChild);
			parent.getUnusedConfigChildSetterCandidates().remove(frankMethod);
			if(log.isTraceEnabled() && frankMethod.isMultiplyInheritedPlaceholder()) {
				log.trace("Config child [{}] is not based on a declared method, but was added because of possible multiple inheritance", configChild);
			}
			log.trace("Done creating config child [{}], the order is [{}]", configChild::toString, configChild::getOrder);
		}
		log.trace("Done creating config children of FrankElement [{}]", parent::getFullName);
		return createdNewConfigChildren;
	}

	void handleConfigChildSetterAncestor(ConfigChild configChild, FrankMethod ancestorMethod, CreationContext context) {
		updateOptionalMandatoryContextFromAncestorMethod(ancestorMethod, context);
		if(! configChild.isExcluded()) {
			configChild.setExcluded(Protected.getInstance().isSetOn(ancestorMethod));
			if(configChild.isExcluded()) {
				log.trace("Set excluded because of method [{}]", ancestorMethod::toString);
			}
		}
		if(StringUtils.isEmpty(configChild.getDescription())) {
			configChild.setDescription(Description.getInstance().valueOf(ancestorMethod));
			if(configChild.getDescription() != null) {
				log.trace("Set description from method [{}]", ancestorMethod::toString);
			}
		}
	}

	void finishConfigChildrenFor(FrankElement parent) {
		// We are only removing config children here to ensure that there is only one ConfigChild per role name.
		// We will not remove config children that are the only one with their role name. Therefore, we will not
		// lose paths that were used to match digester rules.
		log.trace("Removing duplicate config children of FrankElement [{}]", parent::getFullName);
		List<ConfigChild> result = ConfigChild.removeDuplicates(parent.getConfigChildrenUnderConstruction());
		// We have to sort the config children because they are not created in the same order as the order of the config child setters.
		Collections.sort(result, Comparator.comparingInt(ConfigChild::getOrder));
		parent.setConfigChildren(result);
		log.trace("The config children are (sequence follows sequence of Java methods):");
		if(log.isTraceEnabled()) {
			result.forEach(c -> log.trace("{}", c));
		}
	}

	ElementRole findOrCreateElementRole(FrankClass elementTypeClass, String roleName) throws FrankDocException {
		log.trace("ElementRole requested for elementTypeClass [{}] and roleName [{}]. Going to get the ElementType", elementTypeClass::getName, () -> roleName);
		ElementType elementType = findOrCreateElementType(elementTypeClass);
		ElementRole.Key key = new ElementRole.Key(elementTypeClass.getName(), roleName);
		if(allElementRoles.containsKey(key)) {
			log.trace("ElementRole already present");
			return allElementRoles.get(key);
		} else {
			ElementRole result = elementRoleFactory.create(elementType, roleName);
			allElementRoles.put(key, result);
			log.trace("For ElementType [{}] and roleName [{}], created ElementRole [{}]", elementType::getFullName, () -> roleName, () -> result.createXsdElementName(""));
			return result;
		}
	}

	public ElementRole findElementRole(ElementRole.Key key) {
		return allElementRoles.get(key);
	}

	public ElementRole findElementRole(ObjectConfigChild configChild) {
		return findElementRole(new ElementRole.Key(configChild));
	}

	ElementRole findElementRole(String fullElementTypeName, String roleName) {
		return allElementRoles.get(new ElementRole.Key(fullElementTypeName, roleName));
	}

	ElementType findOrCreateElementType(FrankClass clazz) throws FrankDocException {
		log.trace("Requested ElementType for class [{}]", clazz::getName);
		if(allTypes.containsKey(clazz.getName())) {
			log.trace("Already present");
			return allTypes.get(clazz.getName());
		}
		FrankDocGroup group = groupFactory.getGroup(clazz);
		log.trace("Creating ElementType [{}] with group [{}]", clazz::getName, group::getName);
		final ElementType result = new ElementType(clazz, group, classRepository);
		// If a containing FrankElement contains the type being created, we do not
		// want recursion.
		allTypes.put(result.getFullName(), result);
		if(result.isFromJavaInterface()) {
			log.trace("Class [{}] is a Java interface, going to create all member FrankElement", clazz::getName);
			List<FrankClass> memberClasses = clazz.getInterfaceImplementations().stream()
					.filter(m -> ! excludedByExcludeFromTypeFeature(m, clazz)).collect(Collectors.toList());
			// We sort here to make the order deterministic.
			Collections.sort(memberClasses, Comparator.comparing(FrankClass::getName));
			for(FrankClass memberClass: memberClasses) {
				addElementIfNotProtected(memberClass, result);
			}
		} else {
			// We do not create a config child if the argument clazz is not an interface and if it has feature PROTECTED.
			// Therefore, we can safely proceed here.
			log.trace("Class [{}] is not a Java interface, creating its FrankElement", clazz::getName);
			if(excludedByExcludeFromTypeFeature(clazz, clazz)) {
				log.error("Feature ExcludeFromTypeFeature excludes the only possible member of class [{}]", clazz.getName());
			}
			FrankElement member = findOrCreateFrankElement(clazz.getName());
			result.addMember(member);
		}
		log.trace("Done creating ElementType for class [{}]", clazz::getName);
		return result;
	}

	private boolean excludedByExcludeFromTypeFeature(FrankClass member, FrankClass typeClass) {
		Set<FrankClass> explicitExcludes = ExcludeFromTypeFeature.getInstance(classRepository).excludedFrom(member);
		if((explicitExcludes != null) && explicitExcludes.contains(typeClass)) {
			return true;
		} else if(member.getSuperclass() != null) {
			return excludedByExcludeFromTypeFeature(member.getSuperclass(), typeClass);
		} else {
			return false;
		}
	}

	private void addElementIfNotProtected(FrankClass memberClass, final ElementType result) throws FrankDocException {
		if(FrankElement.classIsProtected(memberClass)) {
			log.info("Class [{}] has feature PROTECTED, not added to type [{}]", memberClass.getName(), result.getFullName());
		} else if(! memberClass.isPublic()) {
			log.info("Class [{}] is not public, not added to type [{}]", memberClass.getName(), result.getFullName());
		} else {
			FrankElement frankElement = findOrCreateFrankElement(memberClass.getName());
			result.addMember(frankElement);
		}
	}

	public ElementType findElementType(String fullName) {
		return allTypes.get(fullName);
	}

	void calculateCommonInterfacesHierarchies() {
		log.trace("Going to calculate highest common interface for every ElementType");
		allTypes.values().forEach(et -> et.calculateCommonInterfaceHierarchy(this));
		log.trace("Done calculating highest common interface for every ElementType");
	}

	void setOverriddenFrom() {
		log.trace("Going to set property overriddenFrom for all config children and all attributes of all FrankElement");
		Set<String> remainingElements = allElements.values().stream().map(FrankElement::getFullName).collect(Collectors.toSet());
		while(! remainingElements.isEmpty()) {
			FrankElement current = allElements.get(remainingElements.iterator().next());
			while((current.getParent() != null) && (remainingElements.contains(current.getParent().getFullName()))) {
				current = current.getParent();
			}
			// Cannot eliminate isTraceEnabled here. Then the operation on variable current would need a lambda,
			// but that is not possible because variable current is not final.
			if(log.isTraceEnabled()) {
				log.trace("Setting property overriddenFrom for all config children and all attributes of FrankElement [{}]", current.getFullName());
			}
			current.getConfigChildren(ALL).forEach(ElementChild::calculateOverriddenFrom);
			current.getAttributes(ALL).forEach(ElementChild::calculateOverriddenFrom);
			current.getStatistics().finish();
			if(log.isTraceEnabled()) {
				log.trace("Done setting property overriddenFrom for FrankElement [{}]", current.getFullName());
			}
			remainingElements.remove(current.getFullName());
		}
		log.trace("Done setting property overriddenFrom");
	}

	void setElementNamesOfFrankElements(String rootClassName) {
		FrankElement root = allElements.get(rootClassName);
		root.addXmlElementName(root.getSimpleName());
		for(ElementRole role: allElementRoles.values()) {
			role.getMembers().forEach(frankElement -> frankElement.addXmlElementName(frankElement.getXsdElementName(role)));
		}
	}

	void setHighestCommonInterface() {
		log.trace("Doing FrankDocModel.setHighestCommonInterface");
		for(ElementRole role: allElementRoles.values()) {
			String roleName = role.getRoleName();
			ElementType et = role.getElementType().getHighestCommonInterface();
			ElementRole result = findElementRole(new ElementRole.Key(et.getFullName(), roleName));
			if(result == null) {
				log.trace("Promoting ElementRole [{}] results in ElementType [{}] and role name {}], but there is no corresponding ElementRole",
					role::toString, et::getFullName, () -> roleName);
				role.setHighestCommonInterface(role);
			} else {
				role.setHighestCommonInterface(result);
				log.trace("Role [{}] has highest common interface [{}]", role::toString, result::toString);
			}
		}
		log.trace("Done FrankDocModel.setHighestCommonInterface");
	}

	void createConfigChildSets() {
		log.trace("Doing FrankDocModel.createConfigChildSets");
		allElementRoles.values().forEach(ElementRole::initConflicts);
		List<FrankElement> sortedFrankElements = new ArrayList<>(allElements.values());
		Collections.sort(sortedFrankElements);
		sortedFrankElements.forEach(this::createConfigChildSets);
		List<ElementRole> sortedElementRoles = new ArrayList<>(allElementRoles.values());
		Collections.sort(sortedElementRoles);
		sortedElementRoles.stream()
			.filter(role -> role.getElementType().isFromJavaInterface())
			.forEach(role -> recursivelyCreateElementRoleSets(List.of(role), 1));
		allElementRoleSets.values().forEach(ElementRoleSet::initConflicts);
		log.trace("Done FrankDocModel.createConfigChildSets");
	}

	private void createConfigChildSets(FrankElement frankElement) {
		log.trace("Handling FrankElement [{}]", frankElement::getFullName);
		Map<String, List<ConfigChild>> cumChildrenByRoleName = frankElement.getCumulativeConfigChildren(ElementChild.ALL_NOT_EXCLUDED, ElementChild.EXCLUDED).stream()
				.collect(Collectors.groupingBy(ConfigChild::getRoleName));
		for(String roleName: cumChildrenByRoleName.keySet()) {
			List<ConfigChild> configChildren = cumChildrenByRoleName.get(roleName);
			if(configChildren.stream().map(ConfigChild::getOwningElement).anyMatch(childOwner -> (childOwner == frankElement))) {
				log.trace("Found ConfigChildSet for role name [{}]", roleName);
				ConfigChildSet configChildSet = new ConfigChildSet(configChildren);
				frankElement.addConfigChildSet(configChildSet);
				createElementRoleSetIfApplicable(configChildSet);
			}
		}
		log.trace("Done handling FrankElement [{}]", frankElement::getFullName);
	}

	private void createElementRoleSetIfApplicable(ConfigChildSet configChildSet) {
		switch(configChildSet.getConfigChildGroupKind()) {
		case TEXT:
			log.trace("[{}] holds only TextConfigChild. No ElementRoleSet needed", configChildSet::toString);
			break;
		case MIXED:
			log.error("[{}] combines ObjectConfigChild and TextConfigChild, which is not supported", configChildSet::toString);
			break;
		case OBJECT:
			createElementRoleSet(configChildSet);
			break;
		default:
			throw new IllegalArgumentException("Cannot happen, switch should cover all enum values");
		}
	}

	void createElementRoleSet(ConfigChildSet configChildSet) {
		Set<ElementRole> roles = configChildSet.getElementRoleStream()
				.collect(Collectors.toSet());
		Set<ElementRole.Key> key = roles.stream()
				.map(ElementRole::getKey)
				.collect(Collectors.toSet());
		if(! allElementRoleSets.containsKey(key)) {
			log.trace("New ElementRoleSet for roles [{}]", () -> ElementRole.describeCollection(roles));
			allElementRoleSets.put(key, new ElementRoleSet(roles));
		}
		ElementRoleSet elementRoleSet = allElementRoleSets.get(key);
		log.trace("[{}] has ElementRoleSet [{}]", configChildSet::toString, elementRoleSet::toString);
	}

	/**
	 * Create {@link org.frankframework.frankdoc.model.ElementRoleSet}, taking
	 * care of generic element option recursion as explained in
	 * {@link org.frankframework.frankdoc.model}.
	 */
	private void recursivelyCreateElementRoleSets(List<ElementRole> roleGroup, int recursionDepth) {
		log.trace("Enter with roles [{}] and recursion depth [{}]", () -> ElementRole.describeCollection(roleGroup), () -> recursionDepth);
		List<FrankElement> rawMembers = roleGroup.stream()
				.flatMap(role -> role.getRawMembers().stream())
				.distinct()
				.collect(Collectors.toList());
		Map<String, List<ConfigChild>> configChildrenByRoleName = rawMembers.stream()
				.flatMap(element -> element.getConfigChildren(ElementChild.ALL_NOT_EXCLUDED).stream())
				.collect(Collectors.groupingBy(ConfigChild::getRoleName));
		List<String> names = new ArrayList<>(configChildrenByRoleName.keySet());
		Collections.sort(names);
		for(String name: names) {
			List<ConfigChild> configChildren = configChildrenByRoleName.get(name);
			handleMemberChildrenWithCommonRoleName(configChildren, recursionDepth);
		}
		log.trace("Leave for roles [{}] and recursion depth [{}]", () -> ElementRole.describeCollection(roleGroup), () -> recursionDepth);
	}

	void handleMemberChildrenWithCommonRoleName(List<ConfigChild> configChildren, int recursionDepth) {
		log.trace("Considering config children [{}]", () -> ConfigChild.toString(configChildren));
		switch(ConfigChildGroupKind.groupKind(configChildren)) {
		case TEXT:
			log.trace("No ElementRoleSet needed for combination of TextConfigChild [{}]", () -> ConfigChild.toString(configChildren));
			break;
		case MIXED:
			log.error("Browsing member children produced a combination of ObjectConfigChild and TextConfigChild [{}], which is not supported", ConfigChild.toString(configChildren));
			break;
		case OBJECT:
			findOrCreateElementRoleSetForMemberChildren(configChildren, recursionDepth);
			break;
		default:
			throw new IllegalArgumentException("Should not happen, because switch statement should cover all enum values");
		}
	}

	void findOrCreateElementRoleSetForMemberChildren(List<ConfigChild> configChildren, int recursionDepth) {
		Set<ElementRole> roles = ConfigChild.getElementRoleStream(configChildren).collect(Collectors.toSet());
		Set<ElementRole.Key> key = roles.stream().map(ElementRole::getKey).collect(Collectors.toSet());
		if(! allElementRoleSets.containsKey(key)) {
			allElementRoleSets.put(key, new ElementRoleSet(roles));
			log.trace("Added new ElementRoleSet [{}]", () -> allElementRoleSets.get(key).toString());
			List<ElementRole> recursionParents = new ArrayList<>(roles);
			recursionParents = new ArrayList<>(recursionParents);
			Collections.sort(recursionParents);
			recursivelyCreateElementRoleSets(recursionParents, recursionDepth + 1);
		}
	}

	AttributeEnum findOrCreateAttributeEnum(FrankClass clazz) {
		return attributeEnumFactory.findOrCreateAttributeEnum(clazz);
    }

	public AttributeEnum findAttributeEnum(String enumTypeFullName) {
		return attributeEnumFactory.findAttributeEnum(enumTypeFullName);
	}

	public List<AttributeEnum> getAllAttributeEnumInstances() {
		return attributeEnumFactory.getAll();
	}

	public void checkSuspiciousHtml() {
		Set<String> allSuspiciousHtmlTagsFound = new HashSet<>();
		for(FrankElement frankElement: allElements.values()) {
			checkDescription(frankElement.getDescription(), "FrankElement", frankElement.getFullName(), allSuspiciousHtmlTagsFound);
			frankElement.getConfigChildren(ElementChild.ALL).stream()
				.filter(c -> c.getDescription() != null)
				.forEach(c -> checkDescription(c.getDescription(), "ConfigChild", c.toString(), allSuspiciousHtmlTagsFound));
			frankElement.getAttributes(ElementChild.ALL).stream()
				.filter(a -> a.getDescription() != null)
				.forEach(a -> checkDescription(a.getDescription(), "Attribute", a.toString(), allSuspiciousHtmlTagsFound));
		}
		if(! allSuspiciousHtmlTagsFound.isEmpty()) {
			log.warn("Searching over the descriptions of elements, config children and attributes, the following suspicious HTML tags were found: [{}]", formatSuspiciousHtmlTags(allSuspiciousHtmlTagsFound));
		}
	}

	private void checkDescription(String description, String item, String itemName, Set<String> allSuspiciousHtmlTagsFound) {
		if (description == null)
			return;
		List<String> htmlTags = Utils.getHtmlTags(description);
		Set<String> suspiciousHtmlTags = new HashSet<>(htmlTags);
		EXPECTED_HTML_TAGS.forEach(suspiciousHtmlTags::remove);
		allSuspiciousHtmlTagsFound.addAll(suspiciousHtmlTags);
		if(! suspiciousHtmlTags.isEmpty()) {
			log.warn("{} [{}] has a description with suspicious HTML tags: [{}]", item, itemName, formatSuspiciousHtmlTags(suspiciousHtmlTags));
		}
	}

	private String formatSuspiciousHtmlTags(Set<String> suspiciousHtmlTags) {
		return suspiciousHtmlTags.stream().map(s -> "<" + s + ">").collect(Collectors.joining(", "));
	}

	// This method handles a corner case regarding Java classes that share a simple name.
	// Typically, if multiple Java classes share a simple name, then some of these classes are deprecated,
	// leaving only one of the Java classes non-deprecated. ElementRole detects the conflict. FrankElement
	// instances that are deprecated and that have a duplicate name are not returned as members of the ElementRole,
	// allowing them to be omitted as elements in the XSD.
	//
	// However, when a deprecated FrankElement with a duplicate name is the inheritance parent of a
	// non-deprecated FrankElement with the same simple name, then we still may need type definitions
	// for the parent. To produce unique type names, we need a sequence number. Setting
	// that sequence number is the purpose of this method.
	void calculateTypeNameSeq() {
		Map<String, List<FrankElement>> elementsBySimpleName = allElements.values().stream()
				.collect(Collectors.groupingBy(FrankElement::getSimpleName));
		for(String name: elementsBySimpleName.keySet()) {
			List<FrankElement> nameGroup = new ArrayList<>(elementsBySimpleName.get(name));
			Collections.sort(nameGroup);
			for(int seq = 0; seq < nameGroup.size(); ++seq) {
				// When we make names unique, we omit the first sequence number. We want names like
				// xxx, xxx2, xxx3. If we would start with zero, we would have xxx, xxx1, xxx2 which
				// looks strange.
				nameGroup.get(seq).setTypeNameSeq(seq + 1);
			}
		}
	}

	void calculateInterfaceBased() {
		allTypes.values().stream().filter(ElementType::isFromJavaInterface)
			.flatMap(et -> et.getMembers().stream())
			.forEach(f -> f.setInterfaceBased(true));
	}

	public void buildGroups() {
		Map<String, List<ElementType>> groupsElementTypes = allTypes.values().stream()
				.collect(Collectors.groupingBy(f -> f.getGroup().getName()));
		groups = groupFactory.getAllGroups();
		for(FrankDocGroup group: groups) {
			// The default applies to group Other in case it has no ElementType objects.
			// In this case we still need group Other for all items in elementsOutsideConfigChildren.
			// This is typically one element that plays the role of Configuration in some tests.
			List<ElementType> elementTypes = new ArrayList<>();
			if(groupsElementTypes.containsKey(group.getName())) {
				elementTypes = new ArrayList<>(groupsElementTypes.get(group.getName()));
			}
			Collections.sort(elementTypes);
			group.setElementTypes(elementTypes);
		}
		final Map<String, FrankElement> leftOvers = new HashMap<>(allElements);
		allTypes.values().stream().flatMap(et -> et.getSyntax2Members().stream()).forEach(f -> leftOvers.remove(f.getFullName()));
		elementsOutsideConfigChildren = leftOvers.values().stream()
				.filter(f -> ! f.isAbstract())
				.filter(f -> ! f.getXmlElementNames().isEmpty())
				.sorted()
				.collect(Collectors.toList());
	}

	public List<String> getAllLabels() {
		return labelValues.getAllLabels();
	}

	public List<String> getAllValuesOfLabel(String label) {
		return labelValues.getAllValuesOfLabel(label);
	}
}
