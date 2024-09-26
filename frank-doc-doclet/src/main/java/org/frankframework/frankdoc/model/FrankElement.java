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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.feature.Deprecated;
import org.frankframework.frankdoc.feature.Description;
import org.frankframework.frankdoc.feature.Notes;
import org.frankframework.frankdoc.feature.Protected;
import org.frankframework.frankdoc.model.ElementChild.AbstractKey;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.FrankType;

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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Models a Java class that can be referred to in a Frank configuration.
 *
 * @author martijn
 */
public class FrankElement implements Comparable<FrankElement> {
	static final String JAVADOC_PARAMETERS = "@ff.parameters";
	public static final String JAVADOC_PARAMETER = "@ff.parameter";
	public static final String JAVADOC_FORWARD = "@ff.forward";
	public static final String JAVADOC_FORWARD_ANNOTATION_CLASSNAME = "org.frankframework.doc.Forward";
	public static final String JAVADOC_FORWARDS_ANNOTATION_CLASSNAME = "org.frankframework.doc.Forwards";
	public static final String JAVADOC_LABEL_ANNOTATION_CLASSNAME = "org.frankframework.doc.Label";
	public static final String JAVADOC_LABELS_ANNOTATION_CLASSNAME = "org.frankframework.doc.Categories";
	public static final String JAVADOC_SEE = "@see";
	public static final String LABEL_NAME = "name";

	private static final Pattern JAVADOC_SEE_PATTERN = Pattern.compile("<a href=[\"'](.*?)[\"']>(.*?)<\\/a>");

	private static Logger log = LogUtil.getLogger(FrankElement.class);

	private static final Comparator<FrankElement> COMPARATOR =
		Comparator.comparing(FrankElement::getSimpleName).thenComparing(FrankElement::getFullName);

	private static final Pattern DESCRIPTION_HEADER_SPLIT = Pattern.compile("(\\. )|(\\.\\n)|(\\.\\r\\n)");

	private final @Getter LinkedHashMap<FrankMethod, Integer> unusedConfigChildSetterCandidates = new LinkedHashMap<>();
	private final @Getter List<ConfigChild> configChildrenUnderConstruction = new ArrayList<>();

	private final @Getter String fullName;
	private final @Getter String simpleName;

	// Needed to handle a corner case that is explained with method FrankDocModel.calculateTypeNameSeq
	private @Setter int typeNameSeq = 1;

	private final @Getter boolean isAbstract;
	private @Getter DeprecationInfo deprecationInfo;

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
	private @Getter String description;
	private @Getter String descriptionHeader;
	private @Getter String meaningOfParameters;
	private @Getter List<ParsedJavaDocTag> specificParameters = new ArrayList<>();
	private @Getter List<Forward> forwards = new ArrayList<>();
	private @Getter List<QuickLink> quickLinks = new ArrayList<>();
	private @Getter List<ParsedJavaDocTag> tags = new ArrayList<>();
	private @Getter List<Note> notes = new ArrayList<>();

	private @Getter Map<String, List<String>> labels = new HashMap<>();

	FrankElement(FrankClass clazz, LabelValues labelValues) {
		this(clazz.getName(), clazz.getSimpleName(), clazz.isAbstract());
		deprecationInfo = Deprecated.getInstance().getInfo(clazz);
		configChildSets = new LinkedHashMap<>();

		handleConfigChildSetterCandidates(clazz);
		meaningOfParameters = parseParametersJavadocTag(clazz);
		specificParameters = parseParameterJavadocTags(clazz);
		forwards = parseForwardJavadocTags(clazz);
		quickLinks = parseSeeJavadocTags(clazz);
		notes = Notes.getInstance().valueOf(clazz);
		labels = parseLabelAnnotations(clazz, labelValues);

		description = Description.getInstance().valueOf(clazz);
		descriptionHeader = renderDescriptionHeader();
	}

	private String renderDescriptionHeader() {
		if (getDescription() != null) {
			return calculateDescriptionHeader(getDescription());
		}

		return null;
	}

