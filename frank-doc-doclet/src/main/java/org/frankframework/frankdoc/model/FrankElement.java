/* 
Copyright 2020, 2021, 2022 WeAreFrank! 

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.feature.Deprecated;
import org.frankframework.frankdoc.feature.Description;
import org.frankframework.frankdoc.feature.Protected;
import org.frankframework.frankdoc.model.ElementChild.AbstractKey;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.FrankType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Models a Java class that can be referred to in a Frank configuration.
 * 
 * @author martijn
 *
 */
public class FrankElement implements Comparable<FrankElement> {
	static final String JAVADOC_PARAMETERS = "@ff.parameters";
	public static final String JAVADOC_PARAMETER = "@ff.parameter";
	public static final String JAVADOC_FORWARD = "@ff.forward";
	public static final String JAVADOC_TAG = "@ff.tag";
	public static final String LABEL = "nl.nn.adapterframework.doc.Label";
	public static final String LABEL_NAME = "name";

	private static Logger log = LogUtil.getLogger(FrankElement.class);

	private static final Comparator<FrankElement> COMPARATOR =
			Comparator.comparing(FrankElement::getSimpleName).thenComparing(FrankElement::getFullName);

	private static final Pattern DESCRIPTION_HEADER_SPLIT = Pattern.compile("(\\. )|(\\.\\n)|(\\.\\r\\n)");

	private @Getter LinkedHashMap<FrankMethod, Integer> unusedConfigChildSetterCandidates = new LinkedHashMap<>();
	private @Getter List<ConfigChild> configChildrenUnderConstruction = new ArrayList<>();
	
	private final @Getter String fullName;
	private final @Getter String simpleName;
	
	// Needed to handle a corner case that is explained with method FrankDocModel.calculateTypeNameSeq
	private @Setter int typeNameSeq = 1;
	
	private final @Getter boolean isAbstract;
	private @Getter boolean isDeprecated = false;

	// True if this FrankElement corresponds to a class that implements a Java interface
	// that we model with an ElementType. This means: The Java interface only counts
	// if it appears as argument type of a config child setter.
	private @Getter @Setter(AccessLevel.PACKAGE) boolean interfaceBased = false;

	// Represents the Java superclass.
	private @Getter FrankElement parent;

	// Used when config children are constructed. A config child is only
	// created when there is a matching rule in digester-rules. These
	// rules have to match config children for which this FrankElement
	// is member of the config child's ElementType.
	private List<ConfigChild> configParents = new ArrayList<>();

	private Map<Class<? extends ElementChild>, LinkedHashMap<? extends AbstractKey, ? extends ElementChild>> allChildren;
	private @Getter List<String> xmlElementNames;
	private @Getter FrankElementStatistics statistics;
	private LinkedHashMap<String, ConfigChildSet> configChildSets;
	private @Getter @Setter String description;
	private @Getter @Setter String descriptionHeader;
	private @Getter String meaningOfParameters;
	private @Getter List<ParsedJavaDocTag> specificParameters = new ArrayList<>();
	private @Getter List<ParsedJavaDocTag> forwards = new ArrayList<>();
	private @Getter List<ParsedJavaDocTag> tags = new ArrayList<>();

	private @Getter List<FrankLabel> labels = new ArrayList<>();

	FrankElement(FrankClass clazz, FrankClassRepository repository, FrankDocGroupFactory groupFactory, LabelValues labelValues) {
		this(clazz.getName(), clazz.getSimpleName(), clazz.isAbstract());
		isDeprecated = Deprecated.getInstance().isSetOn(clazz);
		configChildSets = new LinkedHashMap<>();
		this.completeFrankElement(clazz);
		handleConfigChildSetterCandidates(clazz);
		handlePossibleParameters(clazz);
		handlePossibleForwards(clazz);
		handlePossibleTags(clazz);
		handleLabels(clazz, labelValues);
	}

	private void completeFrankElement(FrankClass clazz) {
		setDescription(Description.getInstance().valueOf(clazz));
		if(getDescription() != null) {
			setDescriptionHeader(calculateDescriptionHeader(getDescription()));
		}
	}

	static String calculateDescriptionHeader(String description) {
		String descriptionHeader = DESCRIPTION_HEADER_SPLIT.split(description)[0];
		String remainder = description.substring(descriptionHeader.length());
		if(remainder.startsWith(".")) {
			descriptionHeader = descriptionHeader + ".";
		}
		return descriptionHeader;
	}

