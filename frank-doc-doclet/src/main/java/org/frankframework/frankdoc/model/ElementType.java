/*
Copyright 2020, 2021, 2023 WeAreFrank!

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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Models a collection of FrankElement. The collection can be characterized by
 * a Java interface in the FF! sources, or there can be one member that is referenced
 * by its FrankElement. FrankElement objects that model an abstract Java class should
 * be omitted as members. This is done automatically when Spring is used to get the
 * implementing classes of a modeled Java interface.
 *
 * @author martijn
 */
public class ElementType implements Comparable<ElementType> {
	private static final Logger log = LogUtil.getLogger(ElementType.class);

	private static final String JAVADOC_DEFAULT_CLASSNAME = "@ff.defaultElement";

	private static final Comparator<ElementType> COMPARATOR =
		Comparator.comparing(ElementType::getSimpleName).thenComparing(ElementType::getFullName);

	private final @Getter(AccessLevel.PACKAGE) List<FrankElement> members;
	private final @Getter boolean fromJavaInterface;

	private static class InterfaceHierarchyItem {
		private final @Getter String fullName;
		private final @Getter String simpleName;
		private final @Getter Map<String, InterfaceHierarchyItem> parentInterfaces = new TreeMap<>();

		InterfaceHierarchyItem(FrankClass clazz) {
			this.fullName = clazz.getName();
			this.simpleName = clazz.getSimpleName();
			if (clazz.isInterface()) {
				for (FrankClass superInterface : clazz.getInterfaces()) {
					InterfaceHierarchyItem superInterfaceHierarchyItem = new InterfaceHierarchyItem(superInterface);
					parentInterfaces.put(superInterfaceHierarchyItem.getFullName(), superInterfaceHierarchyItem);
				}
			}
		}

		List<ElementType> findMatchingElementTypes(FrankDocModel model) {
			ElementType currentMatch = model.findElementType(fullName);
			if (currentMatch != null) {
				return List.of(currentMatch);
			}
			List<ElementType> result = new ArrayList<>();
			for (String parentKey : parentInterfaces.keySet()) {
				result.addAll(parentInterfaces.get(parentKey).findMatchingElementTypes(model));
			}
			return result;
		}
	}

	private final InterfaceHierarchyItem interfaceHierarchy;
	private @Getter List<ElementType> commonInterfaceHierarchy;
	private final @Getter String defaultElement;

	ElementType(FrankClass clazz, FrankClassRepository repository) {
		interfaceHierarchy = new InterfaceHierarchyItem(clazz);
		members = new ArrayList<>();
		this.fromJavaInterface = clazz.isInterface();
		this.defaultElement = parseDefaultElementTag(clazz, repository);
	}

	private static String parseDefaultElementTag(FrankClass clazz, FrankClassRepository repository) {
		String value = clazz.getJavaDocTag(JAVADOC_DEFAULT_CLASSNAME);
		if (StringUtils.isBlank(value)) {
			// null means the JavaDoc tag was not present - then nothing to do.
			if (value != null) {
				log.error("JavaDoc tag {} of interface [{}] should have a parameter", JAVADOC_DEFAULT_CLASSNAME, clazz.getName());
			}
			return null;
		}
		String result = null;
		try {
			FrankClass defaultClass = repository.findClass(value);
			result = defaultClass.getName();
		} catch (FrankDocException e) {
			log.error("JavaDoc tag {} on interface [{}] does not point to a valid class: [{}]", JAVADOC_DEFAULT_CLASSNAME, clazz.getName(), value, e);
		}
		return result;
	}

	public String getFullName() {
		return interfaceHierarchy.getFullName();
	}

	public String getSimpleName() {
		return interfaceHierarchy.getSimpleName();
	}

	// This is not about FrankDocGroups, but about groups in the XSDs.
	String getGroupName() {
		String result = getSimpleName();
		if (result.startsWith("I")) {
			result = result.substring(1);
		}
		return result;
	}

	void addMember(FrankElement member) {
		Utils.addToSortedListNonUnique(members, member);
	}

	void calculateCommonInterfaceHierarchy(FrankDocModel model) {
		commonInterfaceHierarchy = new ArrayList<>();
		commonInterfaceHierarchy.add(this);
		ElementType nextCandidate = commonInterfaceHierarchy.get(commonInterfaceHierarchy.size() - 1).getNextCommonInterface(model);
		while (nextCandidate != null) {
			commonInterfaceHierarchy.add(nextCandidate);
			nextCandidate = commonInterfaceHierarchy.get(commonInterfaceHierarchy.size() - 1).getNextCommonInterface(model);
		}
		if (log.isTraceEnabled()) {
			String commonInterfaceHierarchyStr = commonInterfaceHierarchy.stream().map(ElementType::getFullName).collect(Collectors.joining(", "));
			log.trace("ElementType [{}] has common interface hierarchy [{}]", this.getFullName(), commonInterfaceHierarchyStr);
		}
	}

	public ElementType getHighestCommonInterface() {
		return commonInterfaceHierarchy.get(commonInterfaceHierarchy.size() - 1);
	}

	private ElementType getNextCommonInterface(FrankDocModel model) {
		if (!fromJavaInterface) {
			return null;
		}
		List<ElementType> candidates = new ArrayList<>();
		for (String key : interfaceHierarchy.getParentInterfaces().keySet()) {
			candidates.addAll(interfaceHierarchy.getParentInterfaces().get(key).findMatchingElementTypes(model));
		}
		if (candidates.isEmpty()) {
			return null;
		} else {
			ElementType result = candidates.get(0);
			if (candidates.size() >= 2) {
				log.error("There are multiple candidates for the next common interface of ElementType [{}], which are [{}]. Chose [{}]",
					this::getFullName, () -> candidates.stream().map(ElementType::getFullName).collect(Collectors.joining(", ")), result::getFullName);
			}
			return result;
		}
	}

	/**
	 * Get the members that can be referenced with syntax 2. Only non-abstracts are returned.
	 */
	public List<FrankElement> getSyntax2Members() {
		return members.stream()
			.filter(frankElement -> !frankElement.getXmlElementNames().isEmpty())
			.sorted()
			.collect(Collectors.toList());
	}

	@Override
	public int compareTo(ElementType other) {
		return COMPARATOR.compare(this, other);
	}

	@Override
	public String toString() {
		return "ElementType " + interfaceHierarchy.getFullName();
	}
}