	static String calculateDescriptionHeader(String description) {
		String descriptionHeader = DESCRIPTION_HEADER_SPLIT.split(description)[0];
		String remainder = description.substring(descriptionHeader.length());
		if (remainder.startsWith(".")) {
			descriptionHeader = descriptionHeader + ".";
		}
		return descriptionHeader;
	}

	private void handleConfigChildSetterCandidates(FrankClass clazz) {
		List<FrankMethod> methods = Arrays.asList(clazz.getDeclaredMethodsAndMultiplyInheritedPlaceholders()).stream()
			.filter(FrankMethod::isPublic)
			.filter(Utils::isConfigChildSetter)
			.collect(Collectors.toList());
		for (int i = 0; i < methods.size(); ++i) {
			if (!configChildCandidateHasProtectedArgument(methods.get(i))) {
				unusedConfigChildSetterCandidates.put(methods.get(i), i);
			}
		}
	}

	private boolean configChildCandidateHasProtectedArgument(FrankMethod frankMethod) {
		log.trace("Checking method [{}]", frankMethod::toString);
		FrankType argumentType = frankMethod.getParameterTypes()[0];
		if (!(argumentType instanceof FrankClass)) {
			// Text config child, wont have feature PROTECTED
			return false;
		}
		FrankClass argument = (FrankClass) argumentType;
		if (argument.isInterface()) {
			return false;
		}
		if (classIsProtected(argument)) {
			log.trace("Method [{}] is not a config child candidate because class [{}] has feature PROTECTED", frankMethod::toString, argument::toString);
			return true;
		}
		return false;
	}

	static boolean classIsProtected(FrankClass clazz) {
		IsProtectedContext ctx = new IsProtectedContext();
		try {
			clazz.browseAncestors(ctx::handleAncestorMethod);
		} catch (FrankDocException e) {
			log.error("Could not browse ancestor classes of class [{}]", clazz.getName(), e);
		}
		if (ctx.isProtected) {
			log.trace("Class [{}] inherits feature Protected from class [{}]", clazz::getName, () -> ctx.cause.getName());
		}
		return ctx.isProtected;
	}

	private static class IsProtectedContext {
		boolean isProtected = false;
		FrankClass cause = null;

		void handleAncestorMethod(FrankClass ancestorClass) {
			if (Protected.getInstance().isSetOn(ancestorClass)) {
				isProtected = true;
				if (cause == null) {
					cause = ancestorClass;
				}
			}
		}
	}

	private List<ParsedJavaDocTag> parseParameterJavadocTags(FrankClass clazz) {
		return parseJavadocTags(clazz, JAVADOC_PARAMETER);
	}

	private String parseParametersJavadocTag(FrankClass clazz) {
		return Utils.substituteJavadocTags(clazz.getJavaDocTag(JAVADOC_PARAMETERS), clazz);
	}

	private List<Forward> parseForwardJavadocTags(FrankClass clazz) {
		// The following line can be removed when ff.forward should no longer be supported.
		List<Forward> forwards = new ArrayList<>(parseJavadocTags(clazz, JAVADOC_FORWARD).stream()
			.map(tag -> new Forward(tag.getName(), tag.getDescription()))
			.toList());

		// The Forwards annotation contains an array of Forward annotation (repeatable annotation).
		// The Forwards annotation will not exist when the class has only one Forward annotation.
		FrankAnnotation forwardsAnnotation = clazz.getAnnotation(JAVADOC_FORWARDS_ANNOTATION_CLASSNAME);
		if (forwardsAnnotation != null) {
			FrankAnnotation[] forwardAnnotations = (FrankAnnotation[]) forwardsAnnotation.getValue();

			forwards.addAll(Arrays.stream(forwardAnnotations)
				.map(this::annotationToForward)
				.toList());
		}

		FrankAnnotation forwardAnnotation = clazz.getAnnotation(JAVADOC_FORWARD_ANNOTATION_CLASSNAME);
		if (forwardAnnotation != null) {
			forwards.add(annotationToForward(forwardAnnotation));
		}

		return forwards;
	}