	private void handleConfigChildSetterCandidates(FrankClass clazz) {
		List<FrankMethod> methods = Arrays.asList(clazz.getDeclaredMethodsAndMultiplyInheritedPlaceholders()).stream()
				.filter(FrankMethod::isPublic)
				.filter(Utils::isConfigChildSetter)
				.collect(Collectors.toList());
		for(int i = 0; i < methods.size(); ++i) {
			if(! configChildCandidateHasProtectedArgument(methods.get(i))) {
				unusedConfigChildSetterCandidates.put(methods.get(i), i);
			}
		}
	}

	private boolean configChildCandidateHasProtectedArgument(FrankMethod frankMethod) {
		log.trace("Checking method [{}]", () -> frankMethod.toString());
		FrankType argumentType = frankMethod.getParameterTypes()[0];
		if(! (argumentType instanceof FrankClass)) {
			// Text config child, wont have feature PROTECTED
			return false;
		}
		FrankClass argument = (FrankClass) argumentType;
		if(argument.isInterface()) {
			return false;
		}
		if(classIsProtected(argument)) {
			log.trace("Method [{}] is not a config child candidate because class [{}] has feature PROTECTED", () -> frankMethod.toString(), () -> argument.toString());
			return true;
		}
		return false;
	}

	static boolean classIsProtected(FrankClass clazz) {
		IsProtectedContext ctx = new IsProtectedContext();
		try {
			clazz.browseAncestors(ctx::handleAncestorMethod);
		} catch(FrankDocException e) {
			log.error("Could not browse ancestor classes of class [{}]", clazz.getName(), e);
		}
		if(ctx.isProtected) {
			log.trace("Class [{}] inherits feature Protected from class [{}]", () -> clazz.getName(), () -> ctx.cause.getName());
		}
		return ctx.isProtected;
	}

	private static class IsProtectedContext {
		boolean isProtected = false;
		FrankClass cause = null;

		void handleAncestorMethod(FrankClass ancestorClass) {
			if(Protected.getInstance().isSetOn(ancestorClass)) {
				isProtected = true;
				if(cause == null) {
					cause = ancestorClass;
				}
			}
		}
	}

	private void handlePossibleParameters(FrankClass clazz) {
		this.meaningOfParameters = clazz.getJavaDocTag(JAVADOC_PARAMETERS);
		assembleParsedJavaDocTags(clazz, JAVADOC_PARAMETER, p -> this.specificParameters.add(p));
	}

	private void handlePossibleForwards(FrankClass clazz) {
		assembleParsedJavaDocTags(clazz, JAVADOC_FORWARD, p -> this.forwards.add(p));
	}

	private void handlePossibleTags(FrankClass clazz) {
		assembleParsedJavaDocTags(clazz, JAVADOC_TAG, p -> this.tags.add(p));
		Map<String, Long> tagCounts = tags.stream().collect(Collectors.groupingBy(ParsedJavaDocTag::getName, Collectors.counting()));
		List<String> duplicates = tagCounts.entrySet().stream()
				.filter(e -> e.getValue() >= 2L)
				.map(Entry::getKey)
				.sorted()
				.collect(Collectors.toList());
		for(String duplicate: duplicates) {
			log.error("FrankElement [{}] has multiple values for tag [{}]", fullName, duplicate);
		}
	}

	private void assembleParsedJavaDocTags(FrankClass clazz, String tagName, Consumer<ParsedJavaDocTag> acceptor) {
		for(String arguments: clazz.getAllJavaDocTagsOf(tagName)) {
			ParsedJavaDocTag parsed = null;
			try {
				parsed = ParsedJavaDocTag.getInstance(arguments);
			} catch(FrankDocException e) {
				log.error("Error parsing a [{}] tag of class [{}]", tagName, fullName, e);
				continue;
			}
			if(parsed.getDescription() == null) {
				log.warn("FrankElement [{}] has a [{}] tag without a value: [{}]", fullName, tagName, arguments);
			}
			acceptor.accept(parsed);
		}
	}