	private Forward annotationToForward(FrankAnnotation annotation) {
		String name = (String) annotation.getValueOf("name");
		String description = (String) annotation.getValueOf("description");

		if (description.isEmpty()) {
			description = null;
		}

		return new Forward(name, description);
	}

	private List<QuickLink> parseSeeJavadocTags(FrankClass clazz) {
		return clazz.getAllJavaDocTagsOf(JAVADOC_SEE).stream()
			.map(value -> {
				final var matcher = JAVADOC_SEE_PATTERN.matcher(value);

				if (!matcher.matches()) {
					return null;
				}

				String label = matcher.group(2);
				String url = matcher.group(1);

				return new QuickLink(label, url);
			})
			.filter(Objects::nonNull)
			.toList();
	}

	private List<ParsedJavaDocTag> parseJavadocTags(FrankClass clazz, String tagName) {
		return clazz.getAllJavaDocTagsOf(tagName).stream()
			.map(arguments -> {
				ParsedJavaDocTag parsed;
				try {
					parsed = ParsedJavaDocTag.getInstance(arguments, s -> Utils.substituteJavadocTags(s, clazz));
				} catch (FrankDocException e) {
					log.error("Error parsing a [{}] tag of class [{}]", tagName, fullName, e);
					return null;
				}
				if (parsed.getDescription() == null) {
					log.warn("FrankElement [{}] has a [{}] tag without a value: [{}]", fullName, tagName, arguments);
				}

				return parsed;
			})
			.filter(Objects::nonNull)
			.toList();
	}

	private boolean isLabelAnnotation(FrankAnnotation annotation) {
		// Check if annotation is a singular label.
		if (annotation.getAnnotation(JAVADOC_LABEL_ANNOTATION_CLASSNAME) != null)
			return true;

		// Check if annotation contains a list of label annotations.
		Object value = annotation.getValue();

		if (value instanceof FrankAnnotation[] valueAnnotations) {
			if (valueAnnotations.length > 0) {
				return valueAnnotations[0].getAnnotation(JAVADOC_LABEL_ANNOTATION_CLASSNAME) != null;
			}
		}

		return false;
	}

	private Map<String, List<String>> parseLabelAnnotations(FrankClass clazz, LabelValues labelValues) {
		return Arrays.stream(clazz.getAnnotations())
			.filter(this::isLabelAnnotation)
			.flatMap(annotation -> parseLabelAnnotation(annotation, labelValues))
			.collect(Collectors.groupingBy(FrankLabel::getName, Collectors.mapping(FrankLabel::getValue, Collectors.toList())));
	}

	private Stream<FrankLabel> parseLabelAnnotation(FrankAnnotation annotation, LabelValues labelValues) {
		Object value = annotation.getValue();

		if (value instanceof FrankAnnotation[] annotations) {
			return Arrays.stream(annotations)
				.map(a -> annotationToFrankLabel(a, labelValues));
		}

		FrankAnnotation labelAnnotation = annotation.getAnnotation(JAVADOC_LABEL_ANNOTATION_CLASSNAME);
		if (labelAnnotation != null) {
			return Stream.of(annotationToFrankLabel(annotation, labelValues));
		}

		return Stream.empty();
	}