	private void handleLabels(FrankClass clazz, LabelValues labelValues) {
		List<FrankAnnotation> annotationsForLabels = Arrays.asList(clazz.getAnnotations()).stream()
				.filter(a -> a.getAnnotation(LABEL) != null)
				.collect(Collectors.toList());
		for(FrankAnnotation a: annotationsForLabels) {
			try {
				handleSpecificLabel(a, labelValues);
			} catch(FrankDocException e) {
				log.error("Could not parse label [{}] of [{}]", a.getName(), toString());
			}
		}
	}

	void handleSpecificLabel(FrankAnnotation labelAddingAnnotation, LabelValues labelValues) throws FrankDocException {
		String label = labelAddingAnnotation.getAnnotation(LABEL).getValueOf(LABEL_NAME).toString();
		Object rawValue = labelAddingAnnotation.getValue();
		if(rawValue instanceof FrankEnumConstant) {
			FrankEnumConstant enumConstant = (FrankEnumConstant) rawValue;
			// This considers annotation @EnumLabel
			EnumValue value = new EnumValue(enumConstant);
			labels.add(new FrankLabel(label, value.getLabel()));
			labelValues.addEnumValue(label, value.getLabel(), enumConstant.getPosition());
		} else {
			labels.add(new FrankLabel(label, rawValue.toString()));
			labelValues.addValue(label, rawValue.toString());
		}
	}

	/**
	 * Constructor for testing purposes. We want to test attribute construction in isolation,
	 * in which case we do not have a parent.
	 * TODO: Reorganize files such that this test constructor need not be public.
	 */
	public FrankElement(final String fullName, final String simpleName, boolean isAbstract) {
		this.fullName = fullName;
		this.simpleName = simpleName;
		this.isAbstract = isAbstract;
		this.allChildren = new HashMap<>();
		this.allChildren.put(FrankAttribute.class, new LinkedHashMap<>());
		this.allChildren.put(ConfigChild.class, new LinkedHashMap<>());
		this.xmlElementNames = new ArrayList<>();
	}

	public void setParent(FrankElement parent) {
		this.parent = parent;
		this.statistics = new FrankElementStatistics(this);
	}

	void addConfigParent(ConfigChild parent) {
		log.trace("To [{}] [{}] added config parent [{}]", this.getClass().getSimpleName(), fullName, parent.toString());
		configParents.add(parent);
	}

	public List<ConfigChild> getConfigParents() {
		List<ConfigChild> result = new ArrayList<>();
		result.addAll(configParents);
		return result;
	}

	public void addXmlElementName(String elementName) {
		Utils.addToSortedListUnique(xmlElementNames, elementName);
	}

	public boolean hasOnePossibleXmlElementName() {
		return xmlElementNames.size() == 1;
	}

	public String getTheSingleXmlElementName() {
		if(! hasOnePossibleXmlElementName()) {
			throw new IllegalStateException(String.format("FrankElement [%s] has more then one possible XML element name: [%s]",
					fullName, xmlElementNames.stream().collect(Collectors.joining(", "))));
		}
		return xmlElementNames.get(0);
	}

	public void setAttributes(List<FrankAttribute> inputAttributes) {
		setChildrenOfKind(inputAttributes, FrankAttribute.class);
	}

	private <C extends ElementChild> void setChildrenOfKind(List<C> inputChildren, Class<C> kind) {
		LinkedHashMap<AbstractKey, C> children = new LinkedHashMap<>();
		for(C c: inputChildren) {
			if(children.containsKey(c.getKey())) {
				log.error("Frank element [{}] has multiple attributes / config children with key [{}]",
						() -> fullName, () -> c.getKey().toString());
			} else {
				children.put(c.getKey(), c);
			}
		}
		allChildren.put(kind, children);
	}

	public List<FrankAttribute> getAttributes(Predicate<ElementChild> filter) {
		return getChildrenOfKind(filter, FrankAttribute.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends ElementChild> List<T> getChildrenOfKind(Predicate<ElementChild> selector, Class<T> kind) {
		Map<? extends AbstractKey, ? extends ElementChild> lookup = allChildren.get(kind);
		return lookup.values().stream().filter(selector).map(c -> (T) c).collect(Collectors.toList());
	}

	public void setConfigChildren(List<ConfigChild> children) {
		setChildrenOfKind(children, ConfigChild.class);
	}

	public List<ConfigChild> getConfigChildren(Predicate<ElementChild> filter) {
		return getChildrenOfKind(filter, ConfigChild.class);
	}

	<C extends ElementChild> ElementChild findElementChildMatch(C elementChild) {
		Class<? extends ElementChild> clazz = elementChild.getClass();
		// We do not have separate lookups for ObjectConfigChild and TextConfigChild.
		// We only have a lookup for ConfigChild.
		if(elementChild instanceof ConfigChild) {
			clazz = ConfigChild.class;
		}
		Map<? extends AbstractKey, ? extends ElementChild> lookup = allChildren.get(clazz);
		return lookup.get(elementChild.getKey());
	}

	public FrankElement getNextAncestorThatHasChildren(Predicate<FrankElement> noChildren) {
		FrankElement ancestor = parent;
		while((ancestor != null) && noChildren.test(ancestor)) {
			ancestor = ancestor.getParent();
		}
		return ancestor;
	}

	public FrankElement getNextAncestorThatHasOrRejectsConfigChildren(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		FrankElement ancestorConfigChildren = getNextAncestorThatHasChildren(el -> (
				el.getChildrenOfKind(selector, ConfigChild.class).isEmpty()
				&& el.getChildrenOfKind(rejector, ConfigChild.class).isEmpty()));
		return ancestorConfigChildren;
	}

	public FrankElement getNextAncestorThatHasOrRejectsAttributes(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		FrankElement ancestorAttributes = getNextAncestorThatHasChildren(el -> (
				el.getChildrenOfKind(selector, FrankAttribute.class).isEmpty()
				&& el.getChildrenOfKind(rejector, FrankAttribute.class).isEmpty()));
		return ancestorAttributes;
	}

	public void walkCumulativeAttributes(
			CumulativeChildHandler<FrankAttribute> handler, Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector) {
		new AncestorChildNavigation<FrankAttribute>(
				handler, childSelector, childRejector, FrankAttribute.class).run(this);
	}

	public void walkCumulativeConfigChildren(
			CumulativeChildHandler<ConfigChild> handler, Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector) {
		new AncestorChildNavigation<ConfigChild>(
				handler, childSelector, childRejector, ConfigChild.class).run(this);		
	}

	public List<ConfigChild> getCumulativeConfigChildren(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		return getCumulativeChildren(selector, rejector, ConfigChild.class).stream()
				.map(c -> (ConfigChild) c).collect(Collectors.toList());
	}

	public List<FrankAttribute> getCumulativeAttributes(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		return getCumulativeChildren(selector, rejector, FrankAttribute.class).stream()
				.map(c -> (FrankAttribute) c).collect(Collectors.toList());
	}

	private <T extends ElementChild> List<T> getCumulativeChildren(Predicate<ElementChild> selector, Predicate<ElementChild> rejector, Class<T> kind) {
		final List<T> result = new ArrayList<>();
		new AncestorChildNavigation<T>(new CumulativeChildHandler<T>() {
			@Override
			public void handleSelectedChildren(List<T> children, FrankElement owner) {
				result.addAll(children);
			}

			@Override
			public void handleSelectedChildrenOfTopLevel(List<T> children, FrankElement owner) {
				handleSelectedChildren(children, owner);
			}

			@Override
			public void handleChildrenOf(FrankElement frankElement) {
				result.addAll(frankElement.getChildrenOfKind(selector, kind));
			}

			@Override
			public void handleCumulativeChildrenOf(FrankElement frankElement) {
				result.addAll(frankElement.getCumulativeChildren(selector, rejector, kind));
			}
		}, selector, rejector, kind).run(this);
		return result;
	}

	public String getXsdElementName(ElementRole elementRole) {
		return getXsdElementName(elementRole.getElementType(), elementRole.getRoleName());
	}

	String getXsdElementName(ElementType elementType, String roleName) {
		if(! elementType.isFromJavaInterface()) {
			return Utils.toUpperCamelCase(roleName);
		}
		// Depends on the fact that FrankDocModel.calculateCommonInterfacesHierarchies() has been executed.
		//
		// There is a subtle point here: the difference between ElementType.getHighestCommonInterface()
		// and ElementRole.getHighestCommonInterface(). Consider for example the ElementRole
		// (IWrapperPipe, outputWrapper). The highest common interface of ElementType IWrapperPipe is
		// IPipe. For this reason, Java class ApiSoapWrapperPipe will produce ApiSoapWrapperOutputWrapper
		// as we want, not the erroneous name ApiSoapWrapperPipeOutputWrapper.
		//
		// On the other hand, the highest common interface of the mentioned ElementRole is just the same ElementRole.
		// The reason is that there is no config child setter for ElementType IPipe and role name outputWrapper.
		// This is also what we want. When the config children of Pipeline are calculated, all ElementRole-s of the
		// config children are promoted to their highest common interface to avoid conflicts. We don't have a conflict
		// here so there is no need to promote (IWrapperPipe, outputWrapper). This way, the outputWrapper config child
		// only allows elements that implement IWrapperPipe, not all implementations of IPipe.
		//
		List<String> removablePostfixes = elementType.getCommonInterfaceHierarchy().stream().map(ElementType::getGroupName).collect(Collectors.toList());
		String result = simpleName;
		for(String removablePostfix: removablePostfixes) {
			if(result.endsWith(removablePostfix)) {
				result = result.substring(0, result.lastIndexOf(removablePostfix));
				break;
			}			
		}
		result = result + Utils.toUpperCamelCase(roleName);
		return result;
	}

	void addConfigChildSet(ConfigChildSet configChildSet) {
		configChildSets.put(configChildSet.getRoleName(), configChildSet);
	}

	public ConfigChildSet getConfigChildSet(String roleName) {
		return configChildSets.get(roleName);
	}

	public List<ConfigChildSet> getCumulativeConfigChildSets() {
		Map<String, ConfigChildSet> resultAsMap = new HashMap<>();
		for(String roleName: configChildSets.keySet()) {
			resultAsMap.put(roleName, configChildSets.get(roleName));
		}
		if(parent != null) {
			List<ConfigChildSet> inheritedConfigChildSets = getParent().getCumulativeConfigChildSets();
			for(ConfigChildSet inherited: inheritedConfigChildSets) {
				resultAsMap.putIfAbsent(inherited.getRoleName(), inherited);
			}
		}
		List<ConfigChildSet> result = new ArrayList<>();
		List<String> keys = new ArrayList<>(resultAsMap.keySet());
		Collections.sort(keys);
		for(String key: keys) {
			result.add(resultAsMap.get(key));
		}
		return result;
	}

	public boolean hasFilledConfigChildSets(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		if(configChildSets.isEmpty()) {
			return false;
		}
		return configChildSets.values().stream()
				.anyMatch(cs -> cs.getConfigChildren().stream().filter(selector.or(rejector)).collect(Collectors.counting()) >= 1);
	}

	public FrankElement getNextPluralConfigChildrenAncestor(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		FrankElement ancestor = parent;
		while(ancestor != null) {
			if(! ancestor.getParent().hasOrInheritsPluralConfigChildren(selector, rejector)) {
				return ancestor;
			}
			if(ancestor.hasFilledConfigChildSets(selector, rejector)) {
				return ancestor;
			}
			ancestor = ancestor.getParent();
		}
		return null;
	}

	public boolean hasOrInheritsPluralConfigChildren(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		boolean hasPluralConfigChildren = configChildSets.values().stream()
				.anyMatch(c -> c.getFilteredElementRoles(selector, rejector).size() >= 2);
		boolean inheritsPluralConfigChildren = false;
		FrankElement ancestor = getNextAncestorThatHasOrRejectsConfigChildren(selector, rejector);
		if(ancestor != null) {
			inheritsPluralConfigChildren = ancestor.hasOrInheritsPluralConfigChildren(selector, rejector);
		}
		return hasPluralConfigChildren || inheritsPluralConfigChildren;
	}

	public String getTypeNameBase() {
		if(typeNameSeq <= 1) {
			return getSimpleName();
		} else {
			return getSimpleName() + Integer.valueOf(typeNameSeq).toString();
		}
	}

	@Override
	public int compareTo(FrankElement other) {
		return COMPARATOR.compare(this, other);
	}

	static String describe(Collection<FrankElement> collection) {
		return collection.stream().map(FrankElement::getFullName).collect(Collectors.joining(", "));
	}

	static Set<FrankElement> join(Set<FrankElement> s1, Set<FrankElement> s2) {
		Set<FrankElement> result = new HashSet<>();
		result.addAll(s1);
		result.addAll(s2);
		return result;
	}

	@Override
	public String toString() {
		return fullName;
	}
}