	// This function adds the label, and it's corresponding value to the global LabelValues as a side effect.
	private FrankLabel annotationToFrankLabel(FrankAnnotation labelAddingAnnotation, LabelValues labelValues) {
		String label = labelAddingAnnotation.getAnnotation(JAVADOC_LABEL_ANNOTATION_CLASSNAME).getValueOf(LABEL_NAME).toString();
		Object rawValue = labelAddingAnnotation.getValue();

		if (rawValue instanceof FrankEnumConstant enumConstant) {
			// This considers annotation @EnumLabel
			EnumValue value = new EnumValue(enumConstant);
			labelValues.addValue(label, value.getLabel());
			return new FrankLabel(label, value.getLabel());
		}

		labelValues.addValue(label, rawValue.toString());
		return new FrankLabel(label, rawValue.toString());
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

		if (fullName.contains("<")) {
			log.error("Still found type with Generic information in type at: {}. Please fix this inside FrankDocDoclet.", fullName);
		}
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
		if (!hasOnePossibleXmlElementName()) {
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
		for (C c : inputChildren) {
			if (children.containsKey(c.getKey())) {
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
		if (elementChild instanceof ConfigChild) {
			clazz = ConfigChild.class;
		}
		Map<? extends AbstractKey, ? extends ElementChild> lookup = allChildren.get(clazz);
		return lookup.get(elementChild.getKey());
	}

	public FrankElement getNextAncestorThatHasChildren(Predicate<FrankElement> noChildren) {
		FrankElement ancestor = parent;
		while ((ancestor != null) && noChildren.test(ancestor)) {
			ancestor = ancestor.getParent();
		}
		return ancestor;
	}

	public FrankElement getNextAncestorThatHasOrRejectsConfigChildren(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		return getNextAncestorThatHasChildren(el -> (
			el.getChildrenOfKind(selector, ConfigChild.class).isEmpty()
				&& el.getChildrenOfKind(rejector, ConfigChild.class).isEmpty()));
	}

	public FrankElement getNextAncestorThatHasOrRejectsAttributes(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		return getNextAncestorThatHasChildren(el -> (
			el.getChildrenOfKind(selector, FrankAttribute.class).isEmpty()
				&& el.getChildrenOfKind(rejector, FrankAttribute.class).isEmpty()));
	}

	public void walkCumulativeAttributes(
		CumulativeChildHandler<FrankAttribute> handler, Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector) {
		new AncestorChildNavigation<>(
			handler, childSelector, childRejector, FrankAttribute.class).run(this);
	}

	public void walkCumulativeConfigChildren(
		CumulativeChildHandler<ConfigChild> handler, Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector) {
		new AncestorChildNavigation<>(
			handler, childSelector, childRejector, ConfigChild.class).run(this);
	}

	public List<ConfigChild> getCumulativeConfigChildren(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		return new ArrayList<>(getCumulativeChildren(selector, rejector, ConfigChild.class));
	}

	public List<FrankAttribute> getCumulativeAttributes(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		return new ArrayList<>(getCumulativeChildren(selector, rejector, FrankAttribute.class));
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
		if (!elementType.isFromJavaInterface()) {
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
		for (String removablePostfix : removablePostfixes) {
			if (result.endsWith(removablePostfix)) {
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
		for (String roleName : configChildSets.keySet()) {
			resultAsMap.put(roleName, configChildSets.get(roleName));
		}
		if (parent != null) {
			List<ConfigChildSet> inheritedConfigChildSets = getParent().getCumulativeConfigChildSets();
			for (ConfigChildSet inherited : inheritedConfigChildSets) {
				resultAsMap.putIfAbsent(inherited.getRoleName(), inherited);
			}
		}
		List<ConfigChildSet> result = new ArrayList<>();
		List<String> keys = new ArrayList<>(resultAsMap.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			result.add(resultAsMap.get(key));
		}
		return result;
	}

	public boolean hasFilledConfigChildSets(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		if (configChildSets.isEmpty()) {
			return false;
		}
		return configChildSets.values().stream()
			.anyMatch(cs -> cs.getConfigChildren().stream().filter(selector.or(rejector)).count() >= 1);
	}

	public FrankElement getNextPluralConfigChildrenAncestor(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		FrankElement ancestor = parent;
		while (ancestor != null) {
			if (!ancestor.getParent().hasOrInheritsPluralConfigChildren(selector, rejector)) {
				return ancestor;
			}
			if (ancestor.hasFilledConfigChildSets(selector, rejector)) {
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
		if (ancestor != null) {
			inheritsPluralConfigChildren = ancestor.hasOrInheritsPluralConfigChildren(selector, rejector);
		}
		return hasPluralConfigChildren || inheritsPluralConfigChildren;
	}

	public String getTypeNameBase() {
		if (typeNameSeq <= 1) {
			return getSimpleName();
		} else {
			return getSimpleName() + typeNameSeq;
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

	public boolean isDeprecated() {
		return this.deprecationInfo != null;
	}

	@Override
	public String toString() {
		return fullName;
	}
}
